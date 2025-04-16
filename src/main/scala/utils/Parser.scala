package utils
import scala.collection.immutable.Seq as end

case class Config(
                   defaultPortsToScan: Seq[Int] = 1 to 65535,
                   showHelp: Boolean = false,
                   showOpenPorts: Boolean = true,
                   saveOnFile: Boolean = false,
                   verboseMode: Boolean = false
                 )

object Parser:

  def parseInputAndConfig(args: Array[String]): (Option[String], Config) =
    val (ipArgs, flags) = args.partition(arg => !arg.startsWith("-"))
    val inputOpt = ipArgs.headOption
    val config = Parser.parseFlags(flags)
    (inputOpt, config)

  def parseCIDR(cidr: String): (String, Int, Int) =
    val Array(network, mask) = cidr.split("/")
    val maskLength = mask.toInt
    val netParts = network.split("\\.").map(_.toInt)
    val netId = netParts.take(3).mkString(".")
    val start = 1
    val end = if (maskLength == 24) 254 else 255
    (netId, start, end)

  def parseFlags(flags: Seq[String]): Config =
    Config(
      showHelp = flags.contains("-H"),
      showOpenPorts = flags.contains("-O"),
      saveOnFile = flags.contains("-S"),
      verboseMode = flags.contains("-V")
    )