package utils
import java.nio.file.{Files, Paths}
import scanner.*

object ResultsManager:

  def saveResults(results: Seq[(String, Seq[Int])], includePorts: Boolean = false): Unit =
    val res = results.map:
      case (ip, ports) =>
        if (includePorts && ports.nonEmpty) s"Host: $ip | Open ports: ${ports.mkString(", ")}"
        else s"Host: $ip"
    val outputPath = java.nio.file.Paths.get("results.txt")
    java.nio.file.Files.write(outputPath, res.mkString("\n").getBytes)
    Logger.info(s"\nActive hosts${if includePorts then " and open ports" else ""} saved in: ${outputPath.toAbsolutePath}")

  def printActiveHosts(hostsUp: Seq[String]): Unit =
    if (hostsUp.nonEmpty)
      Logger.success("\nActive hosts:")
      hostsUp.foreach(ip => Logger.success(s" - $ip"))
    else
      Logger.error("\nNo active hosts found.")

  def printHostStutus(result: Result, verbose: Boolean = false): Unit =
    if (verbose)
      result match
        case up(ip)   => Logger.success(s"Host UP: $ip")
        case down(ip) => Logger.warn(s"Host DOWN: $ip")

  def printPortStatus(ip: String, openPorts: Seq[Int], showOpenPorts: Boolean = false): Unit =
    if (showOpenPorts)
      if (openPorts.isEmpty) Logger.warn(s"No open ports found on $ip.")
      else
        Logger.success(s"Open ports on $ip:")
        openPorts.foreach(port => Logger.success(s"$port"))
    else if (!showOpenPorts && openPorts.nonEmpty) Logger.info("Open ports found! Re-run with -O to see them.")

  def printActiveOutOfTotal(active: Int, total: Int): Unit =
    val msg = s"\nFinished: $active active hosts out of $total."
    if (active > 0) Logger.success(msg) else Logger.error(msg)

  private def printServiceInfo(ip: String, port: Int, showServices: Boolean = false): Unit =
    if (showServices)
      ServiceRecon.recognize(ip, port)
