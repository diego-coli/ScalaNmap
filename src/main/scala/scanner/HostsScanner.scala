package scanner
import scanner.PortsScanner.*
import utils.Logger.*
import utils.Parser.*
import utils.ResultsManager.*
import utils.*
import java.net.InetAddress
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.*

sealed trait Status:
  def ip: String

case class up(ip: String) extends Status
case class down(ip: String) extends Status

object HostsScanner:

  def singleScan(ip: String, config: Config): Unit =
    ping(ip).map: host =>
      printHostStatus(host, config)
      host match
        case up(ip) =>
          scanPorts(ip).map: openPorts =>
            resultsManagement(hostsUp = Seq(ip), config, totalHosts = 1)
        case _ => ()

  def subnetScan(ip: String, config: Config): Unit =
    val (netId, firstIP, lastIP) = parseCIDR(ip)
    info(s"Scanning subnet $netId.$firstIP-$lastIP...")
    val range = (firstIP to lastIP).map(i => s"$netId.$i")
    val futures = range.map: ip =>
      ping(ip).map: host =>
        printHostStatus(host, config, range = true)
        host
    sequence(futures).flatMap: totalHosts =>
      val hostsUp = extractActiveHosts(totalHosts)
      printActiveHosts(hostsUp)
      resultsManagement(hostsUp, config, totalHosts.size)
      Future.unit

  private def ping(ip: String): Future[Status] =
    val timeout = 500
    val address = InetAddress.getByName(ip)
    Future:
      if (address.isReachable(timeout)) up(ip)
      else down(ip)

  private def extractActiveHosts(hosts: Seq[Status]): Seq[String] =
    hosts.collect:
      case up(ip) => ip