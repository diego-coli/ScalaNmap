package utils
import scala.collection.immutable.Seq as end

case class Config(
                   defaultPortsToScan: Seq[Int] = 1 to 65535,
                   showOpenPorts: Boolean = true,
                   saveOnFile: Boolean = false,
                   verboseMode: Boolean = false
                 )

object Parser:

  def parseIp(input: String): (String, Int) =
    val octets = input.split("\\.")
    val netId = octets.take(3).mkString(".")
    val last = octets(3).toInt
    (netId, last)

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
      saveOnFile = flags.contains("-s"),
      verboseMode = flags.contains("-v"),
      showOpenPorts = flags.contains("-o")
    )