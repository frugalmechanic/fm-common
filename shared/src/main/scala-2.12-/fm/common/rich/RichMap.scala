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
package fm.common.rich

import scala.collection.generic.CanBuildFrom
import scala.collection.{MapLike, mutable}
import scala.collection.immutable.SortedMap

final class RichMap[A, B, This <: MapLike[A,B,This] with scala.collection.Map[A,B]](val self: MapLike[A,B,This]) extends AnyVal {
  /**
   * The normal Map.mapValues method produces a view which is not what we usually want.
   * This is a strict version of it.
   *
   * https://issues.scala-lang.org/browse/SI-4776
   */
  @inline def mapValuesStrict[C, That <: scala.collection.Map[A, C]](f: B => C)(implicit bf: CanBuildFrom[This, (A, C), That]): That = {
    val builder: mutable.Builder[(A, C), That] = bf.apply()
    self.foreach{ case (k,v) => builder += k -> f(v) }
    builder.result
  }

  def toSortedMap(implicit ord: Ordering[A]): SortedMap[A, B] = self match {
    case sorted: SortedMap[_,_] => sorted.asInstanceOf[SortedMap[A,B]]
    case _ =>
      val builder = SortedMap.newBuilder[A,B]
      builder ++= self
      builder.result
  }

  def toReverseSortedMap(implicit ord: Ordering[A]): SortedMap[A, B] = toSortedMap(ord.reverse)
}