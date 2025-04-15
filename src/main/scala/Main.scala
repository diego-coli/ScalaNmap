import utils.*
import scanner.*
import scala.util.matching.Regex
import scala.concurrent.ExecutionContext.Implicits.global
import java.nio.file.{Files, Paths}

object Main:

  val ipRegex: Regex = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$""".r
  val cidrRegex: Regex = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)/([12]?[0-9]|3[0-2])$""".r

  def main(args: Array[String]): Unit =
    welcomeMessage()
    if (args.isEmpty) Logger.warn("No argument provided")
    else
      val (inputOpt, config) = getInputAndConfig(args)
      if (config.showHelp) Parser.printHelp
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

def getInputAndConfig(args: Array[String]): (Option[String], Config) =
  val (ipArgs, flags) = args.partition(arg => !arg.startsWith("-"))
  val inputOpt = ipArgs.headOption
  val config = Parser.parseFlags(flags)
  (inputOpt, config)

def welcomeMessage() =
  Logger.info("\n--------------------------------------------------------------------")
  Logger.info("This is ScalaNmap, a network mapper fully designed in Scala and Java. " +
          "\nType -h for help. Enjoy! " +
          "\nDiego Coli', 2025")
  Logger.info("--------------------------------------------------------------------")