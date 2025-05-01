package utils
import scanner.*
import utils.HostInfoLogger.*
import utils.MsgLogger.*
import java.nio.file.Files.*
import java.nio.file.Paths.*

case class Result(
                   ip: String,
                   mac: Option[String] = None,
                   ports: Option[Seq[Int]] = None,
                   services: Map[Int, Option[String]] = Map.empty,
                   os: Option[String] = None,
                   status: Status
                 )

object ResultsManager:

  def handleResults(results: Seq[Result], config: Config, totalHosts: Int): Unit =
    val activeHosts = results.filter(_.status.isInstanceOf[up])
    if (totalHosts > 1) printActiveHosts(activeHosts.map(_.ip))
    activeHosts.foreach: host =>
      handleMACResults(config, host)
      handlePortsResults(config, host)
      handleOSResults(config, host)
      handleSave(config, activeHosts)
      /* high scalability, add here more functions ...
      ... */
    // recap
    printActiveOutOfTotal(activeHosts.size, totalHosts)

  private def handleMACResults(config: Config, host: Result): Unit =
    val ip = host.ip
    host.mac match
      case Some(mac) => success(s"MAC address of ${host.ip}: $mac")
      case _ => warn(s"MAC address not found on $ip.")

  private def handlePortsResults(config: Config, host: Result): Unit =
    val ip = host.ip
    host.ports match
      case Some(ports) if ports.nonEmpty =>
        printOpenPorts(host, ports, config)
      case _ =>
        if (config.showOpenPorts) warn(s"No open ports found on $ip.")

  private def handleOSResults(config: Config, host: Result): Unit =
    if (config.detectOS)
      val ip = host.ip
      host.os match
        case Some(name) => info(s"Host: $ip | OS detected: $name")
        case None => warn(s"Host: $ip | OS not detected")

  private def handleSave(config: Config, activeHosts: Seq[Result]): Unit =
    if (config.saveOnFile)
      val formattedResults = save(activeHosts, config)
      val path = get("results.txt")
      write(path, formattedResults.getBytes)
      info(s"\nResults saved in: ${path.toAbsolutePath}")

  private def save(results: Seq[Result], config: Config): String =
    results.map(res => formatResult(res, config)).mkString("\n")

  private def formatResult(host: Result, config: Config): String =
    val builder = new StringBuilder()
    // save IP on file
    builder.append(s"Host: ${host.ip}")
    // save MAC on file
    builder.append(s"\nMAC address: ${host.mac.getOrElse("Something went wrong.")}")
    // save open ports on file (if requested)
    if (config.showOpenPorts) builder.append(s"\nOpen ports: ${formatPorts(host.ports, host.services, config)}")
    // save operating system on file (if requested)
    if (config.detectOS) builder.append(s"\nOS: ${host.os.getOrElse("Something went wrong.")}")
    /* high scalability, add here what you wanna save on file ...
    ... */
    builder.append("\n").toString()

  private def formatPorts(portsOpt: Option[Seq[Int]], services: Map[Int, Option[String]], config: Config): String =
    portsOpt match
      case Some(ports) if ports.nonEmpty =>
        ports.map { port =>
          services.get(port).flatten match
            case Some(service) => s"$port ($service)"
            case None          => s"$port"
        }.mkString(", ")
      case _ => "No open ports"