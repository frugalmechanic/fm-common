/*
 * Copyright 2017 Frugal Mechanic (http://frugalmechanic.com)
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

import scala.math.Ordered

object UUIDWrapper {
  /**
   * All UUID methods are available to an UUIDWrapper instance via this Implicit
   */
  implicit def toUUID[T <: UUIDWrapper[T]](wrapper: UUIDWrapper[T]): UUID = wrapper.uuid
}

/**
 * An UUID wrapper class that allow you to define custom types that represent an UUID.
 *
 * The intended usage pattern is something like:
 * {{{
 *   object UserId extends UUIDFactory[UserId](new UserId(_))
 *   final class UserId(val uuid: UUID) extends UUIDWrapper[UserId]
 * }}}
 *
 * This allows you to then reference UserId instead of UUID and to have methods that
 * take a strongly typed UserId instead of an UUID (which could represent something other
 * than a User id)
 *
 * Note: The class that extends this should also extends AnyVal so that serialization
 *       will just pass through to the underlying uuid
 */
trait UUIDWrapper[T <: UUIDWrapper[T]] extends Any with Ordered[T] {
  def uuid: UUID

  // toString is always the hex representation of the UUID
  // since there are too many places we potentially just call
  // .toString on an id.
  final override def toString: String = uuid.toString

  final def compare(that: T): Int = uuid.compareTo(that.uuid)
}
