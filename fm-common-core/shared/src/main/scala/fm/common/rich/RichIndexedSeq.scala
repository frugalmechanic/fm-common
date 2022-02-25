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

import java.util.concurrent.ThreadLocalRandom

final class RichIndexedSeq[A](val self: scala.collection.IndexedSeq[A]) extends AnyVal {
  /**
   * Choose a random element from this IndexedSeq
   */
  def random: A = self(ThreadLocalRandom.current().nextInt(self.size))
  
  /**
   * Similar to takeWhile except for counting values that match a predicate
   */
  def countWhile(f: A => Boolean): Int = countWhile(0)(f)
  
  /**
   * Like countWhile except takes a starting index
   */
  def countWhile(startingIdx: Int)(f: A => Boolean): Int = {
    require(startingIdx >= 0, "startingIdx must be >= 0")
    
    val len: Int = self.size
    var i: Int = startingIdx
    
    while (i < len && f(self(i))) i += 1
    
    i - startingIdx
  }
}