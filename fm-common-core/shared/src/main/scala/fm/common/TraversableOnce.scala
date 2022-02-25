/*
 * Copyright 2021 Tim Underwood (https://github.com/tpunder)
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

import scala.reflect.ClassTag

object TraversableOnce extends TraversableOnceAdapters {
  private class Reducer[A, B >: A](op: (B, A) => B) extends (A => Unit) {
    var first: Boolean = true
    var acc: B = null.asInstanceOf[B]

    override def apply(x: A): Unit = {
      if (first) {
        acc = x
        first = false
      } else {
        acc = op(acc, x)
      }
    }
  }

  private class Folder[A, B](initial: B, op: (B, A) => B) extends (A => Unit) {
    var result: B = initial
    override def apply(x: A): Unit = result = op(result, x)
  }

  private class CopyToArray[A, B >: A: ClassTag](size: Int) extends (A => Unit) {
    val arr: Array[B] = new Array(size)
    private var i: Int = 0

    override def apply(x: A): Unit = {
      arr(i) = x
      i += 1
    }
  }
}

/**
 * A stripped down scala.collection.TraversableOnce that can be used as the
 * the basis for collections implemented only via a foreach method and for
 * implicit rich wrappers that can be implemented only in terms of a foreach
 * method.
 *
 * Several of our Scala libraries relied on the pre Scala 2.13 TraversableOnce
 * as the base trait for its implementation (e.g. fm.lazyseq.LazySeq) or for
 * rich wrappers (e.g. fm.common.RichTraversableOnce and fm.fastutil) where we
 * primarily need just a foreach method. Starting in Scala 2.13 the built-in
 * scala.collection.TraversableOnce is now a deprecated alias for IterableOnce
 * which relies on an Iterator which we cannot always provide (e.g. 
 * fm.lazyseq.LazySeq).
 *
 * 
 */
trait TraversableOnce[+A] extends Any {
  def foreach[U](f: A => U): Unit

  // This can be used for explicitly requesting an implicit conversion to a TraversableOnce
  final def asTraversableOnce: TraversableOnce[A] = this

  //
  // Methods with default implementations
  //

  /**
   * @return The number of elements in this collection, if it can be cheaply
   *         computed, -1 otherwise. Cheaply usually means: Not requiring a
   *         collection traversal.
   */
  def knownSize: Int = -1

  def isTraversableAgain: Boolean = false

  // TODO: move all of these to TraversableOps (or similar). RichTraversableOnce?
  final def hasKnownSize: Boolean = knownSize >= 0
  final def hasKnownSizeAndIsNonEmpty: Boolean = knownSize > 0
  final def hasKnownSizeAndIsEmpty: Boolean = knownSize == 0

  def foldLeft[B](z: B)(op: (B, A) => B): B = {
    val folder: TraversableOnce.Folder[A, B] = new TraversableOnce.Folder[A, B](z, op)
    foreach(folder)
    folder.result
  }

  def reduceLeft[B >: A](op: (B, A) => B): B = {
    val reducer: TraversableOnce.Reducer[A, B] = new TraversableOnce.Reducer[A, B](op)
    foreach(reducer)
    if (reducer.first) throw new UnsupportedOperationException("empty.reduceLeft")
    reducer.acc
  }

  def reduceLeftOption[B >: A](op: (B, A) => B): Option[B] = {
    val reducer: TraversableOnce.Reducer[A, B] = new TraversableOnce.Reducer[A, B](op)
    foreach(reducer)
    if (reducer.first) None else Some(reducer.acc)
  }

  def sum[B >: A](implicit num: Numeric[B]): B = foldLeft(num.zero)(num.plus)

  def min[B >: A](implicit ord: Ordering[B]): A = {
    reduceLeftOption{ (x: A, y: A) => if (ord.lteq(x, y)) x else y }.getOrElse{ throw new UnsupportedOperationException("empty.min") }
  }

  def max[B >: A](implicit cmp: Ordering[B]): A = {
    reduceLeftOption{ (x: A, y: A) => if (cmp.gteq(x, y)) x else y }.getOrElse{ throw new UnsupportedOperationException("empty.max") }
  }

  def toArray[B >: A: ClassTag]: Array[B] = {
    if (hasKnownSizeAndIsEmpty) return Array.empty[B]

    if (knownSize > 0) {
      val copyToArray: TraversableOnce.CopyToArray[A, B] = new TraversableOnce.CopyToArray[A, B](knownSize)
      foreach(copyToArray)
      copyToArray.arr
    } else {
      val builder = Array.newBuilder[B]
      foreach{ builder += _ }
      builder.result()
    }
  }

  def toVector: Vector[A] = {
    if (knownSize == 0) return Vector.empty
    build(Vector.newBuilder[A])
  }

  def toIndexedSeq: IndexedSeq[A] = toVector

  def toSeq: Seq[A] = toIndexedSeq

  def toList: List[A] = {
    if (knownSize == 0) return Nil
    build(List.newBuilder[A])
  }

  @inline private def build[To](builder: scala.collection.mutable.Builder[A, To]): To = {
    if (knownSize > 0) builder.sizeHint(knownSize)
    foreach{ builder += _ }
    builder.result()
  }
}