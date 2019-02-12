/*
 * Copyright 2019 Frugal Mechanic (http://frugalmechanic.com)
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

protected abstract class OptionCacheBase {
  private[this] val True: Some[Boolean] = Some(true)
  private[this] val False: Some[Boolean] = Some(false)

  final def valueOf(v: Boolean): Some[Boolean] = if (v) True else False

  def valueOf(v: Char): Some[Char]
  def valueOf(v: Byte): Some[Byte]
  def valueOf(v: Short): Some[Short]
  def valueOf(v: Int): Some[Int]
  def valueOf(v: Long): Some[Long]
}
