package scanner
import scanner.PortsScanner.*
import utils.*
import utils.Parser.parseCIDR
import recon.OS.*
import recon.Services.*
import utils.HostInfoLogger.*
import utils.MsgLogger.*
import utils.ResultsManager.{handleResults, *}

import scala.concurrent.ExecutionContext.Implicits.global
import java.net.InetAddress
import scala.concurrent.Future
import scala.concurrent.Future.sequence

sealed trait Status:
  def ip: String

case class up(ip: String) extends Status
case class down(ip: String) extends Status

case class Result(
                   ip: String,
                   ports: Option[Seq[Int]] = None,
                   services: Map[Int, Option[String]] = Map.empty,
                   os: Option[String] = None,
                   status: Status
                 )

object HostScanner:

  def scan(input: String, config: Config, isSubnet: Boolean): Future[Unit] =
    if (isSubnet)
      val (netId, firstIP, lastIP) = parseCIDR(input)
      info(s"Scanning subnet $netId.$firstIP-$lastIP...")
      val range = (firstIP to lastIP).map(i => s"$netId.$i")
      val scans = range.map(ip => getScanResults(ip, config))
      sequence(scans).map: results =>
        handleResults(results, config, totalHosts = range.size)
    else
      getScanResults(input, config).map: result =>
        handleResults(Seq(result), config, totalHosts = 1)

  private def getScanResults(ip: String, config: Config): Future[Result] =
    for
      hostStatus <- ping(ip)
      _ = printHostStatus(hostStatus, config)
      result <- hostStatus match
        case up(ip) =>
          for
            openPorts <- scanPorts(ip)
            osName = detectOS(ip)
            services =
              if (config.showServices) openPorts.map(p => p -> recognizeService(ip, p)).toMap
              else Map.empty
            /* high scalability, add here more info ...
            ... */
          yield Result(ip = ip, ports = Some(openPorts), services = services, os = osName, status = hostStatus)
        case down(ip) =>
          Future.successful(Result(ip, None, Map.empty, None, hostStatus))
    yield result

  private def ping(ip: String): Future[Status] =
    val timeout = 500
    val address = InetAddress.getByName(ip)
    Future:
      if (address.isReachable(timeout)) up(ip)
      else down(ip)