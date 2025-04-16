package utils
import java.nio.file.{Files, Paths}

object ResultsManager:

  def saveResults(hosts: Seq[String]) =
    val outputPath = java.nio.file.Paths.get("scanned_hosts.txt")
    java.nio.file.Files.write(outputPath, hosts.mkString("\n").getBytes)
    Logger.info(s"\nActive hosts saved in: ${outputPath.toAbsolutePath}")

  def printResults(hostsUp: Seq[String]) =
    if (hostsUp.nonEmpty)
      Logger.success("\nActive hosts:")
      hostsUp.foreach(ip => Logger.success(s" - $ip"))
    else
      Logger.error("\nNo active hosts found.")