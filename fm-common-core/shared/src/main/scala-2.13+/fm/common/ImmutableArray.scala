/*
 * Copyright 2020 Frugal Mechanic (http://frugalmechanic.com)
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

import scala.annotation.unchecked.uncheckedVariance
import scala.reflect.{ClassTag, classTag}
import scala.collection.{BuildFrom, ClassTagSeqFactory, EvidenceIterableFactoryDefaults, SeqFactory, StrictOptimizedClassTagSeqFactory}
import scala.collection.immutable.{AbstractSeq, IndexedSeq, IndexedSeqOps, StrictOptimizedSeqOps}
import scala.collection.mutable.Builder

object ImmutableArray extends StrictOptimizedClassTagSeqFactory[ImmutableArray] { self =>
  val untagged: SeqFactory[ImmutableArray] = new ClassTagSeqFactory.AnySeqDelegate(self)

  override def apply[@specialized A: ClassTag](elems: A*): ImmutableArray[A] = {
    if (elems.isEmpty) empty[A]
    else copy[A](elems)
  }

  /**
   * Create a new ImmutableArray by creating a copy of the passed in collection
   */
  def copy[@specialized A: ClassTag, COL](col: COL)(implicit toTraversableOnce: COL => TraversableOnce[A]): ImmutableArray[A] = copy[A](toTraversableOnce(col).toArray[A])

  /**
   * Create a new ImmutableArray by creating a copy of the passed in collection
   */
  def copy[@specialized A: ClassTag](col: TraversableOnce[A]): ImmutableArray[A] = copy[A](col.toArray[A])

  /**
   * Create a new ImmutableArray by creating a copy of the passed in collection
   */
  def copy[@specialized A: ClassTag](col: BuilderCompat.TraversableOnceOrIterableOnce[A]): ImmutableArray[A] = copy[A](col.toArray[A])

  /**
   * Create a new Immutable Array by creating a copy of the passed in array
   */
  def copy[@specialized A: ClassTag](arr: Array[A]): ImmutableArray[A] = {
    if (arr.length == 0) empty else {
      val dst = new Array[A](arr.length)
      System.arraycopy(arr, 0, dst, 0, arr.length)
      new ImmutableArray[A](dst)
    }
  }

  /**
   * Wrap an existing array in an ImmutableArray.  The passed in array must not be changed after calling this!
   */
  @deprecated("Use unsafeWrapArray instead","")
  def wrap[@specialized A: ClassTag](arr: Array[A]): ImmutableArray[A] = unsafeWrapArray(arr)

  /**
   * Wrap an existing array in an ImmutableArray.  The passed in array must not be changed after calling this!
   */
  def unsafeWrapArray[@specialized A: ClassTag](arr: Array[A]): ImmutableArray[A] = {
    if (arr.length == 0) empty else new ImmutableArray(arr)
  }

  private type From = ImmutableArray[_]

  implicit val buildFromChar: BuildFrom[From, Char, ImmutableArray[Char]] = new CBF(builderForChar)
  implicit val buildFromShort: BuildFrom[From, Short, ImmutableArray[Short]] = new CBF(builderForShort)
  implicit val buildFromFloat: BuildFrom[From, Float, ImmutableArray[Float]] = new CBF(builderForFloat)
  implicit val buildFromDouble: BuildFrom[From, Double, ImmutableArray[Double]] = new CBF(builderForDouble)
  implicit val buildFromInt: BuildFrom[From, Int, ImmutableArray[Int]] = new CBF(builderForInt)
  implicit val buildFromLong: BuildFrom[From, Long, ImmutableArray[Long]] = new CBF(builderForLong)

  implicit def canBuildFrom[A]: BuildFrom[From, A, ImmutableArray[A]] = new CBF[A](builderForAnyRef.asInstanceOf[ImmutableArrayBuilder[A]])

  private class CBF[A](makeBuilder: => ImmutableArrayBuilder[A]) extends BuildFrom[From, A, ImmutableArray[A]] {
    override def fromSpecific(from: From)(it: IterableOnce[A]): ImmutableArray[A] = {
      val builder: Builder[A, ImmutableArray[A]] = newBuilder(from)
      builder ++= it
      builder.result()
    }

    override def newBuilder(from: From): ImmutableArrayBuilder[A] = makeBuilder
  }

  //  def empty[A]: ImmutableArray[A] = _empty.asInstanceOf[ImmutableArray[A]]
  override def empty[A](implicit ev: ClassTag[A]): ImmutableArray[A] = _empty.asInstanceOf[ImmutableArray[A]]

  override def from[A: ClassTag](it: IterableOnce[A]): ImmutableArray[A] = it match {
    case arr: ImmutableArray[A] => arr
    case _ => unsafeWrapArray(Array.from[A](it))
  }

  override def newBuilder[@specialized A: ClassTag]: ImmutableArrayBuilder[A] = new ImmutableArrayBuilder[A](0)
  def newBuilder[@specialized A: ClassTag](initialSize: Int): ImmutableArrayBuilder[A] = new ImmutableArrayBuilder[A](initialSize)

  def builderForChar: ImmutableArrayBuilder[Char] = new ImmutableArrayBuilder[Char](0)
  def builderForShort: ImmutableArrayBuilder[Short] = new ImmutableArrayBuilder[Short](0)
  def builderForFloat: ImmutableArrayBuilder[Float] = new ImmutableArrayBuilder[Float](0)
  def builderForDouble: ImmutableArrayBuilder[Double] = new ImmutableArrayBuilder[Double](0)
  def builderForInt: ImmutableArrayBuilder[Int] = new ImmutableArrayBuilder[Int](0)
  def builderForLong: ImmutableArrayBuilder[Long] = new ImmutableArrayBuilder[Long](0)
  def builderForAnyRef: ImmutableArrayBuilder[AnyRef] = new ImmutableArrayBuilder[AnyRef](0)

  private val _empty: ImmutableArray[Nothing] = new ImmutableArray(new Array[AnyRef](0)).asInstanceOf[ImmutableArray[Nothing]]
}

final class ImmutableArray[@specialized +A: ClassTag] (arr: Array[A])
  extends AbstractSeq[A]
    with IndexedSeq[A]
    with IndexedSeqOps[A, ImmutableArray, ImmutableArray[A]]
    with StrictOptimizedSeqOps[A, ImmutableArray, ImmutableArray[A]]
    with EvidenceIterableFactoryDefaults[A, ImmutableArray, ClassTag] {
  override def apply(idx: Int): A = arr(idx)
  override def length: Int = arr.length

  override def iterableFactory: SeqFactory[ImmutableArray] = ImmutableArray.untagged
  override protected def evidenceIterableFactory: ImmutableArray.type = ImmutableArray
  override protected def iterableEvidence: ClassTag[A @uncheckedVariance] = classTag[A]
}

final class ImmutableArrayBuilder[@specialized A: ClassTag] (initialSize: Int) extends Builder[A, ImmutableArray[A]] {
  //
  // Note: DO NOT make these private[this] since that doesn't play well with @specialized
  //
  private var arr: Array[A] = if (initialSize > 0) new Array[A](initialSize) else null // Array.empty creates a new array each time so avoid using that
  private var capacity: Int = if (null == arr) 0 else arr.length
  private var _length: Int = 0

  /**
   * The number of items that have been added to this builder
   */
  def size: Int = _length
  def length: Int = _length

  override def addOne(elem: A): this.type = {
    ensureCapacity(_length + 1)
    arr(_length) = elem
    _length += 1
    this
  }

  def apply(idx: Int): A = {
    if (idx < 0 || idx >= length) throw new ArrayIndexOutOfBoundsException(s"Length: $length, requested idx: $idx")
    arr(idx)
  }

  def update(idx: Int, value: A): Unit = {
    ensureCapacity(idx + 1)
    arr(idx) = value
    _length = math.max(_length, idx + 1)
  }

  def insert(idx: Int, value: A): Unit = {
    ensureCapacity(_length + 1)

    if (idx >= _length) {
      // Nothing to shift around so just use the update() method
      update(idx, value)
    } else {
      System.arraycopy(arr, idx, arr, idx + 1, _length - idx)
      arr(idx) = value
      _length += 1
    }
  }

  def toArray: Array[A] = {
    if (_length == 0) return Array.empty
    assert(_length <= arr.length, s"Length: ${_length},  Array.length: ${arr.length}")

    val buf: Array[A] = new Array[A](_length)
    System.arraycopy(arr, 0, buf, 0, _length)
    buf
  }

  def result(): ImmutableArray[A] = {
    if (_length == 0) return ImmutableArray.empty
    else new ImmutableArray[A](toArray)
  }

  def clear(): Unit = {
    arr = null
    capacity = 0
    _length = 0
  }

  override def sizeHint(size: Int): Unit = {
    if (capacity < size) resize(size)
  }

  private def ensureCapacity(size: Int): Unit = {
    if (capacity < size) {
      var newSize: Int = if (capacity == 0) 16 else capacity * 2
      while (newSize < size) newSize = newSize * 2
      resize(newSize)
    }
  }

  private def resize(size: Int): Unit = {
    val buf: Array[A] = new Array[A](size)
    if (_length > 0 && null != arr) System.arraycopy(arr, 0, buf, 0, _length)
    arr = buf
    capacity = size
  }

  override def toString: String = {
    if (null == arr) "ImmutableArrayBuilder()"
    else arr.slice(0, _length).mkString("ImmutableArrayBuilder(", ",",")")
  }
}
