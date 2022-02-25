/*
 * Copyright 2023 Tim Underwood (https://github.com/tpunder)
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

object GrowableCompat {
  type Growable[-A] = scala.collection.mutable.Growable[A]
}

/**
 * Provides a compatibility layer between Scala 2.11/2.12 and 2.13/3.x
 */
trait GrowableCompat[-A] extends GrowableCompat.Growable[A] {
  def addAll(elems: TraversableOnce[A]): this.type = {
    elems.foreach{ addOne(_) }
    this
  }

  @inline final def ++= (elems: TraversableOnce[A]): this.type = {
    addAll(elems)
  }
}
