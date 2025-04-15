package scanner
import utils.Config
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
          case up(ip)   => println(s"[VERBOSE] Host UP: $ip")
          case down(ip) => println(s"[VERBOSE] Host DOWN: $ip")
        result
    Future.sequence(futures)