package scanner

import java.net.InetAddress

sealed trait Result:
  def ip: String

case class up(ip: String) extends Result
case class down(ip: String) extends Result

object HostsScanner:

  def ping(ip: String): Option[Result] =
    InetAddress.getByName(ip) match
      case _ if InetAddress.getByName(ip).isReachable(500) => Some(up(ip))
      case _ => Some(down(ip))
    
      
  




