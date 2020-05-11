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
package fm.common

import java.nio.charset.StandardCharsets.UTF_8
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

final class TestHex extends AnyFunSuite with Matchers {
  private[this] val data: Vector[(String,String)] = Vector(
    "" -> "",
    "Hello World" -> "48656c6c6f20576f726c64",
    "abcdefghijklmnopqrstuvwxyz" -> "6162636465666768696a6b6c6d6e6f707172737475767778797a",
    "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz" -> "6162636465666768696a6b6c6d6e6f707172737475767778797a6162636465666768696a6b6c6d6e6f707172737475767778797a",
    """abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+-=><,./';":][}{\|""" -> "6162636465666768696a6b6c6d6e6f707172737475767778797a4142434445464748494a4b4c4d4e4f505152535455565758595a3132333435363738393021402324255e262a28295f2b2d3d3e3c2c2e2f273b223a5d5b7d7b5c7c",
    new String((0 to 127).map{ _.toByte }.toArray, UTF_8) -> "000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f303132333435363738393a3b3c3d3e3f404142434445464748494a4b4c4d4e4f505152535455565758595a5b5c5d5e5f606162636465666768696a6b6c6d6e6f707172737475767778797a7b7c7d7e7f"
  )
  
  test("Basic Encoding and Decoding") {
    data.foreach{ case (original, encoded) => check(original, encoded) }
  }
  
  private def check(original: String, encoded: String): Unit = {
    val bytes: Array[Byte] = original.getBytes(UTF_8)
    
    Hex.encodeHex(bytes) shouldBe encoded.toCharArray
    Hex.encodeHex(bytes, true) shouldBe encoded.toCharArray // Lowercase by default
    Hex.encodeHex(bytes, false) shouldBe encoded.toUpperCase.toCharArray
    Hex.encodeHexString(bytes) shouldBe encoded
    
    Hex.decodeHex(encoded.toCharArray) shouldBe bytes
    Hex.decodeHex(encoded.toLowerCase.toCharArray) shouldBe bytes
    Hex.decodeHex(encoded.toUpperCase.toCharArray) shouldBe bytes
  }
}
