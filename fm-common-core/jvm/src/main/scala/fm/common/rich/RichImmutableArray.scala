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
package fm.common.rich

import com.github.benmanes.caffeine.cache.{CacheLoader, Caffeine, LoadingCache}
import fm.common.{ImmutableArray, Interner}
import fm.common.ImmutableArray

object RichImmutableArray {
  // Needed on Scala 2.11. Other versions of Scala can automatically perform this functional interface conversion
  private implicit def toCacheLoader[K, V](f: K => V): CacheLoader[K, V] = new CacheLoader[K, V] {
    override def load(key: K): V = f(key)
  }

  private val interners: LoadingCache[Class[_], Interner[ImmutableArray[_]]] = Caffeine.newBuilder()
    .weakKeys()
    .build{ (_: Class[_]) =>
    Interner[ImmutableArray[_]]()
  }
}

final class RichImmutableArray[A](val arr: ImmutableArray[A]) extends AnyVal {
  def intern: ImmutableArray[A] = RichImmutableArray.interners.get(arr.getClass)(arr).asInstanceOf[ImmutableArray[A]]
}