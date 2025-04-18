package scanner

import java.net.{InetAddress, InetSocketAddress, Socket}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Try, Success, Failure}


object PortsScanner:

  val commonPorts: Seq[Int] = Seq(21, 22, 23, 25, 53, 80, 110, 139, 143, 443, 445, 3389)

  def scanSinglePort(ip: String, port: Int): Future[Option[Int]] =
    Future {
      val socket = new Socket()
      val timeout = 500
      Try {
        socket.connect(new InetSocketAddress(ip, port), timeout)
        socket.close()
        port
      }.toOption // return Success value or default Failure value
    }

  def scanPorts(ip: String, ports: Seq[Int] = commonPorts): Future[Seq[Int]] =
    val portFutures = ports.map(scanSinglePort(ip, _))
    Future.sequence(portFutures).map(_.flatten)