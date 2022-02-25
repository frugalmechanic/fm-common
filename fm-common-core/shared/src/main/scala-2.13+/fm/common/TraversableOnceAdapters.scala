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

import scala.collection.IterableOnceOps
import scala.reflect.ClassTag

object TraversableOnceAdapters {
  final class IterableOnceFunction1Adapter[A, B](val self: A => IterableOnce[B]) extends (A => TraversableOnce[B]) {
    override def apply(a: A): TraversableOnce[B] = new IterableOnceAdapter(self(a))
  }

  // This is a value class so cannot be directly defined in the TraversableOnceAdapters trait
  final class IterableOnceAdapter[+A](val self: IterableOnce[A]) extends AnyVal with TraversableOnce[A] {
    override def knownSize: Int = self.knownSize

    override def isTraversableAgain: Boolean = {
      self match {
        case it: IterableOnceOps[_, _, _] => it.isTraversableAgain // IterableOnce does not have isTraversableAgain
        case _ => false
      }
    }

    override def foreach[U](f: A => U): Unit = self.iterator.foreach(f)
    override def foldLeft[B](z: B)(op: (B, A) => B): B = self.iterator.foldLeft(z)(op)
    override def reduceLeft[B >: A](op: (B, A) => B): B = self.iterator.reduceLeft(op)
    override def toArray[B >: A: ClassTag]: Array[B] = self.iterator.toArray[B]
    override def toIndexedSeq: IndexedSeq[A] = self.iterator.toIndexedSeq
  }

  // This is a value class so cannot be directly defined in the TraversableOnceAdapters trait
  final class IteratorAdapter[+A](val self: Iterator[A]) extends AnyVal with TraversableOnce[A] {
    override def knownSize: Int = self.knownSize
    override def isTraversableAgain: Boolean = self.isTraversableAgain
    override def foreach[U](f: A => U): Unit = self.foreach(f)
  }
}

trait TraversableOnceAdapters {
  import TraversableOnceAdapters._

  implicit def toIterableOnceFunction1Adapter[A, B](self: A => IterableOnce[B]): A => TraversableOnce[B] = new IterableOnceFunction1Adapter(self)
  implicit def toIterableOnceAdapter[A](self: IterableOnce[A]): IterableOnceAdapter[A] = new IterableOnceAdapter(self)
  implicit def toIteratorAdapter[A](self: Iterator[A]): IteratorAdapter[A] = new IteratorAdapter(self)
}
