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

import scala.collection.mutable

/**
 * Wraps a Scala mutable.HashMap inside of a ThreadLocal and exposes
 * some simple operations.
 *
 * The initial use case of this is for caching NumberFormat instances
 * by Locale.  This can be accomplished by overriding the initialValue
 * method and just calling the apply() method with the Locale.
 *
 * @tparam K The HashMap Key
 * @tparam V The HashMap Value
 */
class ThreadLocalHashMap[K,V] {
  private val threadLocal: ThreadLocal[mutable.HashMap[K,V]] = new ThreadLocal[mutable.HashMap[K,V]]{
    override def initialValue: mutable.HashMap[K,V] = new mutable.HashMap[K,V]
  }

  private def getMap: mutable.HashMap[K,V] = threadLocal.get()

  final def apply(key: K): V = get(key).getOrElse{ throw new NoSuchElementException(key.toString) }

  final def get(key: K): Option[V] = {
    val m: mutable.HashMap[K,V] = getMap

    val res: Option[V] = m.get(key)

    if (res.isDefined) res else {
      val init: Option[V] = initialValue(key)
      if (init.nonEmpty) m(key) = init.get
      init
    }
  }

  final def update(key: K, value: V): Unit = getMap.update(key, value)

  protected def initialValue(key: K): Option[V] = None
}
