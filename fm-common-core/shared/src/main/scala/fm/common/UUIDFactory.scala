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

import java.util.Date
import scala.math.Ordering

/**
 * This goes along with the UUIDWrapper
 *
 * See documentation of UUIDWrapper for the intended usage of this class
 */
abstract class UUIDFactory[T <: UUIDWrapper[T]](create: UUID => T) {
  final val Zero: T = create(UUID.Zero)
  final val UnsignedMaxValue: T = create(UUID.UnsignedMaxValue)
  final val SignedMaxValue: T = create(UUID.SignedMaxValue)
  final val SignedMinValue: T = create(UUID.SignedMinValue)

  final def apply(): T = create(UUID())

  // Note: This gets overridden if you use a case class
  def apply(uuid: UUID): T = create(uuid)

  def apply(uuid: Date): T = create(UUID(uuid))
  def apply(uuid: ImmutableDate): T = create(UUID(uuid))
  def apply(uuid: Instant): T = create(UUID(uuid))

  def apply(bytes: ImmutableArray[Byte]): T = create(UUID(bytes))
  def apply(bytes: Array[Byte]): T = create(UUID(bytes))

  def forEpochMilli(epochMilli: Long): T = create(UUID.forEpochMilli(epochMilli))

  final def apply(uuid: String): T = create(UUID(uuid))
  final def get(uuid: String): Option[T] = if (UUID.isValid(uuid)) Some(apply(uuid)) else None
  final def isValid(uuid: String): Boolean = UUID.isValid(uuid)

  // Can use this in match expressions
  final def unapply(s: String): Option[T] = get(s)

  implicit object ordering extends Ordering[T] { def compare(a: T, b: T): Int = a.compare(b) }
}