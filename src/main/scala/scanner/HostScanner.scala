package scanner
import utils.{Config, Logger, Parser, ResultsManager}
import java.net.InetAddress
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait Result:
  def ip: String

case class up(ip: String) extends Result
case class down(ip: String) extends Result

object HostScanner:

  def pingHost(ip: String): Future[Result] =
    val timeout = 500
    val address = InetAddress.getByName(ip)
    Future:
      if (address.isReachable(timeout)) up(ip)
      else down(ip)

  def pingRange(netId: String, first: Int, last: Int, config: Config): Future[Seq[Result]] =
    val range = (first to last).map(i => s"$netId.$i")
    val futures = range.map: ip =>
      pingHost(ip).map: result =>
        ResultsManager.printHostStutus(result, config.verboseMode)
        result
    Future.sequence(futures)

  def scanHost(ip: String, config: Config): Unit =
    pingHost(ip).map: result =>
      ResultsManager.printHostStutus(result, verbose = true)
      result match
        case up(ip) =>
          PortsScanner.scanPorts(ip).map: openPorts =>
              ResultsManager.printPortStatus(ip, openPorts, config.showOpenPorts)
              if (config.saveOnFile && result.isInstanceOf[up]) ResultsManager.saveResults(Seq(result.ip))
        case _ => ()

  def scanRange(cidrInput: String, config: Config): Unit =
    val (netId, firstIP, lastIP) = Parser.parseCIDR(cidrInput)
    Logger.info(s"Scanning subnet $netId.$firstIP-$lastIP...")
    pingRange(netId, firstIP, lastIP, config).map: results =>
      val hostsUp = results.collect:
        case up(ip) => ip
      if (!config.verboseMode) ResultsManager.printActiveHosts(hostsUp)
      if (config.saveOnFile) ResultsManager.saveResults(hostsUp)
      val ports = hostsUp.map: ip =>
        PortsScanner.scanPorts(ip).map: openPorts =>
          ResultsManager.printPortStatus(ip, openPorts, config.showOpenPorts)
      Future.sequence(ports)
      ResultsManager.printActiveOutOfTotal(hostsUp.size, results.size)