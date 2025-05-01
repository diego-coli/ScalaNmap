package recon
import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.{InetSocketAddress, Socket}
import scala.util.Try

object Services:

  def recognizeService(ip: String, port: Int): Option[String] =
    val service = knownServices.getOrElse(port, "Unknown")
    Some(grabBanner(ip, port).fold(service)(banner => s"$service ($banner)"))

  private def grabBanner(ip: String, port: Int): Option[String] =
    Try:
      val socket = new Socket()
      socket.connect(new InetSocketAddress(ip, port), 1000)
      socket.setSoTimeout(1000)
      val input = new BufferedReader(new InputStreamReader(socket.getInputStream))
      val output = new PrintWriter(socket.getOutputStream, true)
      val request = defaultRequests.get(port)
      request.foreach(output.println)
      val banner = input.readLine()
      socket.close()
      banner
    .toOption.map(cleanBanner)

  private def cleanBanner(banner: String): String =
    banner.filter(c => c.isLetterOrDigit || c.isWhitespace || isPunctuation(c))

  private def isPunctuation(c: Char): Boolean =
    "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".contains(c)

  private val knownServices = Map(
    21 -> "FTP",
    22 -> "SSH",
    23 -> "Telnet",
    25 -> "SMTP",
    53 -> "DNS",
    80 -> "HTTP",
    110 -> "POP3",
    139 -> "SMB",
    143 -> "IMAP",
    443 -> "HTTPS",
    445 -> "SMB",
    3306 -> "MySQL",
    3389 -> "RDP",
    5432 -> "PostgreSQL",
    6379 -> "Redis",
    8080 -> "HTTP-Alt"
  )

  private val defaultRequests: Map[Int, String] = Map(
    21 -> "USER anonymous\r\n",
    23 -> "\r\n",
    25 -> "EHLO example.com\r\n",
    80 -> "HEAD / HTTP/1.0\r\n\r\n",
    110 -> "USER test\r\n",
    143 -> "a001 LOGIN test test\r\n",
    8080 -> "HEAD / HTTP/1.0\r\n\r\n"
  )  