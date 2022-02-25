/*
 * Copyright 2022 Tim Underwood (https://github.com/tpunder)
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

object BuilderCompat {
  type TraversableOnceOrIterableOnce[+A] = scala.collection.IterableOnce[A]
}

/**
 * Provides a compatibility layer between Scala 2.11/2.12 and 2.13/3.x
 */
trait BuilderCompat[-A, +To] extends scala.collection.mutable.Builder[A, To] with GrowableCompat[A] {
  // Everything we need is in GrowableCompat
}
