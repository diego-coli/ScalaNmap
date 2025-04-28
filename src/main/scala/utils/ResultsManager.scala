package utils
import recon.Services.*
import java.nio.file.*
import scanner.*
import utils.Logger.*
import java.nio.file.Files.*
import Paths.*
import recon.OS.*
import scanner.PortsScanner.*
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.sequence

object ResultsManager:

  def resultsManagement(hostsUp: Seq[String], config: Config, totalHosts: Int): Unit =
    if (config.showOpenPorts || config.detectOS || config.saveOnFile)
      val scannedPorts = hostsUp.map: ip =>
        scanPorts(ip).map: openPorts =>
          printPorts(ip, openPorts, config)
          printOS(Seq(ip), config)
          (ip, openPorts)
      sequence(scannedPorts).map: hostsAndPorts =>
        save(hostsAndPorts, config)
    printActiveOutOfTotal(hostsUp.size, totalHosts)

  def printActiveHosts(hostsUp: Seq[String]): Unit =
    if (hostsUp.nonEmpty)
      success("\nActive hosts:")
      hostsUp.foreach(ip => success(s" - $ip"))
    else
      error("\nNo active hosts found.")

  def printHostStatus(host: Result, config: Config, range: Boolean = false): Unit =
    if (config.verboseMode || !range)   // !range because if it's a single host it should print UP or DOWN anyway
      host match
        case up(ip)   => success(s"Host UP: $ip")
        case down(ip) => warn(s"Host DOWN: $ip")

  def printOS(hostsUp: Seq[String], config: Config): Unit =
    if (hostsUp.nonEmpty && config.detectOS)
      hostsUp.foreach(ip => detectOS(ip))

  private def printPorts(ip: String, openPorts: Seq[Int], config: Config): Unit =
    if (config.showOpenPorts)
      if (openPorts.isEmpty) warn(s"No open ports found on $ip.")
      else
        success(s"Open ports on $ip:")
        openPorts.foreach{
          port =>
            val service: String = printService(ip, port, config)
            success(s"$port\t$service")
        }
    else if (!config.showOpenPorts && openPorts.nonEmpty) info("Open ports found! Re-run with -P to see them.")

  private def printActiveOutOfTotal(active: Int, total: Int): Unit =
    val msg = s"\nFinished: $active active hosts out of $total."
    if (active > 0) success(msg) else error(msg)

  private def printService(ip: String, port: Int, config: Config): String =
    if (config.showServices) recognizeService(ip, port)
    else ""

  private def save(results: Seq[(String, Seq[Int])], config: Config): Unit =
    if (config.saveOnFile)
      val res = results.map:
        case (ip, ports) =>
          if (config.showOpenPorts && ports.nonEmpty)
            val portInfo = ports.map: port =>
              val service = if (config.showServices) recognizeService(ip, port) else ""
              if (service.nonEmpty) s"$port ($service)" else s"$port"
            .mkString(", ")
            s"Host: $ip\nOpen ports: $portInfo"
          else
            s"Host: $ip"

      val outputPath = get("results.txt")
      write(outputPath, res.mkString("\n").getBytes)
      info(s"\nActive hosts${
        if (config.showOpenPorts) ", open ports"
        else ""
      }${
        if (config.showServices) " and services"
        else ""
      } saved in: ${outputPath.toAbsolutePath}")
