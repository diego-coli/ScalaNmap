package recon
import scanner.*
import utils.Parser.*
import utils.Subnet

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

  def getSubnet(cidr: String, ipStr: String, prefixLengthStr: String): Subnet =
    val ip = ipToInt(ipStr)
    val prefixLength = prefixLengthStr.toInt
    val netmask = (-1 << (32 - prefixLength)) >>> 0
    val network = ip & netmask
    val broadcast = network | ~netmask
    val first = if (prefixLength == 32) network else network + 1
    val last = if (prefixLength == 32) network else broadcast - 1
    Subnet(
      network = intToIp(network),
      subnetMask = intToIp(netmask),
      broadcast = intToIp(broadcast),
      firstIp = intToIp(first),
      lastIp = intToIp(last),
      cidr = cidr
    )

  def ipToInt(ip: String): Int =
    ip.split("\\.").map(_.toInt).foldLeft(0)((acc, octet) => (acc << 8) + octet)

  def intToIp(ip: Int): String =
    (0 to 3).map(i => (ip >>> (24 - 8 * i)) & 0xFF).mkString(".")
