/*
 * Copyright 2015 Frugal Mechanic (http://frugalmechanic.com)
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

import it.unimi.dsi.fastutil.ints.{Int2IntAVLTreeMap, IntComparator, IntIterator}
import it.unimi.dsi.fastutil.longs.{LongIterator, Long2ObjectOpenHashMap}
import scala.collection.mutable.Builder

object IPMap {
  def newBuilder[T]: IPMapMutable[T] = IPMapMutable()

  def empty[T]: IPMapImmutable[T] = IPMapImmutable.empty[T]

  def apply[T](ips: TraversableOnce[(IPOrSubnet, T)]): IPMapImmutable[T] = IPMapImmutable(ips)

  private[common] val leadingBitsFirstComparator: IntComparator = new IntComparator() {
    def compare(a: Int, b: Int): Int = {
      // Note:
      //  - masks should only have leading ones so Integer.bitCount should be fine
      //  - We want a higher number of leading 1's to be sorted first
      Integer.compare(Integer.bitCount(b), Integer.bitCount(a))
    }
  }
}

sealed trait IPMap[T] {
  // This maps the ip and mask to whatever object we are storing
  private[common] def ipsWithMaskMap: Long2ObjectOpenHashMap[T]

  // This maps the mask to a count of how many items have this mask in the ipsWithMaskMap
  private[common] def maskToCountMap: Int2IntAVLTreeMap

  def toImmutable: IPMapImmutable[T]
  def toMutable: IPMapMutable[T]

  final def apply(ip: String): T = apply(IP(ip))

  final def apply(ip: IP): T = {
    val value: T = getOrNull(ip)
    if (null == value) throw new NoSuchElementException(ip.toString)
    value
  }

  final def get(ip: String): Option[T] = get(IP(ip))

  final def get(ip: IP): Option[T] = Option(getOrNull(ip))

  private def getOrNull(ip: IP): T = {
    val it: IntIterator = maskToCountMap.keySet.iterator()
    while (it.hasNext) {
      val mask: Int = it.nextInt
      val value: T = ipsWithMaskMap.get(makeIPWithMask(ip.intValue, mask))
      if (null != value) return value
    }

    null.asInstanceOf[T]
  }

  final def exact(ip: IPOrSubnet): T = {
    val value: T = getExactOrNull(ip)
    if (null == value) throw new NoSuchElementException(ip.toString)
    value
  }

  final def getExact(ip: IPOrSubnet): Option[T] = Option(getExactOrNull(ip))

  private def getExactOrNull(ip: IPOrSubnet): T = ipsWithMaskMap.get(makeIPWithMask(ip))

  final def contains(ip: String): Boolean = contains(IP(ip))

  final def contains(ip: IP): Boolean = {
    val it: IntIterator = maskToCountMap.keySet.iterator()
    while (it.hasNext) {
      val mask: Int = it.nextInt
      if (ipsWithMaskMap.containsKey(makeIPWithMask(ip.intValue, mask))) return true
    }

    false
  }

  /**
   * Does this IPMap exactly contain the given subnet
   */
  final def containsExact(subnet: IPOrSubnet): Boolean = ipsWithMaskMap.containsKey(makeIPWithMask(subnet))

  final def isEmpty: Boolean = ipsWithMaskMap.isEmpty()

  protected def makeIPWithMask(subnet: IPOrSubnet): Long = makeIPWithMask(subnet.start.intValue, subnet.mask)

  // [UPPER 32 BITS IS IP ADDRESS][LOWER 32 BITS IS THE BITMASK]
  protected def makeIPWithMask(ip: Int, mask: Int): Long = BitUtils.makeLong(ip & mask, mask)
}

object IPMapMutable {
  def apply[T](): IPMapMutable[T] = new IPMapMutable[T]()
}

final class IPMapMutable[T] extends IPMap[T] with Builder[(IPOrSubnet,T), IPMapImmutable[T]] {
  private[common] val ipsWithMaskMap: Long2ObjectOpenHashMap[T] = new Long2ObjectOpenHashMap()
  private[common] val maskToCountMap: Int2IntAVLTreeMap = new Int2IntAVLTreeMap(IPMap.leadingBitsFirstComparator)

  def toImmutable: IPMapImmutable[T] = result
  def toMutable: IPMapMutable[T] = this

  def +=(ip: String, value: T): this.type = +=(IPSubnet.parse(ip), value)

  def +=(ipAndValue: (IPOrSubnet, T)): this.type = +=(ipAndValue._1, ipAndValue._2)

  def +=(ip: IPOrSubnet, value: T): this.type = {
    add(ip.start.intValue, ip.mask, value)
    this
  }

  def ++=(other: IPMap[T]): this.type = {
    val ipIT: LongIterator = other.ipsWithMaskMap.keySet().iterator()
    while (ipIT.hasNext) {
      val key: Long = ipIT.nextLong
      add(BitUtils.getUpper(key), BitUtils.getLower(key), other.ipsWithMaskMap.get(key))
    }

    this
  }

  private def add(ip: Int, mask: Int, value: T): Unit = {
    val key: Long = makeIPWithMask(ip, mask)

    if (!ipsWithMaskMap.containsKey(key)) {
      // Key doesn't exist which means we need to modify the maskToCountMap
      maskToCountMap.put(mask, maskToCountMap.get(mask) + 1)
    }

    ipsWithMaskMap.put(key, value)
  }

  def clear(): Unit = {
    ipsWithMaskMap.clear()
    maskToCountMap.clear()
  }

  def result: IPMapImmutable[T] = new IPMapImmutable(this)
}

object IPMapImmutable {
  def apply[T](ips: TraversableOnce[(IPOrSubnet, T)]): IPMapImmutable[T] = {
    val builder: IPMapMutable[T] = IPMap.newBuilder
    ips.foreach{ builder += _ }
    builder.result
  }

  def empty[T]: IPMapImmutable[T] = _empty.asInstanceOf[IPMapImmutable[T]]

  private val _empty: IPMapImmutable[AnyRef] = IPMap.newBuilder[AnyRef].result()
}

final class IPMapImmutable[T](map: IPMapMutable[T]) extends IPMap[T] {
  private[common] val ipsWithMaskMap: Long2ObjectOpenHashMap[T] = new Long2ObjectOpenHashMap(map.ipsWithMaskMap)
  private[common] val maskToCountMap: Int2IntAVLTreeMap = new Int2IntAVLTreeMap(map.maskToCountMap)

  def toImmutable: IPMapImmutable[T] = this

  def toMutable: IPMapMutable[T] = {
    IPMap.newBuilder ++= this
  }

  def ++(other: IPMap[T]): IPMapImmutable[T] = (toMutable ++= other).result
}
