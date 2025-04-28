import utils.*
import scanner.*
import scanner.HostsScanner.*
import utils.Logger.*
import utils.Parser.*
import scala.util.matching.Regex

object Main:

  val ipRegex: Regex = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$""".r
  val cidrRegex: Regex = """^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)/([12]?[0-9]|3[0-2])$""".r

  def main(args: Array[String]): Unit =
    welcomeMessage()

    if (args.isEmpty)
      warn("No argument provided")
      help()
    else
      val (inputOpt, config) = parseInputAndConfig(args)
      if (config.showHelp) help()
      else inputOpt match
          case Some(input) => input match
                                case ipRegex(_*)   => scan(input, config, range = false)
                                case cidrRegex(_*) => scan(input, config, range = true)
                                case _             => error(s"IP Address format not valid, retry.")
          case None        => error("No IP address or netID provided.")
                              help()