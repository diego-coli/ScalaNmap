package utils
import java.nio.file.{Files, Paths}
import scanner.*

object ResultsManager:

  def saveResults(hosts: Seq[String]): Unit =
    val outputPath = java.nio.file.Paths.get("results.txt")
    java.nio.file.Files.write(outputPath, hosts.mkString("\n").getBytes)
    Logger.info(s"\nActive hosts saved in: ${outputPath.toAbsolutePath}")

  def printActiveHosts(hostsUp: Seq[String]): Unit =
    if (hostsUp.nonEmpty)
      Logger.success("\nActive hosts:")
      hostsUp.foreach(ip => Logger.success(s" - $ip"))
    else
      Logger.error("\nNo active hosts found.")

  def printHostStutus(result: Result, verbose: Boolean = false): Unit =
    if (verbose)
      result match
        case up(ip)   => Logger.success(s"${if verbose then "[VERBOSE] " else ""}Host UP: $ip")
        case down(ip) => Logger.warn(s"${if verbose then "[VERBOSE] " else ""}Host DOWN: $ip")

  def printPortStatus(ip: String, openPorts: Seq[Int], verbose: Boolean = false): Unit =
    if (openPorts.isEmpty)
      if (verbose) Logger.warn(s"No open ports on $ip.")
    else
      Logger.success(s"Open ports on $ip:")
      openPorts.foreach(port => Logger.success(s"$port"))

  def printActiveOutOfTotal(active: Int, total: Int): Unit =
    val msg = s"\nFinished: $active active hosts out of $total."
    if (active > 0) Logger.success(msg) else Logger.error(msg)