package utils

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object Logger:
  private val timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss")

  private def timestamp: String = s"[${LocalTime.now().format(timeFormat)}]"

  private def log(msg: String, colorCode: String): Unit =
    println(s"$colorCode$msg${Console.RESET}")

  def info(msg: String): Unit = log(msg, Console.BLUE)
  def message(msg: String): Unit = log(msg, Console.BLUE)
  def success(msg: String): Unit = log(msg, Console.GREEN)
  def warn(msg: String): Unit = log(msg, Console.YELLOW)
  def error(msg: String): Unit = log(msg, Console.RED)



 
