/*
 * Copyright 2019 Frugal Mechanic (http://frugalmechanic.com)
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

import scala.collection.{AnyConstr, IterableOps, MapOps}
import scala.collection.immutable.SortedMap

object RichMap {
  /**
   * Some hackery needed to the compiler happy.
   * See https://github.com/scala/scala/blob/8a2cf63ee5bad8c8c054f76464de0e10226516a0/src/library/scala/collection/package.scala#L54-L59
   */
  type AnyConstr[X] = Any
}

final class RichMap[K, +V, +CC[_,_] <: IterableOps[_, RichMap.AnyConstr, _], +C](val self: MapOps[K,V,CC,C]) extends AnyVal {
  @inline def mapValuesStrict[V2](f: V => V2): CC[K,V2] = {
    self.map{ (pair: (K,V)) => (pair._1, f(pair._2)) }
  }

  def toSortedMap(implicit ord: Ordering[K]): SortedMap[K, V] = self match {
    case sorted: SortedMap[_,_] => sorted.asInstanceOf[SortedMap[K,V]]
    case _ =>
      val builder = SortedMap.newBuilder[K,V]
      builder ++= self
      builder.result()
  }

  def toReverseSortedMap(implicit ord: Ordering[K]): SortedMap[K, V] = toSortedMap(ord.reverse)
}