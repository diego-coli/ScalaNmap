package utils
import recon.Address.*

case class Config(
                   showHelp: Boolean = false,
                   showOpenPorts: Boolean = true,
                   showServices: Boolean = false,
                   detectOS: Boolean = false,
                   saveOnFile: Boolean = false,
                   verboseMode: Boolean = false
                 )

case class Subnet(
                   network: String,
                   subnetMask: String,
                   broadcast: String,
                   firstIp: String,
                   lastIp: String,
                   cidr: String
                 )

object Parser:

  def parseInputAndConfig(args: Array[String]): (Option[String], Config) =
    val (ipArgs, flags) = args.partition(arg => !arg.startsWith("-"))
    val inputOpt = ipArgs.headOption
    val config = Parser.parseFlags(flags)
    (inputOpt, config)

  def parseCIDR(cidr: String): Subnet =
    val Array(ipStr, prefixLengthStr) = cidr.split("/")
    getSubnet(cidr, ipStr, prefixLengthStr)

  private def parseFlags(flags: Seq[String]): Config =
    Config(
      showHelp = flags.contains("-h"),
      showOpenPorts = flags.contains("-open"),
      showServices = flags.contains("-serv"),
      detectOS = flags.contains("-os"),
      saveOnFile = flags.contains("-save"),
      verboseMode = flags.contains("-v")
    )