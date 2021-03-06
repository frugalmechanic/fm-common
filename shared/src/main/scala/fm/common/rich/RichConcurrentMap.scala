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

import java.util.concurrent.ConcurrentMap

final class RichConcurrentMap[K,V](val map: ConcurrentMap[K,V]) extends AnyVal {
  /**
   * Scala's Map.getOrElse method.
   * 
   * Assumes that the underlying ConcurrentMap does not allow null values.
   */
  @inline def getOrElse(key: K, default: => V): V = {
    val v: V = map.get(key)
    if (null != v) v else default
  }
  
  /**
   * Attempts to only evaluate the value if the key is not present in the map
   */
  @inline def getOrElseUpdate(key: K, makeValue: => V): V = {
    // TODO: figure out (and note!) why I was calling containsKey and not just map.get(key) with null checking
    if (map.containsKey(key)) {
      val v: V = map.get(key)
      if (null != v) return v
    }
    
    // The key doesn't exist so evaluate the value and use putIfAbsent
    val value: V = makeValue
    val existingValue: V = map.putIfAbsent(key, value)
    
    if (existingValue != null) existingValue else value 
  }
  
}