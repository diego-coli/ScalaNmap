import utils.*
import scanner.*
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
      val (input, config): (String, Config) = getInputAndConfig(args)
      input match
        case ipRegex(_*) => // single host scan
            HostScanner.pingHost(input).map:
              case up(ip) =>
                println("\nHost up")
              case down(ip) =>
                println("\nHost down")

        case cidrRegex(_*) => // subnet scan
              val (netId, firstIP, lastIP) = Parser.parseCIDR(input)
              println(s"Scanning subnet $netId.$firstIP-$lastIP...")

              HostScanner.pingRange(netId, firstIP, lastIP, config).map: results =>
                val hostsUp = results.collect { case up(ip) => ip }
                printResults(hostsUp)
                if config.saveOnFile then saveResults(hostsUp)
        
        case _ =>
              Logger.info(s"IP Address format not valid, retry.")

def getInputAndConfig(args: Array[String]): (String, Config) =
  val (ipArgs, flags) = args.partition(arg => !arg.startsWith("-"))
  val input = ipArgs.head
  val config = Parser.parseFlags(flags)
  (input, config)

def saveResults(hostsUp: Seq[String]) =
  val outputPath = java.nio.file.Paths.get("scanned_hosts.txt")
  java.nio.file.Files.write(outputPath, hostsUp.mkString("\n").getBytes)
  println(s"\nActive hosts saved in: ${outputPath.toAbsolutePath}")

def printResults(hostsUp: Seq[String]) =
  if hostsUp.nonEmpty then
    println("\nActive hosts:")
    hostsUp.foreach(ip => println(s" - $ip"))
  else
    println("\nNo active hosts found.")