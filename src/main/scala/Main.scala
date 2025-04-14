import utils.Logger
import scanner.*

import scala.concurrent.Future

object Main:

  val defaultIP = "192.168.56"
  val startRange = 1
  val endRange = 20
  val defaultPortsToScan: Seq[Int] = 1 to 65535

  case class defaultConfig(showOpen: Boolean = true, verbose: Boolean = false)

  def main(args: Array[String]) =
    val (ipArg, flags) = args.partition(!_.startsWith("--"))
    val netId = ipArg.headOption match
      case Some(netId) => netId
      case None     => defaultIP

    Logger.info(s"Starting ScalaNmap on subnet: $netId.$startRange-$endRange")

    val scanning = for
      hostsUp <- discoverHostsUp(netId)
      hostsUpPorts <- scanAllHosts(hostsUp)
    yield()


private def discoverHostsUp(ip: String): Future[Seq[String]] = ???

private def scanAllHosts(hostsUp: Seq[String]) = ???