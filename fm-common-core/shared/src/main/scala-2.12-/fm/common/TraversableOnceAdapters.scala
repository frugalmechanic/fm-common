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

object TraversableOnceAdapters {
  final class TraversableOnceFunction1Adapter[A, B](val self: A => scala.collection.TraversableOnce[B]) extends (A => TraversableOnce[B]) {
    override def apply(a: A): TraversableOnce[B] = new TraversableOnceAdapter(self(a))
  }

  // In Scala 2.13 Option extends IterableOnce but in 2.12 it does not so we need this
  final class OptionFunction1Adapter[A, B](val self: A => Option[B]) extends (A => TraversableOnce[B]) {
    override def apply(a: A): TraversableOnce[B] = new OptionAdapter(self(a))
  }

  final class TraversableOnceAdapter[+A](val self: scala.collection.TraversableOnce[A]) extends AnyVal with TraversableOnce[A] {
    override def knownSize: Int = -1
    override def isTraversableAgain: Boolean = self.isTraversableAgain
    override def foreach[U](f: A => U): Unit = self.foreach(f)
    override def foldLeft[B](z: B)(op: (B, A) => B): B = self.foldLeft(z)(op)
    override def reduceLeft[B >: A](op: (B, A) => B): B = self.reduceLeft(op)
    override def toArray[B >: A: ClassTag]: Array[B] = self.toArray[B]
    override def toIndexedSeq: IndexedSeq[A] = self.toIndexedSeq
  }

  final class IteratorAdapter[+A](val self: Iterator[A]) extends AnyVal with TraversableOnce[A] {
    override def knownSize: Int = -1
    override def isTraversableAgain: Boolean = self.isTraversableAgain
    override def foreach[U](f: A => U): Unit = self.foreach(f)
  }

  // In Scala 2.13 Option extends IterableOnce but in 2.12 it does not so we need this
  final class OptionAdapter[+A](val self: Option[A]) extends AnyVal with TraversableOnce[A] {
    override def knownSize: Int = if (self.isDefined) 1 else 0
    override def isTraversableAgain: Boolean = true
    override def foreach[U](f: A => U): Unit = self.foreach(f)
  }
}

trait TraversableOnceAdapters {
  import TraversableOnceAdapters._

  implicit def toTraversableOnceFunction1Adapter[A, B](self: A => scala.collection.TraversableOnce[B]): A => TraversableOnce[B] = new TraversableOnceFunction1Adapter(self)
  implicit def toOptionFunction1Adapter[A, B](self: A => Option[B]): A => TraversableOnce[B] = new OptionFunction1Adapter(self)
  implicit def toTraversableOnceAdapter[A](self: scala.collection.TraversableOnce[A]): TraversableOnceAdapter[A] = new TraversableOnceAdapter(self)
  implicit def toIteratorAdapter[A](self: Iterator[A]): IteratorAdapter[A] = new IteratorAdapter(self)
  implicit def toOptionAdapter[A](self: Option[A]): OptionAdapter[A] = new OptionAdapter(self)
}
