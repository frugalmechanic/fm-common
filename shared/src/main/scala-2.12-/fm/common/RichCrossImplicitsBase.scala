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

trait RichCrossImplicitsBase extends Any {
  implicit def toRichTraversableOnce[T](t: scala.collection.TraversableOnce[T]) = new RichTraversableOnce(t)
  implicit def toRichIndexedSeq[T](t: scala.collection.IndexedSeq[T]) = new RichIndexedSeq(t)
  implicit def toRichMap[A,B,This <: scala.collection.MapLike[A,B,This] with Map[A,B]](m: scala.collection.MapLike[A,B,This]) = new RichMap(m)
}