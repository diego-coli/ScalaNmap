package utils
import java.nio.file.{Files, Paths}
import scanner.*

object ResultsManager:

  def saveResults(results: Seq[(String, Seq[Int])], includePorts: Boolean = false, includeServices: Boolean = false): Unit =
    val res = results.map:
      case (ip, ports) =>
        if (includePorts && ports.nonEmpty)
          val portInfo = ports.map: port =>
            val service = if (includeServices) ServiceRecon.recognize(ip, port) else ""
            if (service.nonEmpty) s"$port ($service)" else s"$port"
          .mkString(", ")
          s"Host: $ip\nOpen ports: $portInfo"
        else
          s"Host: $ip"

    val outputPath = Paths.get("results.txt")
    Files.write(outputPath, res.mkString("\n").getBytes)
    Logger.info(s"\nActive hosts${
      if (includePorts) ", open ports"
      else ""
    }${
      if (includeServices) " and services"
      else ""
    } saved in: ${outputPath.toAbsolutePath}")

  def printActiveHosts(hostsUp: Seq[String]): Unit =
    if (hostsUp.nonEmpty)
      Logger.success("\nActive hosts:")
      hostsUp.foreach(ip => Logger.success(s" - $ip"))
    else
      Logger.error("\nNo active hosts found.")

  def printHosts(result: Result, verbose: Boolean = false): Unit =
    if (verbose)
      result match
        case up(ip)   => Logger.success(s"Host UP: $ip")
        case down(ip) => Logger.warn(s"Host DOWN: $ip")

  def printPorts(ip: String, openPorts: Seq[Int], showOpenPorts: Boolean = false, showServices: Boolean = false): Unit =
    if (showOpenPorts)
      if (openPorts.isEmpty) Logger.warn(s"No open ports found on $ip.")
      else
        Logger.success(s"Open ports on $ip:")
        openPorts.foreach{
          port =>
            val service: String = printService(ip, port, showServices)
            Logger.success(s"$port\t$service")
        }
    else if (!showOpenPorts && openPorts.nonEmpty) Logger.info("Open ports found! Re-run with -O to see them.")

  def printActiveOutOfTotal(active: Int, total: Int): Unit =
    val msg = s"\nFinished: $active active hosts out of $total."
    if (active > 0) Logger.success(msg) else Logger.error(msg)

  private def printService(ip: String, port: Int, showServices: Boolean = false): String =
    if (showServices) ServiceRecon.recognize(ip, port)
    else ""