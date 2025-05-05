package scanner
import recon.Address
import recon.Address.*
import scanner.PortsScanner.*
import utils.*
import utils.Parser.*
import recon.OS.*
import recon.Services.*
import utils.HostInfoLogger.*
import utils.MsgLogger.*
import utils.ResultsManager.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Future.sequence

sealed trait Status:
  def ip: String

case class up(ip: String) extends Status
case class down(ip: String) extends Status

object HostScanner:

  def scan(input: String, config: Config, isSubnet: Boolean): Future[Unit] =
    if (isSubnet)
      val subnet = parseCIDR(input)
      info(s"Scanning subnet ${subnet.firstIp} to ${subnet.lastIp}...")
      val firstIp = ipToInt(subnet.firstIp)
      val lastIp = ipToInt(subnet.lastIp)
      val range = (firstIp to lastIp).map(intToIp)
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
            mac <- getMac(ip)
            hostName = getHostname(ip)
            openPorts <- scanPorts(ip)
            osName = detectOS(ip)
            services = if (config.showServices) openPorts.map(p => p -> recognizeService(ip, p)).toMap
                       else Map.empty
            /* high scalability, add here more info ...
            ... */
          yield Result(ip = ip, mac = mac, ports = Some(openPorts), services = services, os = osName, hostName = hostName, status = hostStatus)
        case down(ip) =>
          Future.successful(Result(ip, None, None, Map.empty, None, None, hostStatus))
    yield result

