package recon
import scanner.*

import java.net.InetAddress
import scala.concurrent.Future
import scala.sys.process.*
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

object Address:

  def ping(ip: String): Future[Status] =
    val timeout = 500
    val address = InetAddress.getByName(ip)
    Future:
      if (address.isReachable(timeout)) up(ip)
      else down(ip)

  def getMac(ip: String): Future[Option[String]] =
    Future:
      Try:
        val output = "arp -a".!!
        val regex = s"""\\s*$ip\\s+([\\da-fA-F-]{17})\\s+""".r
        regex.findFirstMatchIn(output).map(_.group(1))
      .getOrElse(None)

  def getHostname(ip: String): Option[String] =
    try
      val address = InetAddress.getByName(ip)
      val host = address.getCanonicalHostName
      if (host != ip)
        Some(host) 
      else None
    catch case _: Exception => None
