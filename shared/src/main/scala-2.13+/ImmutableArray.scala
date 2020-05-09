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

import scala.annotation.unchecked.uncheckedVariance
import scala.collection.{BuildFrom, Factory, IndexedSeqOps, mutable}
import scala.reflect.ClassTag
import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.IndexedSeq
import scala.collection.mutable.Builder

object ImmutableArray {
  def apply[@specialized A: ClassTag](elems: A*): ImmutableArray[A] = {
    if (elems.isEmpty) empty[A]
    else copy[A](elems)
  }

  /**
   * Create a new ImmutableArray by creating a copy of the passed in collection
   */
  def copy[@specialized A: ClassTag](col: IterableOnce[A]): ImmutableArray[A] = copy[A](col.iterator.toArray[A])

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
  def wrap[@specialized A: ClassTag](arr: Array[A]): ImmutableArray[A] = {
    if (arr.length == 0) empty else new ImmutableArray(arr)
  }

  private type Coll = ImmutableArray[_]

  implicit val canBuildFromChar: BuildFrom[Coll, Char, ImmutableArray[Char]] = new CBF(builderForChar)
  implicit val canBuildFromShort: BuildFrom[Coll, Short, ImmutableArray[Short]] = new CBF(builderForShort)
  implicit val canBuildFromFloat: BuildFrom[Coll, Float, ImmutableArray[Float]] = new CBF(builderForFloat)
  implicit val canBuildFromDouble: BuildFrom[Coll, Double, ImmutableArray[Double]] = new CBF(builderForDouble)
  implicit val canBuildFromInt: BuildFrom[Coll, Int, ImmutableArray[Int]] = new CBF(builderForInt)
  implicit val canBuildFromLong: BuildFrom[Coll, Long, ImmutableArray[Long]] = new CBF(builderForLong)

  implicit def canBuildFrom[A]: BuildFrom[Coll, A, ImmutableArray[A]] = new CBF[A](builderForAnyRef.asInstanceOf[ImmutableArrayBuilder[A]])

  private class CBF[Elem](makeBuilder: => ImmutableArrayBuilder[Elem]) extends BuildFrom[Coll, Elem, ImmutableArray[Elem]] {
    def apply(): ImmutableArrayBuilder[Elem] = makeBuilder

    override def fromSpecific(from: Coll)(it: IterableOnce[Elem]): ImmutableArray[Elem] = {
      val builder: ImmutableArrayBuilder[Elem] = makeBuilder
      it.iterator.foreach { builder += _ }
      builder.result
    }

    override def newBuilder(from: Coll): mutable.Builder[Elem, ImmutableArray[Elem]] = makeBuilder
  }

  def empty[A]: ImmutableArray[A] = _empty.asInstanceOf[ImmutableArray[A]]

  def newBuilder[@specialized A: ClassTag]: ImmutableArrayBuilder[A] = new ImmutableArrayBuilder[A](0)
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

final class ImmutableArray[@specialized +A: ClassTag] (arr: Array[A]) extends IndexedSeq[A] {
  def apply(idx: Int): A = arr(idx)
  def length: Int = arr.length
  //def newBuilder: ImmutableArrayBuilder[A @uncheckedVariance] = new ImmutableArrayBuilder[A](0)
}

final class ImmutableArrayBuilder[@specialized A: ClassTag] (initialSize: Int) extends mutable.Builder[A, ImmutableArray[A]] {
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

  def addOne(elem: A): this.type = {
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

  def result: ImmutableArray[A] = {
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