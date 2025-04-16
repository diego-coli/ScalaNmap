import utils.*
import scanner.*
import scala.util.matching.Regex
import scala.concurrent.ExecutionContext.Implicits.global
import java.nio.file.{Files, Paths}

object Main:

  val ipRegex: Regex = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$""".r
  val cidrRegex: Regex = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)/([12]?[0-9]|3[0-2])$""".r

  def main(args: Array[String]): Unit =
    Logger.welcomeMessage()

    if (args.isEmpty) Logger.warn("No argument provided")
    else
      val (inputOpt, config) = Parser.parseInputAndConfig(args)
      if (config.showHelp) Logger.help
      else inputOpt match
          case Some(input) =>
            input match
              case ipRegex(_*) => // single host scan
                  HostScanner.pingHost(input).map:
                    case up(ip) =>
                      Logger.success(s"\n$input: Host UP")
                    case down(ip) =>
                      Logger.warn(s"\n$input: Host DOWN")
              case cidrRegex(_*) => // subnet scan
                  val (netId, firstIP, lastIP) = Parser.parseCIDR(input)
                  println(s"Scanning subnet $netId.$firstIP-$lastIP...")
                  HostScanner.pingRange(netId, firstIP, lastIP, config).map: results =>
                    val hostsUp = results.collect { case up(ip) => ip }
                    ResultsManager.printResults(hostsUp)
                    if (config.saveOnFile) ResultsManager.saveResults(hostsUp)
              case _ =>
                  Logger.error(s"IP Address format not valid, retry.")
          case None =>
            Logger.error("No IP address or netID provided.")