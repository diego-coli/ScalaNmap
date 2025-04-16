package utils
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object Logger:

  private val timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss")
  private def timestamp: String = s"[${LocalTime.now().format(timeFormat)}]"
  private def log(msg: String, colorCode: String): Unit = println(s"$colorCode$msg${Console.RESET}")

  def info(msg: String): Unit = log(msg, Console.BLUE)
  def message(msg: String): Unit = log(msg, Console.BLUE)
  def success(msg: String): Unit = log(msg, Console.GREEN)
  def warn(msg: String): Unit = log(msg, Console.YELLOW)
  def error(msg: String): Unit = log(msg, Console.RED)

  def welcomeMessage() =
    info("\n--------------------------------------------------------------------")
    info("This is ScalaNmap, a network mapper fully designed in Scala and Java. " +
      "\nType -h for help. Enjoy! " +
      "\nDiego Coli', 2025")
    info("--------------------------------------------------------------------")

  def help =
    warn("-----------------------------------------------")
    warn("(On sbt shell)")
    warn("Usage:\trun <IP address> [-HOSV]")
    warn("\trun <netID>/<CIDR> [-HOSV]")
    warn("Options:")
    warn("-H \tshow usage (this option won't start the scan)" +
      "\n-O \tshow open ports" +
      "\n-S \tsave scan results on file" +
      "\n-V \tverbose mode")
    warn("\n---------Default options are all false---------")
    warn("-----------------------------------------------")