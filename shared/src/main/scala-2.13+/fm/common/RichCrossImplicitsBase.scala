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

import fm.common.rich._
import scala.collection.{IterableOps, MapOps}

object RichCrossImplicitsBase {

}

trait RichCrossImplicitsBase extends Any {
  implicit def toRichIterableOnce[T](t: IterableOnce[T]) = new RichIterableOnce(t)
  implicit def toRichIndexedSeq[T](t: IndexedSeq[T]) = new RichIndexedSeq(t)
  implicit def toRichMap[K, V, CC[_, _] <: IterableOps[_, Any, _], This <: Map[K, V]](m: MapOps[K, V, CC, This]) = new RichMap[K, V, CC, This](m)
}