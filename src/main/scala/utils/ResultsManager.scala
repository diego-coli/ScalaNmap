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
        val formatted = save(activeHosts, config)
        val path = get("results.txt")
        write(path, formatted.getBytes)
        info(s"\nResults saved in: ${path.toAbsolutePath}")
    // recap
    printActiveOutOfTotal(activeHosts.size, totalHosts)

  private def save(results: Seq[Result], config: Config): String =
    results.map: res =>
      val portsStr = res.ports match
        case Some(ports) if ports.nonEmpty =>
          ports.map: port =>
            val svc = res.services.getOrElse(port, None).getOrElse("")
            if svc.nonEmpty then s"$port ($svc)" else s"$port"
          .mkString(", ")
        case _ => "No open ports"
      val osStr = res.os.getOrElse("Unknown OS")
      s"Host: ${res.ip}\nOpen ports: $portsStr\nOS: $osStr\n"
    .mkString("\n")