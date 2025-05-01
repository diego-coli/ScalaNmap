package utils

object MsgLogger:

  private def log(msg: String, colorCode: String): Unit = println(s"$colorCode$msg${Console.RESET}")
  def info(msg: String): Unit = log(msg, Console.BLUE)
  def message(msg: String): Unit = log(msg, Console.BLUE)
  def success(msg: String): Unit = log(msg, Console.GREEN)
  def warn(msg: String): Unit = log(msg, Console.YELLOW)
  def error(msg: String): Unit = log(msg, Console.RED)
  def welcomeMessage(): Unit =
    info("\n\n\n--------------------------------------------------------------------")
    info("This is ScalaNmap, a network mapper fully designed in Scala. " +
      "\nType -h for help. Enjoy! " +
      "\nDiego Coli', 2025")
    info("--------------------------------------------------------------------")
  def help(): Unit =
    warn("-----------------------------------------------")
    warn("(On sbt shell)")
    warn("Usage:\trun <IP address> [-options]")
    warn("\trun <netID>/<CIDR> [-options]")
    warn("Options:")
    warn("-h \tshow usage (this option won't start the scan)" +
      "\n-open \tshow open ports" +
      "\n-serv \tshow services on open ports" +
      "\n-os \tdetect operating system" +
      "\n-save \tsave scan results on file" +
      "\n-v \tverbose mode")
    warn("\n---------Default options are all false---------")
    warn("-----------------------------------------------")