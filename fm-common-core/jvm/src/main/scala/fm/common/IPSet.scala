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

import it.unimi.dsi.fastutil.ints.{IntAVLTreeSet, IntIterator}
import it.unimi.dsi.fastutil.longs.{LongIterator, LongOpenHashSet}

object IPSet {
  def newBuilder: IPSetMutable = IPSetMutable()
  
  val empty: IPSetImmutable = IPSetImmutable.empty
  
  def apply(ips: String*): IPSetImmutable = IPSetImmutable(ips:_*)
  def apply(ips: TraversableOnce[IPOrSubnet]): IPSetImmutable = IPSetImmutable(ips)
}

sealed trait IPSet {
  private[common] def ipsWithMask: LongOpenHashSet
  private[common] def masks: IntAVLTreeSet
  
  def toImmutable: IPSetImmutable
  def toMutable: IPSetMutable
  
  final def contains(ip: String): Boolean = contains(IP(ip))
  
  final def contains(ip: IP): Boolean = {
    val it: IntIterator = masks.iterator()
    while (it.hasNext) {
      val mask: Int = it.nextInt
      if (ipsWithMask.contains(makeIPWithMask(ip.intValue & mask, mask))) return true
    }
    
    false
  }
  
  /**
   * Does this IPSet exactly contain the given subnet
   */
  final def containsExact(subnet: IPOrSubnet): Boolean = {
    ipsWithMask.contains(makeIPWithMask(subnet.start.intValue, subnet.mask))
  }
  
  final def isEmpty: Boolean = ipsWithMask.isEmpty()
  
  final def hasDefaultRoute: Boolean = containsExact(IPSubnet.DefaultRoute)
  final def hasQuadZero: Boolean = containsExact(IPSubnet.QuadZero)
  
  // [UPPER 32 BITS IS IP ADDRESS][LOWER 32 BITS IS THE BITMASK]
  protected def makeIPWithMask(ip: Int, mask: Int): Long = BitUtils.makeLong(ip, mask)
}

object IPSetMutable {
  def apply(): IPSetMutable = new IPSetMutable()
}

final class IPSetMutable extends IPSet with BuilderCompat[IPOrSubnet, IPSetImmutable] {
  private[common] val ipsWithMask: LongOpenHashSet = new LongOpenHashSet()
  private[common] val masks: IntAVLTreeSet = new IntAVLTreeSet()
  
  def toImmutable: IPSetImmutable = result()
  def toMutable: IPSetMutable = this
  
  def +=(ip: String): this.type = +=(IPSubnet.parse(ip))
  
  override def addOne(ip: IPOrSubnet): this.type = {
    ipsWithMask.add(makeIPWithMask(ip.start.intValue, ip.mask))
    masks.add(ip.mask)
    this
  }
  
  def ++=(ips: String*): this.type = {
    ips.foreach{ += _ }
    this
  }
  
  def ++=(other: IPSet): this.type = {
    val ipIT: LongIterator = other.ipsWithMask.iterator()
    while (ipIT.hasNext) {
      ipsWithMask.add(ipIT.nextLong)
    }
    
    val maskIT: IntIterator = other.masks.iterator()
    
    while (maskIT.hasNext) {
      masks.add(maskIT.nextInt)
    }
    
    this
  }
  
  override def clear(): Unit = {
    ipsWithMask.clear()
    masks.clear()
  }
  
  override def result(): IPSetImmutable = new IPSetImmutable(this)
}

object IPSetImmutable {
  def apply(ips: String*): IPSetImmutable = {
    val builder: IPSetMutable = IPSet.newBuilder
    ips.foreach{ builder += _ }
    builder.result()
  }
  
  def apply(ips: TraversableOnce[IPOrSubnet]): IPSetImmutable = {
    val builder: IPSetMutable = IPSet.newBuilder
    ips.foreach{ builder += _ }
    builder.result()
  }
  
  val empty: IPSetImmutable = IPSet.newBuilder.result()
}

final class IPSetImmutable(set: IPSetMutable) extends IPSet {
  private[common] val ipsWithMask: LongOpenHashSet = new LongOpenHashSet(set.ipsWithMask)
  private[common] val masks: IntAVLTreeSet = new IntAVLTreeSet(set.masks)
 
  def toImmutable: IPSetImmutable = this
  
  def toMutable: IPSetMutable = {
    IPSet.newBuilder ++= this
  }
  
  def ++(other: IPSet): IPSetImmutable = (toMutable ++= other).result()
}
