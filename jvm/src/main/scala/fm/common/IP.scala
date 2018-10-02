/*
 * Copyright 2014 Frugal Mechanic (http://frugalmechanic.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fm.common

import java.net.InetAddress
import scala.util.matching.Regex

final case class InvalidIPException(msg: String) extends IllegalArgumentException(msg)

/**
 * Helpers for parsing and working with IPv4 addresses
 */
object IP {
  val MAX_IP: Long = 4294967295L

  val empty: IP = new IP(0)
  
  /**
   * Is this a valid IPv4 Address formatted as xxx.xxx.xxx.xxx ?
   * NOTE!: This doesn't mean the apply() method will fail since it does hostname resolution.
   */
  def isValid(ip: String): Boolean = try { toInt(ip); true } catch { case _: InvalidIPException => false }

  // ddd.ddd.ddd.ddd with an optional trailing dot followed by the end of the string/line or whitespace
  private def ipv4Pattern: Regex = """(^|\s|,)(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})\.?($|(?=(\s|,)))""".r
  
  def findAllIPsIn(ips: String): IndexedSeq[IP] = if(null == ips) Vector.empty else ipv4Pattern.findAllIn(ips).matchData.map{_.group(2)}.map{apply}.toIndexedSeq
  
  object parse {
    def apply(ip: String): Option[IP] = get(ip)
    def unapply(ip: String): Option[IP] = get(ip)
  }
  
  def get(ip: String): Option[IP] = try{ Some(apply(ip)) } catch{ case _: InvalidIPException => None }
  
  def apply(ip: String): IP = {
    if (ip.isNullOrBlank) throw new InvalidIPException("IP Address cannot be empty")
    
    val dotCount: Int = ip.count{ _ === '.' }
    
    try {
      // Allow 1.2.3.4 and 1.2.3.4. (trailing dot)
      if (dotCount === 3 || dotCount === 4) apply(toInt(ip))
      else if (ip.forall{Character.isDigit(_)}) apply(ip.toLong)
      else throw new InvalidIPException("Not sure how to parse: "+ip)
    } catch {
      case ex: NumberFormatException => try { apply(InetAddress.getByName(ip)) } catch { case _: NoSuchElementException => throw new InvalidIPException("Not sure how to parse: "+ip) }
    }
  }
  
  def apply(ip: Long): IP = apply(toInt(ip))
  def apply(address: InetAddress): IP = apply(toInt(address.getAddress))
  def apply(bytes: Array[Byte]): IP = apply(toInt(bytes))

  /**
   * The single apply() method that actually creates an IP object
   */
  def apply(ip: Int): IP = new IP(ip)

  def toInt(ip: String): Int = {
    val fixedIp: String = if (ip.endsWith(".")) ip.substring(0, ip.length - 1) else ip
    val octets = fixedIp.trim.split('.').map{_.toShortOption.filter{ n: Short => n >= 0 && n <= 255 }.getOrElse{ throw InvalidIPException("Invalid IP Address: "+fixedIp) }.toByte}
    if(octets.size !== 4) throw InvalidIPException("Invalid IP Address: "+fixedIp)
    toInt(octets)
  }

  def toLong(ip: String): Long = toLong(toInt(ip))

  def toInt(bytes: Array[Byte]): Int = {
    if(bytes.size !== 4) throw InvalidIPException("Invalid IP Address: "+bytes.toSeq)
    ((bytes(0) & 0xff) << 24) + ((bytes(1) & 0xff) << 16) + ((bytes(2) & 0xff) << 8) + (bytes(3) & 0xff)
  }

  def toInt(ints: Array[Int]): Int = toInt(ints.map{_.toByte})

  def toLong(bytes: Array[Byte]): Long = toLong(toInt(bytes))
  def toLong(ints: Array[Int]): Long = toLong(toInt(ints))

  def toBytes(ip: Int): Array[Byte] = {
    val bytes = new Array[Byte](4)
    bytes(0) = ((ip >>> 24) & 0xff).toByte
    bytes(1) = ((ip >>> 16) & 0xff).toByte
    bytes(2) = ((ip >>> 8) & 0xff).toByte
    bytes(3) = (ip & 0xff).toByte
    bytes
  }

  def toBytes(ip: Long): Array[Byte] = toBytes(toInt(ip))

  def toLong(ip: Int): Long = ip & 0xffffffffL
  def toInt(ip: Long): Int = ip.toInt

  def toIntArray(ip: Int): Array[Int] = toBytes(ip).map{_ & 0xff}
  def toIntArray(ip: Long): Array[Int] = toBytes(ip).map{_ & 0xff}

  def toString(ip: Int): String = toIntArray(ip).mkString(".")
  def toString(ip: Long): String = toIntArray(ip).mkString(".")

  def toReversedString(ip: String): String = toReversedString(toInt(ip))

  def toReversedString(ip: Int): String = toIntArray(ip).reverse.mkString(".")
  def toReversedString(ip: Long): String = toIntArray(ip).reverse.mkString(".")
}

/**
 * Simple Wrapper around an IPv4 Address
 */
final class IP private(val ip: Int) extends AnyVal with Ordered[IP] with IPOrSubnet {
  import IP._
  
  // IPOrSubnet implementation
  def start: IP = this
  
  // IPOrSubnet implementation
  def end: IP = this
  
  // IPOrSubnet implementation
  def mask: Int = -1 // all 1's
  
  // IPOrSubnet implementation
  def toIPSubnet: IPSubnet = IPSubnet(this, 32)
  
  def bytes: Array[Byte] = toBytes(ip)
  def intArray: Array[Int] = toIntArray(ip)
  def inetAddress: InetAddress = InetAddress.getByAddress(bytes)

  def octets: (Int, Int, Int, Int) = {
    val arr: Array[Int] = intArray
    (arr(0), arr(1), arr(2), arr(3))
  }
  
  def longValue: Long = toLong(ip)
  def intValue: Int = ip
  
  /** Is this a 127.0.0.0/8 IP Address? */
  def isLocalhost: Boolean = IPSubnet.Localhost.contains(this)
  
  /** Is this NOT a 127.0.0.0/8 IP Address? */
  def isNotLocalhost: Boolean = !isLocalhost
  
  /** Is this an RFC 1918 Private IP Address (or Localhost)? */
  def isPrivate: Boolean = IPSubnets.Private.contains(this)

  def reversedString: String = toReversedString(ip)
  override def toString: String = IP.toString(ip)

  // Ordered[IP] Implementation
  def compare(that: IP): Int = longValue.compare(that.longValue)
  
  def contains(other: IP): Boolean = ip === other.ip
}
