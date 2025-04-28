package utils

case class Config(
                   showHelp: Boolean = false,
                   showOpenPorts: Boolean = true,
                   showServices: Boolean = false,
                   detectOS: Boolean = false,
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
    val first = 1
    val last = if (maskLength == 24) 254 else 255
    (netId, first, last)

  private def parseFlags(flags: Seq[String]): Config =
    Config(
      showHelp = flags.contains("-h"),
      showOpenPorts = flags.contains("-open"),
      showServices = flags.contains("-serv"),
      detectOS = flags.contains("-os"),
      saveOnFile = flags.contains("-save"),
      verboseMode = flags.contains("-v")
    )