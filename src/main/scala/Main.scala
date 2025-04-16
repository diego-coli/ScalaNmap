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
          case Some(input) => input match
                                case ipRegex(_*)   => HostScanner.scanHost(input, config)
                                case cidrRegex(_*) => HostScanner.scanRange(input, config)
                                case _             => Logger.error(s"IP Address format not valid, retry.")
          case None        => Logger.error("No IP address or netID provided.")