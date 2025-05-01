package utils
import scanner.*
import utils.HostInfoLogger.*
import utils.MsgLogger.*
import java.nio.file.Files.*
import java.nio.file.Paths.*

object ResultsManager:

  def handleResults(results: Seq[Result], config: Config, totalHosts: Int): Unit =
    // get and print active hosts
    val activeHosts = results.filter(_.status.isInstanceOf[up])
    if (totalHosts > 1) printActiveHosts(activeHosts.map(_.ip))
    // for each active host, print info
    activeHosts.foreach: host =>
      val ip = host.ip
      // open ports
      host.ports match
        case Some(ports) if ports.nonEmpty =>
          printOpenPorts(ip, ports, config, host)
        case _ =>
          if (config.showOpenPorts) warn(s"No open ports found on $ip.")
      // operating system
      if (config.detectOS)
        host.os match
          case Some(name) => info(s"Host: $ip | OS detected: $name")
          case None       => warn(s"Host: $ip | OS not detected")
      // save on file
      if (config.saveOnFile)
        val formattedResults = save(activeHosts, config)
        val path = get("results.txt")
        write(path, formattedResults.getBytes)
        info(s"\nResults saved in: ${path.toAbsolutePath}")
    // recap
    printActiveOutOfTotal(activeHosts.size, totalHosts)

  private def save(results: Seq[Result], config: Config): String =
    results.map(formatResult).mkString("\n")

  private def formatResult(res: Result): String =
    val portsStr = formatPorts(res.ports, res.services)
    val osStr    = res.os.getOrElse("Something went wrong. OS or ttl not detected.")
    /* high scalability, add here what you wanna save on file ...
    ... */
    s"Host: ${res.ip}\nOpen ports: $portsStr\nOS: $osStr\n"

  private def formatPorts(portsOpt: Option[Seq[Int]], services: Map[Int, Option[String]]): String =
    portsOpt match
      case Some(ports) if ports.nonEmpty =>
        ports.map { port =>
          services.get(port).flatten match
            case Some(service) => s"$port ($service)"
            case None          => s"$port"
        }.mkString(", ")
      case _ => "No open ports"
