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

//  def parseIp(input: String): (String, Int) =
//    val octets = input.split("\\.")
//    val netId = octets.take(3).mkString(".")
//    val last = octets(3).toInt
//    (netId, last)

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

  def printHelp =
    Logger.warn("-----------------------------------------------")
    Logger.warn("(On sbt shell)")
    Logger.warn("Usage:\trun <IP address> [-HOSV]")
    Logger.warn("\trun <netID>/<CIDR> [-HOSV]")
    Logger.warn("Options:")
    Logger.warn("-H \tshow usage (this option won't start the scan)" +
      "\n-O \tshow open ports" +
      "\n-S \tsave scan results on file" +
      "\n-V \tverbose mode")
    Logger.warn("\n---------Default options are all false---------")
    Logger.warn("-----------------------------------------------")