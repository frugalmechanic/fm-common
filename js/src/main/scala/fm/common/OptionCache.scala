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

object OptionCache extends OptionCacheBase {
  // No caching for the JS version:
  override def valueOf(v: Char): Some[Char] = Some(v)
  override def valueOf(v: Byte): Some[Byte] = Some(v)
  override def valueOf(v: Short): Some[Short] = Some(v)
  override def valueOf(v: Int): Some[Int] = Some(v)
  override def valueOf(v: Long): Some[Long] = Some(v)
}
