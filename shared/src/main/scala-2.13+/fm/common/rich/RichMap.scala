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

import scala.collection.{BuildFrom, IterableOps, MapOps, mutable}
import scala.collection.immutable.SortedMap

final class RichMap[A, B, CC[_, _] <: IterableOps[_, Any, _], This <: Map[A, B]](val self: MapOps[A, B, CC, This]) extends AnyVal {
  /**
   * The normal Map.mapValues method produces a view which is not what we usually want.
   * This is a strict version of it.
   *
   * https://issues.scala-lang.org/browse/SI-4776
   */
  @inline def mapValuesStrict[C, That <: Map[A, C]](f: B => C)(implicit bf: BuildFrom[This, (A, C), That]): That = {
    val builder: mutable.Builder[(A, C), That] = bf.newBuilder(self.repr)
    self.foreach{ case (k: A @unchecked, v: B @unchecked) => builder += k -> f(v) }
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
