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

object WithFilterCompat {
  type TraversableOnceOrIterableOnce[+A] = scala.collection.TraversableOnce[A]
}

/**
 * Provides a compatibility layer between Scala 2.11/2.12 and 2.13/3.x
 */
abstract class WithFilterCompat[+A, +CC[_]]  {
  // This is the Scala 2.13/3 Definition of scala.collection.WithFilter
  def map[B](f: A => B): CC[B]
  def flatMap[B](f: A => WithFilterCompat.TraversableOnceOrIterableOnce[B]): CC[B]
  def foreach[U](f: A => U): Unit
  def withFilter(q: A => Boolean): WithFilterCompat[A, CC]
}
