package scanner
import scanner.PortsScanner.*
import utils.Logger.*
import utils.Parser.*
import utils.ResultsManager.*
import utils.{Config, Parser}
import java.net.InetAddress
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait Result:
  def ip: String

case class up(ip: String) extends Result
case class down(ip: String) extends Result

object HostScanner:

  def scanHost(ip: String, config: Config): Unit =
    pingHost(ip).map: result =>
      printHosts(result, verbose = true)
      result match
        case up(ip) =>
          scanPorts(ip).map: openPorts =>
              printPorts(ip, openPorts, config.showOpenPorts, config.showServices)
              if (config.saveOnFile) saveResults(Seq(ip -> openPorts), config.showOpenPorts, config.showServices)
        case _ => ()

  def scanRange(cidrInput: String, config: Config): Unit =
    val (netId, firstIP, lastIP) = parseCIDR(cidrInput)
    info(s"Scanning subnet $netId.$firstIP-$lastIP...")
    pingRange(netId, firstIP, lastIP, config).flatMap: results =>
      val hostsUp = extractActiveHosts(results)
      if (!config.verboseMode) printActiveHosts(hostsUp)
      if (config.showOpenPorts || config.saveOnFile)
        showPortsAndSave(hostsUp, config, results.size)
      else
        printActiveOutOfTotal(hostsUp.size, results.size)
        Future.unit

  private def pingHost(ip: String): Future[Result] =
    val timeout = 500
    val address = InetAddress.getByName(ip)
    Future:
      if (address.isReachable(timeout)) up(ip)
      else down(ip)

  private def pingRange(netId: String, first: Int, last: Int, config: Config): Future[Seq[Result]] =
    val range = (first to last).map(i => s"$netId.$i")
    val futures = range.map: ip =>
      pingHost(ip).map: result =>
        printHosts(result, config.verboseMode)
        result
    Future.sequence(futures)

  private def extractActiveHosts(results: Seq[Result]): Seq[String] =
    results.collect { case up(ip) => ip }

  private def showPortsAndSave(hostsUp: Seq[String], config: Config, totalHosts: Int) = {
    val scannedPorts = hostsUp.map: ip =>
      scanPorts(ip).map(openPorts =>
        printPorts(ip, openPorts, config.showOpenPorts, config.showServices)
        (ip, openPorts)
      )
    Future.sequence(scannedPorts).map: hostsAndPorts =>
      if (config.saveOnFile) saveResults(hostsAndPorts, config.showOpenPorts, config.showServices)
      printActiveOutOfTotal(hostsUp.size, totalHosts)
  }