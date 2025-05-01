package recon
import scala.sys.process.*
import scala.util.Try

object OS:

  def detectOS(ip: String): Option[String] =
    getTTL(ip) match
      case Some(ttl) => ttl match
        case 64  => Some("Linux / MacOS")
        case 128 => Some("Windows")
        case 255 => Some("Cisco Router / Network device")
        case _   => Some(s"Unknown (TTL: $ttl)")
      case None => None

  private def getTTL(ip: String): Option[Int] =
    val command =
      if (System.getProperty("os.name").toLowerCase.contains("win")) s"ping -n 1 $ip"
      else s"ping -c 1 $ip"
    val result = Try(command.!!).toOption
    result.flatMap: output =>
      val ttlPattern = """(?i)ttl[=\s](\d+)""".r
      ttlPattern.findFirstMatchIn(output).map: m =>
        m.group(1).toInt