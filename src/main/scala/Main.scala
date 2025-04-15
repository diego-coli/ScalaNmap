import utils.*
import scanner.*
import scala.concurrent.Future
import scala.util.matching.Regex
import scala.concurrent.ExecutionContext.Implicits.global
import java.nio.file.{Files, Paths}

object Main:

  val ipRegex: Regex = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$""".r
  val cidrRegex: Regex = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)/([12]?[0-9]|3[0-2])$""".r

  def main(args: Array[String]): Unit =
    if args.isEmpty then
      Logger.info("No argument given.")
    else
      val (ipArgs, flags) = args.partition(arg => !arg.startsWith("-"))
      val input = ipArgs.head
      val config: Config = Parser.parseFlags(flags)

      input match
        case ipRegex(_*) =>
          HostsScanner.pingHost(input).map:
            case up(ip) =>
              println("\nHost up")
            case down(ip) =>
              println("\nHost down")

        case cidrRegex(_*) =>
          val (netId, start, end) = Parser.parseCIDR(input)
          println(s"Scanning subnet $netId.$start-$end...")

          HostsScanner.pingRange(netId, start, end).map: results =>
            val hostsUp = results.collect { case up(ip) => ip }

            if hostsUp.nonEmpty then
              println("\nActive hosts:")
              hostsUp.foreach(ip => println(s" - $ip"))

              if config.saveOnFile then
                val outputPath = java.nio.file.Paths.get("scanned_hosts.txt")
                java.nio.file.Files.write(outputPath, hostsUp.mkString("\n").getBytes)
                println(s"\nActive hosts saved in: ${outputPath.toAbsolutePath}")
            else
              println("\nNo active hosts found.")

        case _ =>
          Logger.info(s"IP Address format not valid, retry.")