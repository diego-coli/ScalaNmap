package utils
import scanner.*
import utils.MsgLogger.*

object HostInfoLogger:

  def printHostStatus(host: Status, config: Config, range: Boolean = false): Unit =
    if (config.verboseMode || !range) // !range because if it's a single host it should print UP or DOWN anyway
      host match
        case up(ip) => success(s"Host UP: $ip")
        case down(ip) => warn(s"Host DOWN: $ip")

  def printActiveHosts(hostsUp: Seq[String]): Unit =
    if (hostsUp.nonEmpty)
      success("\nActive hosts:")
      hostsUp.foreach(ip => success(s" - $ip"))
    else
      error("\nNo active hosts found.")

  def printOpenPorts(host: Result, openPorts: Seq[Int], config: Config): Unit =
    if (config.showOpenPorts)
      val ip = host.ip
      if (openPorts.isEmpty) warn(s"No open ports found on $ip.")
      else
        success(s"Open ports on $ip:")
        openPorts.foreach: port =>
          val service = host.services.getOrElse(port, None).getOrElse("")
          success(s"$port\t$service")
        if (!config.showServices) info("Wanna see which services are running? Re-run with -serv to see them.")
    else if (!config.showOpenPorts && openPorts.nonEmpty)
      info("Open ports found! Re-run with -open to see them.")
  
  def printActiveOutOfTotal(active: Int, total: Int): Unit =
    val msg = s"Finished: $active active hosts out of $total.\n\n"
    if (active > 0) success(msg) else error(msg)
