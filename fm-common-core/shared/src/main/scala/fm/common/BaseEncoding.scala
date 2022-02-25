/*
 * Copyright 2021 Tim Underwood (https://github.com/tpunder)
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

import java.nio.ByteBuffer

trait BaseEncoding {

  def decode(data: Array[Byte]): Array[Byte]
  def decode(data: ByteBuffer): ByteBuffer
  def decode(data: Array[Char]): Array[Byte]
  def decode(data: CharSequence): Array[Byte]

  final def tryDecode(data: Array[Char]): Option[Array[Byte]] = try { Option(decode(data)) } catch { case ex: Exception => None }
  final def tryDecode(data: CharSequence): Option[Array[Byte]] = try { Option(decode(data)) } catch { case ex: Exception => None }

  def encode(bytes: Array[Byte]): String
  def encode(bytes: Array[Byte], offset: Int, length: Int): String
}
