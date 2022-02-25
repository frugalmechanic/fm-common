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

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, IOException, InputStream, OutputStream}
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets.UTF_8
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

final class TestBase64 extends AnyFunSuite with Matchers {
  private[this] val data: Vector[(String,String)] = Vector(
    "" -> "",
    "Hello World" -> "SGVsbG8gV29ybGQ=",
    "abcdefghijklmnopqrstuvwxyz" -> "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXo=",
    "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz" -> "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5eg==",
    """abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+-=><,./';":][}{\|""" -> "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjEyMzQ1Njc4OTAhQCMkJV4mKigpXystPT48LC4vJzsiOl1bfXtcfA==",
    new String((0 to 127).map{ _.toByte }.toArray, UTF_8) -> "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygpKissLS4vMDEyMzQ1Njc4OTo7PD0+P0BBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWltcXV5fYGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6e3x9fn8=",
    new String((0 to 1024).flatMap{ Character.toChars(_).toSeq }.toArray) -> "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygpKissLS4vMDEyMzQ1Njc4OTo7PD0+P0BBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWltcXV5fYGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6e3x9fn/CgMKBwoLCg8KEwoXChsKHwojCicKKwovCjMKNwo7Cj8KQwpHCksKTwpTClcKWwpfCmMKZwprCm8Kcwp3CnsKfwqDCocKiwqPCpMKlwqbCp8KowqnCqsKrwqzCrcKuwq/CsMKxwrLCs8K0wrXCtsK3wrjCucK6wrvCvMK9wr7Cv8OAw4HDgsODw4TDhcOGw4fDiMOJw4rDi8OMw43DjsOPw5DDkcOSw5PDlMOVw5bDl8OYw5nDmsObw5zDncOew5/DoMOhw6LDo8Okw6XDpsOnw6jDqcOqw6vDrMOtw67Dr8Oww7HDssOzw7TDtcO2w7fDuMO5w7rDu8O8w73DvsO/xIDEgcSCxIPEhMSFxIbEh8SIxInEisSLxIzEjcSOxI/EkMSRxJLEk8SUxJXElsSXxJjEmcSaxJvEnMSdxJ7En8SgxKHEosSjxKTEpcSmxKfEqMSpxKrEq8SsxK3ErsSvxLDEscSyxLPEtMS1xLbEt8S4xLnEusS7xLzEvcS+xL/FgMWBxYLFg8WExYXFhsWHxYjFicWKxYvFjMWNxY7Fj8WQxZHFksWTxZTFlcWWxZfFmMWZxZrFm8WcxZ3FnsWfxaDFocWixaPFpMWlxabFp8WoxanFqsWrxazFrcWuxa/FsMWxxbLFs8W0xbXFtsW3xbjFucW6xbvFvMW9xb7Fv8aAxoHGgsaDxoTGhcaGxofGiMaJxorGi8aMxo3GjsaPxpDGkcaSxpPGlMaVxpbGl8aYxpnGmsabxpzGncaexp/GoMahxqLGo8akxqXGpsanxqjGqcaqxqvGrMatxq7Gr8awxrHGssazxrTGtca2xrfGuMa5xrrGu8a8xr3Gvsa/x4DHgceCx4PHhMeFx4bHh8eIx4nHiseLx4zHjceOx4/HkMeRx5LHk8eUx5XHlseXx5jHmceax5vHnMedx57Hn8egx6HHosejx6THpcemx6fHqMepx6rHq8esx63Hrsevx7DHsceyx7PHtMe1x7bHt8e4x7nHuse7x7zHvce+x7/IgMiByILIg8iEyIXIhsiHyIjIiciKyIvIjMiNyI7Ij8iQyJHIksiTyJTIlciWyJfImMiZyJrIm8icyJ3InsifyKDIociiyKPIpMilyKbIp8ioyKnIqsiryKzIrciuyK/IsMixyLLIs8i0yLXItsi3yLjIuci6yLvIvMi9yL7Iv8mAyYHJgsmDyYTJhcmGyYfJiMmJyYrJi8mMyY3JjsmPyZDJkcmSyZPJlMmVyZbJl8mYyZnJmsmbyZzJncmeyZ/JoMmhyaLJo8mkyaXJpsmnyajJqcmqyavJrMmtya7Jr8mwybHJssmzybTJtcm2ybfJuMm5ybrJu8m8yb3Jvsm/yoDKgcqCyoPKhMqFyobKh8qIyonKisqLyozKjcqOyo/KkMqRypLKk8qUypXKlsqXypjKmcqaypvKnMqdyp7Kn8qgyqHKosqjyqTKpcqmyqfKqMqpyqrKq8qsyq3KrsqvyrDKscqyyrPKtMq1yrbKt8q4yrnKusq7yrzKvcq+yr/LgMuBy4LLg8uEy4XLhsuHy4jLicuKy4vLjMuNy47Lj8uQy5HLksuTy5TLlcuWy5fLmMuZy5rLm8ucy53Lnsufy6DLocuiy6PLpMuly6bLp8uoy6nLqsury6zLrcuuy6/LsMuxy7LLs8u0y7XLtsu3y7jLucu6y7vLvMu9y77Lv8yAzIHMgsyDzITMhcyGzIfMiMyJzIrMi8yMzI3MjsyPzJDMkcySzJPMlMyVzJbMl8yYzJnMmsybzJzMncyezJ/MoMyhzKLMo8ykzKXMpsynzKjMqcyqzKvMrMytzK7Mr8ywzLHMssyzzLTMtcy2zLfMuMy5zLrMu8y8zL3Mvsy/zYDNgc2CzYPNhM2FzYbNh82IzYnNis2LzYzNjc2OzY/NkM2RzZLNk82UzZXNls2XzZjNmc2azZvNnM2dzZ7Nn82gzaHNos2jzaTNpc2mzafNqM2pzarNq82sza3Nrs2vzbDNsc2yzbPNtM21zbbNt824zbnNus27zbzNvc2+zb/OgM6BzoLOg86EzoXOhs6HzojOic6KzovOjM6Nzo7Oj86QzpHOks6TzpTOlc6WzpfOmM6ZzprOm86czp3Ons6fzqDOoc6izqPOpM6lzqbOp86ozqnOqs6rzqzOrc6uzq/OsM6xzrLOs860zrXOts63zrjOuc66zrvOvM69zr7Ov8+Az4HPgs+Dz4TPhc+Gz4fPiM+Jz4rPi8+Mz43Pjs+Pz5DPkc+Sz5PPlM+Vz5bPl8+Yz5nPms+bz5zPnc+ez5/PoM+hz6LPo8+kz6XPps+nz6jPqc+qz6vPrM+tz67Pr8+wz7HPss+zz7TPtc+2z7fPuM+5z7rPu8+8z73Pvs+/0IA="
  )
  
  test("Basic Encoding and Decoding") {
    data.foreach{ case (original, encoded) => check(original, encoded) }
  }
  
  test("URLToStrictInputStream") {
    Vector(
      "" -> "",
      "+" -> "+",
      "/" -> "/",
      "-" -> "+",
      "_" -> "/",
      "_-_-_-_-_-_-_-_-_-_-" -> "/+/+/+/+/+/+/+/+/+/+",
      "------------" -> "++++++++++++",
      "____________" -> "////////////",
      "+_=-/" -> "+/=+/",
      """abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+-=><,./';":][}{\|""" -> """abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()/++=><,./';":][}{\|"""
      
    ).foreach { case (in, expected) => 
      checkURLToStrictInputStreamRead(in, expected)
      
      (1 to in.length).foreach { (size: Int) =>
        checkURLToStrictInputStreamReadArray(in, expected, size)
      }
      
      for {
        off <- (1 to in.length)
        len <- (1 to in.length)
      } {
        checkURLToStrictInputStreamReadArrayOffLen(in, expected, off, len)
      }
    }
  }
  
  private def check(original: String, encoded: String): Unit = {
    val bytes: Array[Byte] = original.getBytes(UTF_8)
    val urlEncoded: String = toURL(encoded)
    val encodedNoPadding: String = stripPadding(encoded)
    val urlEncodedNoPadding: String = stripPadding(urlEncoded)
    
    Base64.encode(bytes) shouldBe encoded
    Base64Strict.encode(bytes) shouldBe encoded
    
    Base64.encodeNoPadding(bytes) shouldBe encodedNoPadding
    Base64Strict.encodeNoPadding(bytes) shouldBe encodedNoPadding
    
    Base64.encodeURL(bytes) shouldBe urlEncoded
    Base64URL.encode(bytes) shouldBe urlEncoded
    
    Base64.encodeURLNoPadding(bytes) shouldBe urlEncodedNoPadding
    Base64URL.encodeNoPadding(bytes) shouldBe urlEncodedNoPadding
    
    Base64.decode(encoded) should equal(bytes)
    Base64.decode(encoded.toCharArray) should equal(bytes)
    Base64.decode(encoded.getBytes(StandardCharsets.UTF_8)) should equal(bytes)
    Base64.decode(ByteBuffer.wrap(encoded.getBytes(StandardCharsets.UTF_8))).array() should equal(bytes)
    Base64Strict.decode(encoded) should equal(bytes)
    Base64Strict.decode(encoded.toCharArray) should equal(bytes)
    Base64Strict.decode(encoded.getBytes(StandardCharsets.UTF_8)) should equal(bytes)
    Base64Strict.decode(ByteBuffer.wrap(encoded.getBytes(StandardCharsets.UTF_8))).array() should equal(bytes)
    
    Base64.decode(urlEncoded) should equal(bytes)
    Base64.decode(urlEncoded.toCharArray) should equal(bytes)
    Base64.decode(urlEncoded.getBytes(StandardCharsets.UTF_8)) should equal(bytes)
    Base64.decode(ByteBuffer.wrap(urlEncoded.getBytes(StandardCharsets.UTF_8))).array() should equal(bytes)
    Base64URL.decode(urlEncoded) should equal(bytes)
    Base64URL.decode(urlEncoded.toCharArray) should equal(bytes)
    Base64URL.decode(urlEncoded.getBytes(StandardCharsets.UTF_8)) should equal(bytes)
    Base64URL.decode(ByteBuffer.wrap(urlEncoded.getBytes(StandardCharsets.UTF_8))).array() should equal(bytes)

    // 2021-09-28 - Extra padding is no longer supported after converting over to java.util.Base64

//    // Extra Padding
//    Base64.decode(encoded+"=") should equal(bytes)
//    Base64.decode(encoded+"==") should equal(bytes)
//    Base64.decode(encoded+"===") should equal(bytes)
//    Base64.decode(encoded+"====") should equal(bytes)
//
//    // Extra Padding
//    Base64Strict.decode(encoded+"=") should equal(bytes)
//    Base64Strict.decode(encoded+"==") should equal(bytes)
//    Base64Strict.decode(encoded+"===") should equal(bytes)
//    Base64Strict.decode(encoded+"====") should equal(bytes)
//
//    // Extra Padding
//    Base64.decode(urlEncoded+"=") should equal(bytes)
//    Base64.decode(urlEncoded+"==") should equal(bytes)
//    Base64.decode(urlEncoded+"===") should equal(bytes)
//    Base64.decode(urlEncoded+"====") should equal(bytes)
//
//    // Extra Padding
//    Base64URL.decode(urlEncoded+"=") should equal(bytes)
//    Base64URL.decode(urlEncoded+"==") should equal(bytes)
//    Base64URL.decode(urlEncoded+"===") should equal(bytes)
//    Base64URL.decode(urlEncoded+"====") should equal(bytes)
    
    writeToOS(bytes){ new Base64.OutputStream(_) } shouldBe encoded
    writeToOS(bytes){ new Base64Strict.OutputStream(_) } shouldBe encoded
    writeToOS(bytes){ new Base64URL.OutputStream(_) } shouldBe urlEncoded
    
    readUsingIS(encoded){ new Base64.InputStream(_) } shouldBe bytes
    readUsingIS(urlEncoded){ new Base64.InputStream(_) } shouldBe bytes
    readUsingIS(encoded){ new Base64Strict.InputStream(_) } shouldBe bytes
    readUsingIS(urlEncoded){ new Base64URL.InputStream(_) } shouldBe bytes
    
    if (Vector('+','-','/','_').exists{ encoded.indexOf(_) != -1 }) {
      an [IllegalArgumentException] should be thrownBy Base64URL.decode(encoded)
      an [IllegalArgumentException] should be thrownBy Base64URL.decode(encoded.toCharArray)
      an [IllegalArgumentException] should be thrownBy Base64Strict.decode(urlEncoded)
      an [IllegalArgumentException] should be thrownBy Base64Strict.decode(urlEncoded.toCharArray)
      
      an [IOException] should be thrownBy readUsingIS(urlEncoded){ new Base64Strict.InputStream(_) }
      an [IOException] should be thrownBy readUsingIS(encoded){ new Base64URL.InputStream(_) }
    }
  }
  
  private def checkURLToStrictInputStreamRead(in: String, expected: String): Unit = {
    val is: InputStream = makeURLToStrictInputStream(in)
    val os: ByteArrayOutputStream = new ByteArrayOutputStream()
    
    var ch: Int = is.read()
    while (-1 != ch) {
      os.write(ch)
      ch = is.read()
    }
    
    new String(os.toByteArray(), UTF_8) shouldBe expected
  }
  
  private def checkURLToStrictInputStreamReadArray(in: String, expected: String, size: Int): Unit = {
    val is: InputStream = makeURLToStrictInputStream(in)
    val os: ByteArrayOutputStream = new ByteArrayOutputStream()
    
    val b: Array[Byte] = new Array(size)
    
    var len: Int = is.read(b)
    while (-1 != len) {
      os.write(b, 0, len)
      len = is.read(b)
    }
    
    new String(os.toByteArray(), UTF_8) shouldBe expected
  }
  
  private def checkURLToStrictInputStreamReadArrayOffLen(in: String, expected: String, off: Int, len: Int): Unit = {
    val is: InputStream = makeURLToStrictInputStream(in)
    val os: ByteArrayOutputStream = new ByteArrayOutputStream()
    val extra: Int = 10
    
    val b: Array[Byte] = new Array(off + len + extra)
    
    var actual: Int = is.read(b, off, len)
    while (-1 != actual) {
      os.write(b, off, actual)
      actual = is.read(b, off, len)
    }
    
    new String(os.toByteArray(), UTF_8) shouldBe expected
  }
  
  private def makeURLToStrictInputStream(in: String): InputStream = new Base64.URLToStrictInputStream(new ByteArrayInputStream(in.getBytes(UTF_8)))
  
  //private def readToString(is: InputStream): String = new String(is.toByteArray, UTF_8)
  
  private def writeToOS(bytes: Array[Byte])(wrapOS: OutputStream => OutputStream): String = {
    val bos: ByteArrayOutputStream = new ByteArrayOutputStream()
    val os: OutputStream = wrapOS(bos)
    os.write(bytes)
    os.close()
    new String(bos.toByteArray(), UTF_8)
  }
  
  private def readUsingIS(encoded: String)(wrapIS: InputStream => InputStream): Array[Byte] = {
    val is: InputStream = wrapIS(new ByteArrayInputStream(encoded.getBytes(UTF_8)))
    is.toByteArray()
  }
  
  private def toURL(s: String): String = s.replace('+', '-').replace('/', '_')
  //private def toStrict(s: String): String = s.replace('-', '+').replace('_', '/')
  private def stripPadding(s: String): String = s.replaceAll("=", "")
}
