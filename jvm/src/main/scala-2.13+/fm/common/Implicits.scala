/*
 * Copyright 2016 Frugal Mechanic (http://frugalmechanic.com)
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

object Implicits extends Implicits {
  // Duplicated in both the JVM and JS version of JVMImplicitsBase.scala
  implicit class ToImmutableArrayByte   (val col: IterableOnce[Byte])    extends AnyVal { def toImmutableArray: ImmutableArray[Byte]    = ImmutableArray.copy(col) }
  implicit class ToImmutableArrayShort  (val col: IterableOnce[Short])   extends AnyVal { def toImmutableArray: ImmutableArray[Short]   = ImmutableArray.copy(col) }
  implicit class ToImmutableArrayInt    (val col: IterableOnce[Int])     extends AnyVal { def toImmutableArray: ImmutableArray[Int]     = ImmutableArray.copy(col) }
  implicit class ToImmutableArrayLong   (val col: IterableOnce[Long])    extends AnyVal { def toImmutableArray: ImmutableArray[Long]    = ImmutableArray.copy(col) }
  implicit class ToImmutableArrayFloat  (val col: IterableOnce[Float])   extends AnyVal { def toImmutableArray: ImmutableArray[Float]   = ImmutableArray.copy(col) }
  implicit class ToImmutableArrayDouble (val col: IterableOnce[Double])  extends AnyVal { def toImmutableArray: ImmutableArray[Double]  = ImmutableArray.copy(col) }
  implicit class ToImmutableArrayBoolean(val col: IterableOnce[Boolean]) extends AnyVal { def toImmutableArray: ImmutableArray[Boolean] = ImmutableArray.copy(col) }
  implicit class ToImmutableArrayChar   (val col: IterableOnce[Char])    extends AnyVal { def toImmutableArray: ImmutableArray[Char]    = ImmutableArray.copy(col) }

  implicit class ToImmutableArrayAnyRef[T <: AnyRef](val col: IterableOnce[T]) extends AnyVal { def toImmutableArray: ImmutableArray[T] = ImmutableArray.copy[AnyRef](col).asInstanceOf[ImmutableArray[T]] }
}

trait Implicits extends JVMImplicitsBase