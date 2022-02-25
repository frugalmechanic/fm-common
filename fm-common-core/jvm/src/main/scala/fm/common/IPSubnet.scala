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

object IPSubnet {
  val Localhost: IPSubnet = parse("127.0.0.0/8")
  val DefaultRoute: IPSubnet = parse("0.0.0.0/0")
  def QuadZero: IPSubnet = DefaultRoute
  
  def parse(subnet: String): IPSubnet = apply(subnet)
  
  def get(ip: String): Option[IPSubnet] = try{ Some(apply(ip)) } catch{ case _: InvalidIPException => None }
  
  def apply(subnet: String): IPSubnet = {
    val slashes: Int = subnet.countOccurrences('/')
    val dashes: Int = subnet.countOccurrences('-')
    
    if (slashes === 1) {
      val Array(ip: String, prefix: String) = (subnet.split('/'): @unchecked) // Silence the "match may not be exhaustive." warning
      forCIDR(IP(ip), prefix.toInt)
    } else if (dashes === 1) {
      val Array(fromStr, toStr) = (subnet.split('-'): @unchecked) // Silence the "match may not be exhaustive." warning
      forRangeOrMask(IP(fromStr), IP(toStr))
    } else if (IP.isValid(subnet)) {
      apply(IP(subnet), 32)
    } else throw new InvalidIPException("Not sure how to parse subnet: "+subnet)
  }
  
  def forRangeOrMask(from: IP, toOrMask: IP): IPSubnet = {
    val validMask: Boolean = isValidMask(toOrMask)
    val validRange: Boolean = isValidRange(from, toOrMask)
    
    require(validMask || validRange, "Not a valid range or mask")
    require(!(validMask && validRange), "toOrMask parameter is ambiguous since it looks like both a valid mask and valid range")
    
    if (validMask) forMask(from, toOrMask)
    else if (validRange) forRange(from, toOrMask)
    else throw new InvalidIPException("Invalid Condition")
  }
  
  def forMask(ip: IP, mask: IP): IPSubnet = {
    require(isValidMask(mask), "Not a valid mask: "+mask)
    val leadingOnes: Int = numberOfLeadingOnes(mask.intValue)
    apply(ip, leadingOnes)
  }
  
  def forRange(from: IP, to: IP): IPSubnet = {
    require(from < to, s"""Expected from "$from" to be less than to "$to"""")
    apply(from, commonPrefixBitCount(from.intValue, to.intValue))
  }

  def forCIDR(ip: IP, prefix: Int): IPSubnet = {
    require(isValidCIDR(ip, prefix), s"Invalid Subnet - ip: $ip  prefix: $prefix")
    apply(ip, prefix)
  }
  
  //def apply(ip: IP, bits: Int): IPSubnet = IPSubnet(ip.intValue, bits)
  
  def isValidMask(ip: IP): Boolean = isValidMaskImpl(ip.intValue)
  
  /**
   * Could this be a valid bit mask?  Should be leading ones and trailing zeros
   */
  private def isValidMaskImpl(ip: Int): Boolean = {
    val trailingZeros: Int = Integer.numberOfTrailingZeros(ip)
    val leadingOnes: Int = numberOfLeadingOnes(ip)
    trailingZeros + leadingOnes === 32
  }

  /**
   *
   * @param ip The IP Address (e.g. 10.10.123.0)
   * @param prefix The mask bits (e.g. 24)
   * @return Whether or not this is a valid subnet
   */
  def isValidCIDR(ip: IP, prefix: Int): Boolean = {
    if (prefix < 0 || prefix > 32) return false

    val trailingZeros: Int = Integer.numberOfTrailingZeros(ip.intValue)
    trailingZeros >= 32 - prefix // All of the mask bits in the IP address should be zero
  }
  
  def isValidRange(from: IP, to: IP): Boolean = isValidRangeImpl(from.intValue, to.intValue)
  
  /**
   * Could this be a valid subnet range?
   * 
   *   - from < to
   *   - They should have a common prefix and all bits after the prefix in from should be zero and in to should be 1
   */
  private def isValidRangeImpl(from: Int, to: Int): Boolean = {
    val commonPrefix: Int = commonPrefixBitCount(from, to)
    val fromTrailingZeros: Int = Integer.numberOfTrailingZeros(from)
    val toTrailingOnes: Int = numberOfTrailingOnes(to)
    
    val validFrom: Boolean = commonPrefix + fromTrailingZeros >= 32
    val validTo: Boolean = commonPrefix + toTrailingOnes >= 32
    
    from < to && validFrom && validTo 
  }
  
  private def commonPrefixBitCount(a: Int, b: Int): Int = Integer.numberOfLeadingZeros(a ^ b)
  private def numberOfLeadingOnes(i: Int): Int = Integer.numberOfLeadingZeros(i ^ 0xffffffff)
  private def numberOfTrailingOnes(i: Int): Int = Integer.numberOfTrailingZeros(i ^ 0xffffffff)
}

final case class IPSubnet(ip: IP, bits: Int) extends IPOrSubnet {
  require(bits <= 32, "Invalid bits: "+bits)

  private def shift: Int = 32 - bits
  private def prefix: Int = ip.intValue >>> shift
  
  // Is this 0.0.0.0/0?
  def isDefaultRoute: Boolean = ip.intValue === 0 && bits === 0
  
  // Alias of isDefaultRoute
  def isQuadZero: Boolean = isDefaultRoute
  
  /**
   * The bitmask for this subnet.
   * 
   * For example the mask for 127.0.0.0/8 is 1111111000000000000000000000000 (8 leading bits are 1)
   */
  def mask: Int = if (32 === shift) 0 else -1 >>> shift << shift
  
  require(ip.intValue >>> shift << shift === ip.intValue, s"Subnet IP has non-zero bits when they should be zero.  IP: $ip  ${Integer.toBinaryString(ip.intValue).lPad(32, '0')}  Mask: ${Integer.toBinaryString(mask).lPad(32, '0')}")
  
  // IPOrSubnet implementation
  def ipAddress: IP = ip
  
  // IPOrSubnet implementation
  def toIPSubnet: IPSubnet = this

  /**
   * Does this Subnet contain the IP address?
   */
  def contains(other: IP): Boolean = prefix.toLong === other.longValue >>> shift

  /**
   * The starting IP address for this Subnet
   */
  def start: IP = ip
  
  /**
   * The ending IP address for this subnet
   */
  def end: IP = IP(ip.intValue | (mask ^ 0xffffffff))
  
  override def toString = ip.toString+"/"+bits
}