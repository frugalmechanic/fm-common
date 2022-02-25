/*
 * Copyright 2018 Frugal Mechanic (http://frugalmechanic.com)
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

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets.UTF_8
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

final class TestDigestUtils extends AnyFunSuite with Matchers {
  test("Basic Checks") {
    check(
      msg = "",
      md5Hex = "d41d8cd98f00b204e9800998ecf8427e",
      sha1Hex = "da39a3ee5e6b4b0d3255bfef95601890afd80709",
      sha256Hex = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
    )

    check(
      msg = "a",
      md5Hex = "0cc175b9c0f1b6a831c399e269772661",
      sha1Hex = "86f7e437faa5a7fce15d1ddcb9eaeaea377667b8",
      sha256Hex = "ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb"
    )

    check(
      msg = "Hello World",
      md5Hex = "b10a8db164e0754105b7a99be72e3fe5",
      sha1Hex = "0a4d55a8d778e5022fab701977c5d840bbc486d0",
      sha256Hex = "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e"
    )

    check(
      msg = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin pretium semper pellentesque. Interdum et malesuada fames ac ante ipsum primis in faucibus. Vestibulum vitae nisl faucibus, luctus nisl eget, aliquam enim. Integer risus odio, vehicula a hendrerit eu, lacinia feugiat tellus. Vestibulum nisi nisi, blandit id dapibus et, hendrerit sed massa. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus tincidunt interdum nibh, eu hendrerit ex rhoncus non. Nam ornare magna quis augue ultrices, non lobortis urna venenatis. Aliquam rutrum placerat aliquam. Fusce pellentesque ultrices justo sed vehicula. Mauris vel varius ligula, a condimentum enim. Fusce ornare tellus ac magna.",
      md5Hex = "b53e2bd16a8dafd617a617bcbbdfc3b5",
      sha1Hex = "4a442ca99bf7779f595c7777c8ee15ef50e1e269",
      sha256Hex = "d3a7a726f1ca2a8f37d8137f55a56a4f2be0a413d6f524695219fb1d41d2e197"
    )
  }

  private def check(
    msg: String,
    md5Hex: String,
    sha1Hex: String,
    sha256Hex: String
  ): Unit = {
    // MD5
    DigestUtils.md5Hex(msg) shouldBe md5Hex
    DigestUtils.md5Hex(msg.getBytes(UTF_8)) shouldBe md5Hex
    DigestUtils.md5Hex(new ByteArrayInputStream(msg.getBytes(UTF_8))) shouldBe md5Hex

    DigestUtils.md5(msg) shouldBe Base16.decode(md5Hex)
    DigestUtils.md5(msg.getBytes(UTF_8)) shouldBe Base16.decode(md5Hex)
    DigestUtils.md5(new ByteArrayInputStream(msg.getBytes(UTF_8))) shouldBe Base16.decode(md5Hex)

    // SHA1
    DigestUtils.sha1Hex(msg) shouldBe sha1Hex
    DigestUtils.sha1Hex(msg.getBytes(UTF_8)) shouldBe sha1Hex
    DigestUtils.sha1Hex(new ByteArrayInputStream(msg.getBytes(UTF_8))) shouldBe sha1Hex

    DigestUtils.sha1(msg) shouldBe Base16.decode(sha1Hex)
    DigestUtils.sha1(msg.getBytes(UTF_8)) shouldBe Base16.decode(sha1Hex)
    DigestUtils.sha1(new ByteArrayInputStream(msg.getBytes(UTF_8))) shouldBe Base16.decode(sha1Hex)

    // SHA256
    DigestUtils.sha256Hex(msg) shouldBe sha256Hex
    DigestUtils.sha256Hex(msg.getBytes(UTF_8)) shouldBe sha256Hex
    DigestUtils.sha256Hex(new ByteArrayInputStream(msg.getBytes(UTF_8))) shouldBe sha256Hex

    DigestUtils.sha256(msg) shouldBe Base16.decode(sha256Hex)
    DigestUtils.sha256(msg.getBytes(UTF_8)) shouldBe Base16.decode(sha256Hex)
    DigestUtils.sha256(new ByteArrayInputStream(msg.getBytes(UTF_8))) shouldBe Base16.decode(sha256Hex)
  }
}
