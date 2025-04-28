package recon
import scala.sys.process.*
import scala.util.Try
import utils.Logger.*

object OS:

  def detectOS(ip: String): Unit =
    getTTL(ip) match
      case Some(ttl) =>
        ttl match
          case 64 => info(s"Host: $ip | OS detected: Linux / MacOS")
          case 128 => info(s"Host: $ip | OS detected: Windows")
          case 255 => info(s"Host: $ip | OS detected: Cisco Router / Network device")
          case _ => warn(s"Host: $ip | Unknown OS (TTL: " + ttl + ")")
      case None => error(s"Host: $ip | Unable to determine OS")

  private def getTTL(ip: String): Option[Int] =
    val command =
      if (System.getProperty("os.name").toLowerCase.contains("win")) s"ping -n 1 $ip"
      else s"ping -c 1 $ip"
    val result = Try(command.!!).toOption
    result.flatMap: output =>
      val ttlPattern = """ttl=(\d+)""".r
      ttlPattern.findFirstMatchIn(output).map: m =>
        m.group(1).toInt