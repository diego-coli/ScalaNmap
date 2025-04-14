import utils.Logger
import scanner.*

import scala.concurrent.Future
import scala.util.matching.Regex

object Main:

  val ipRegex: Regex = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$""".r
  val cidrRegex: Regex = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)/([12]?[0-9]|3[0-2])$""".r
  val defaultIP = "192.168.56"
  val startRange = 1
  val endRange = 20
  val defaultPortsToScan: Seq[Int] = 1 to 65535

  case class defaultConfig(showOpen: Boolean = true, verbose: Boolean = false)

  def main(args: Array[String]): Unit = args match
    case _ if args.length == 1 =>
      val input = args(0)
      input match
        case ipRegex(_*) =>
          val (netId: String, last: Int) = parseIp(input)
          HostsScanner.scan(netId = netId, start = last, end = last)
        case cidrRegex(_*) =>
          val (netId, start, end) = parseCIDR(input)
          HostsScanner.scan(netId, start, end)
        case _ =>
          Logger.info(s"IP Address format not valid, retry.")


private def discoverHostsUp(ip: String): Future[Seq[String]] = ???

private def scanAllHosts(hostsUp: Seq[String]) = ???

private def parseIp(input: String): (String, Int) =
  val octets = input.split("\\.")
  val netId = octets.take(3).mkString(".")
  val last = octets(3).toInt
  (netId, last)

private def parseCIDR(cidr: String): (String, Int, Int) =
  val Array(network, mask) = cidr.split("/")
  val maskLength = mask.toInt
  val netParts = network.split("\\.").map(_.toInt)
  val netId = netParts.take(3).mkString(".")
  val start = 1
  val end = if (maskLength == 24) 254 else 255
  (netId, start, end)


