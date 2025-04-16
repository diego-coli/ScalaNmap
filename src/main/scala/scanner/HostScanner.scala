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
    Future:
      InetAddress.getByName(ip) match
            case _ if InetAddress.getByName(ip).isReachable(timeout) => up(ip)
            case _ => down(ip)

  def pingRange(netId: String, start: Int, end: Int, config: Config): Future[Seq[Result]] =
    val range = (start to end).map(i => s"$netId.$i")
    val futures = range.map: ip =>
      pingHost(ip).map: result =>
        if (config.verboseMode) result match
          case up(ip)   => Logger.success(s"[VERBOSE] Host UP: $ip")
          case down(ip) => Logger.warn(s"[VERBOSE] Host DOWN: $ip")
        result
    Future.sequence(futures)

  def scanHost(ip: String, config: Config): Unit =
    pingHost(ip).map:
      case up(ip) =>
        Logger.success(s"\n$ip: Host UP")
        if (config.saveOnFile) ResultsManager.saveResults(Seq(ip))
      case down(ip) => Logger.warn(s"\n$ip: Host DOWN")

  def scanRange(cidrInput: String, config: Config): Unit =
    val (netId, firstIP, lastIP) = Parser.parseCIDR(cidrInput)
    Logger.info(s"Scanning subnet $netId.$firstIP-$lastIP...")
    pingRange(netId, firstIP, lastIP, config).map: results =>
      val hostsUp = results.collect { case up(ip) => ip }
      if (!config.verboseMode) ResultsManager.printResults(hostsUp)
      if (config.saveOnFile) ResultsManager.saveResults(hostsUp)
      val (totCount, hostsUpCount) = (results.size, hostsUp.size)
      val msg = s"\nFinished: $hostsUpCount active hosts out of $totCount."
      if (hostsUpCount > 0) Logger.success(msg)
      else Logger.error(msg)