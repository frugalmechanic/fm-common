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

import java.nio.charset.StandardCharsets.UTF_8
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

final class TestCrypto extends AnyFunSuite with Matchers {

  test("PBKDF2") {
    pbkdf2(
      ivHex = "f6d49d390035b6d9a34656c79c12ea3b2b4d65a08c3dcdb666dbaf3378fd8fb8",
      password = "",
      iterationCount = 1,
      expectedHex = "2ce2a31e6b7176d6ea9de0f48e205034c6bc3de87e4f01bbba47aabd231b5f9a"
    )

    pbkdf2(
      ivHex = "f6d49d390035b6d9a34656c79c12ea3b2b4d65a08c3dcdb666dbaf3378fd8fb8",
      password = "",
      iterationCount = 10000,
      expectedHex = "c04ab1fc4bc05bdf4c80925f35b2e1d315f2070246ab887dbe7e8cab14843419"
    )

    pbkdf2(
      ivHex = "f6d49d390035b6d9a34656c79c12ea3b2b4d65a08c3dcdb666dbaf3378fd8fb8",
      password = "foo",
      iterationCount = 10000,
      expectedHex = "262b5c8b42413faaf0ad792ae537770b4447c1dc7b5ed1bea508ace2a8644daa"
    )

    pbkdf2(
      ivHex = "f6d49d390035b6d9a34656c79c12ea3b2b4d65a08c3dcdb666dbaf3378fd8fb8",
      password = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      iterationCount = 20000,
      expectedHex = "49ee2e84355cabfcc2ed39554b038f636dd1517126d1a93d36faa43398d79002"
    )

    pbkdf2(
      ivHex = "f6d49d390035b6d9a34656c79c12ea3b2b4d65a08c3dcdb666dbaf3378fd8fb8",
      password = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin pretium semper pellentesque. Interdum et malesuada fames ac ante ipsum primis in faucibus. Vestibulum vitae nisl faucibus, luctus nisl eget, aliquam enim. Integer risus odio, vehicula a hendrerit eu, lacinia feugiat tellus. Vestibulum nisi nisi, blandit id dapibus et, hendrerit sed massa. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus tincidunt interdum nibh, eu hendrerit ex rhoncus non. Nam ornare magna quis augue ultrices, non lobortis urna venenatis. Aliquam rutrum placerat aliquam. Fusce pellentesque ultrices justo sed vehicula. Mauris vel varius ligula, a condimentum enim. Fusce ornare tellus ac magna.",
      iterationCount = 200000,
      expectedHex = "7a2f436c531c6a5fead186338da4efbe19f0b9771bb2edfe758db553d01cd40c"
    )
  }

  def pbkdf2(ivHex: String, password: String, iterationCount: Int, expectedHex: String): Unit = {
    Crypto.PBKDF2.sha256Hex(Base16.decode(ivHex), password, iterationCount) shouldBe expectedHex
    Crypto.PBKDF2.sha256Hex(Base16.decode(ivHex), password.toCharArray, iterationCount) shouldBe expectedHex

    Crypto.PBKDF2.sha256(Base16.decode(ivHex), password, iterationCount) shouldBe Base16.decode(expectedHex)
    Crypto.PBKDF2.sha256(Base16.decode(ivHex), password.toCharArray, iterationCount) shouldBe Base16.decode(expectedHex)
  }


  test("Basic Encryption Key Sizes") {

    // Should use sha256 hash
    encrypt("", "Hello World")
    encrypt("a", "Hello World")
    encrypt("abc", "Hello World")

    // Should use key exactly
    encrypt("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "Hello World")

    // Should truncate
    encrypt("dce104043477fa295bb97c509d6fb662a9dead3f943d64580f3ce78e1ec22c01", "Hello World")
    encrypt("121021ec251fdb27b442ddba4847b92c4bb71594071a2b7746e2ecf8beea0223", "Hello World")
  }

  test("Encryption Keys of various sizes") {
    var key: String = ""
    (0 to 1024).foreach{i =>
      encrypt(key, "Hello World")
      key += "a"
    }
  }

  test("Encryption/Decryption of various sizes") {
    val key: String = "dce104043477fa295bb97c509d6fb662a9dead3f943d64580f3ce78e1ec22c01"

    var s: String = ""

    (0 to 1024).foreach{ i =>
      encrypt(key, s)
      s += "a"
    }
  }

  test("Encryption/MAC Interop Checks") {
    encrypt(
      key = "",
      msg = "Hello World",
      macHex = Some("b66af62997f62731c356c5109d12e0f1a8ba79cc"),
      macBase64 = Some("tmr2KZf2JzHDVsUQnRLg8ai6ecw="),
      macBase64URLSafe = Some("tmr2KZf2JzHDVsUQnRLg8ai6ecw="),
      encryptedBase64Default = Some("EUnSvZbLU7SPbmfxOaODOQAAudLhPHwMw0bCTCETh6U="),
      encryptedBase64URLSafeDefault = Some("aWbJ-CU02rCqxiDWwXX1yEzyhIFjgx6VI5I0WPcsrqE="),
      encryptedBase64Authenticated = Some("gvV/mm3s/pbp0HKxbGLAlDKIDGl3+4rlKjZzq2QYAkbhHz1O9MsaSTWhtg=="),
      encryptedBase64URLSafeAuthenticated = Some("FVaylXfcEKDyNtvvpeoY74apnNujKqygu2N1-MRIiJ_iixpDzdWq76rIMA==")
    )

    encrypt(
      key = "a",
      msg = "Hello World",
      macHex = Some("86f65c0bda63a68cdaa640ef1e2a60719fe3ef73"),
      macBase64 = Some("hvZcC9pjpozapkDvHipgcZ/j73M="),
      macBase64URLSafe = Some("hvZcC9pjpozapkDvHipgcZ_j73M="),
      encryptedBase64Default = Some("+KVsjqxVbd38ZjlEg6bfET54fsr7AbEpDCJylPrwcC4="),
      encryptedBase64URLSafeDefault = Some("ytaAtdtDqR5XZzxUnPEob8WvNaHr70GFMdmZWz2PTOc="),
      encryptedBase64Authenticated = Some("wjp8byzoDd/E2UXrD4lkfvPFJQWOpvLVcF30y2KLqGmYw9uHdYyHz1EBGg=="),
      encryptedBase64URLSafeAuthenticated = Some("9fcuYpVgR_xXStviETn_thmI_5c5jfTX2ijscnQDh7WFlW2IvoZmrG53pg==")
    )

    encrypt(
      key = "abc",
      msg = "Hello World",
      macHex = Some("9fd4a15d66d02eacd685e615957f61e2ff0d2dfe"),
      macBase64 = Some("n9ShXWbQLqzWheYVlX9h4v8NLf4="),
      macBase64URLSafe = Some("n9ShXWbQLqzWheYVlX9h4v8NLf4="),
      encryptedBase64Default = Some("xgZVddUI8+SdnDIRrSWBLa7dx3Mmce1N7+oyzBahwuE="),
      encryptedBase64URLSafeDefault = Some("Unx4yuuwNtAluPP0thxyloVKKftW8o3Mn2SiXi3Sk2M="),
      encryptedBase64Authenticated = Some("TBEiKzIp+L/waZeq0k3+j1LSdcJBV2CDd25zKhW8R14RD9b3R93HPxJlJQ=="),
      encryptedBase64URLSafeAuthenticated = Some("Yu4lWSqP6IgQ52HCQA1l-HsxW-afYJLcjrZiPvr0Bg9wcR8SExfPeO04gg==")
    )

    encrypt(
      key = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
      msg = "Hello World",
      macHex = Some("e424d94a4cec049ef3320700730ee942af51c48f"),
      macBase64 = Some("5CTZSkzsBJ7zMgcAcw7pQq9RxI8="),
      macBase64URLSafe = Some("5CTZSkzsBJ7zMgcAcw7pQq9RxI8="),
      encryptedBase64Default = Some("ifv6wveneEqpjs92Yb7dz4TfU+RNBXDnbJElXNCe/oU="),
      encryptedBase64URLSafeDefault = Some("dqUxoy1PeUlrNVnQB1yceiB_4v40A-hvjG_QCP341tY="),
      encryptedBase64Authenticated = Some("7Gwf1OpCAyeKhqJiUo+K3e1GvqOJ0CaT35ThVqqq3y3VPaSAKSlzhoklPA=="),
      encryptedBase64URLSafeAuthenticated = Some("RKSpJHdeyr_SbV12H-XyL_PpoVuivYFoqis1q1Fu3FVE_Nv6pff_8DnapA==")
    )

    encrypt(
      key = "dce104043477fa295bb97c509d6fb662a9dead3f943d64580f3ce78e1ec22c01",
      msg = "Hello World",
      macHex = Some("f368676ca67f6c4a1aeef284624c431671fd98f9"),
      macBase64 = Some("82hnbKZ/bEoa7vKEYkxDFnH9mPk="),
      macBase64URLSafe = Some("82hnbKZ_bEoa7vKEYkxDFnH9mPk="),
      encryptedBase64Default = Some("NxAZRK7VeaaxTnDcNdEfw2gPiIcpZqsqd2dWCMU3g+w="),
      encryptedBase64URLSafeDefault = Some("B0VpQEeU5RrJjItXF7otQff0y78WcaoWVQvvXtSKULA="),
      encryptedBase64Authenticated = Some("Iwj+PotL4cFIKGnXxsasI1YyfZ5rdQyYz/Dad8q7cD7zGI7kZCyMV7FEoA=="),
      encryptedBase64URLSafeAuthenticated = Some("lMTNidtc5es7Wr82ALtgNX42PrM5eir3SveZDoomSGK_BjZSF_QdX8yj-w==")
    )

    encrypt(
      key = "121021ec251fdb27b442ddba4847b92c4bb71594071a2b7746e2ecf8beea0223",
      msg = "Hello World",
      macHex = Some("12392a706eff821a2ac0f14d13ec5b63179923a4"),
      macBase64 = Some("EjkqcG7/ghoqwPFNE+xbYxeZI6Q="),
      macBase64URLSafe = Some("EjkqcG7_ghoqwPFNE-xbYxeZI6Q="),
      encryptedBase64Default = Some("4QYuP1dhe113lr8jkfcKfkfXxvMA9LL/UY/aRvlcOUw="),
      encryptedBase64URLSafeDefault = Some("5A3JNffMxhqU0bd61l58QC9f6XwG9dS3oyDaGASb2o0="),
      encryptedBase64Authenticated = Some("RBYDD4pRJjVkoKbURO0/UcRdEpJ5UIUlZY6lNyZavbOcQVwRthgjSqaBpg=="),
      encryptedBase64URLSafeAuthenticated = Some("7Z-yybdwyfShu6mFpHEKSxfMNQpQPA-IEQcR6Inp2VW44qGcqh_KKWC-YQ==")
    )

    encrypt(
      key = "121021ec251fdb27b442ddba4847b92c4bb71594071a2b7746e2ecf8beea0223",
      msg = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin pretium semper pellentesque. Interdum et malesuada fames ac ante ipsum primis in faucibus. Vestibulum vitae nisl faucibus, luctus nisl eget, aliquam enim. Integer risus odio, vehicula a hendrerit eu, lacinia feugiat tellus. Vestibulum nisi nisi, blandit id dapibus et, hendrerit sed massa. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus tincidunt interdum nibh, eu hendrerit ex rhoncus non. Nam ornare magna quis augue ultrices, non lobortis urna venenatis. Aliquam rutrum placerat aliquam. Fusce pellentesque ultrices justo sed vehicula. Mauris vel varius ligula, a condimentum enim. Fusce ornare tellus ac magna.",
      macHex = Some("bdde2402118e631f2e2efcb73b91a90041c2f630"),
      macBase64 = Some("vd4kAhGOYx8uLvy3O5GpAEHC9jA="),
      macBase64URLSafe = Some("vd4kAhGOYx8uLvy3O5GpAEHC9jA="),
      encryptedBase64Default = Some("L6PEAERv3pDntKyE3dvpfxbeRKvH0S+FmI8IgX+kq2PI0KV4qYN+MEEf52K2taDSxXQAUM8FBh9cUTA2Ac7K/1QUc8pT0izcTRc94WdAIo1aGdmsn8gFCA234FAW1BcKnHJ3W8PeZd7PORfgWjiRjxU/ZKLukvws/QCuG1ac1EMLLt+ns11naTndma2QHtT1FhBduRlkQgjkzVRslfJGNaOtmFVEWIVIIatTqEPQFtM70HUCDBIs0DsqQRAevX+sc2micHGwlDTxUchk/Z4BNk0615XEFbL2xiEZDJB8kgqDMVZpkdbNrQY7ZDueSLVedCh2/ZEfQ8S523/SC/kT9CcAsbl8ttRo3z4cpybcUG00bORX2e4WvBFHIiRVav/Du2u7dJeXbbTw10N02i3BBg4cKC9MOL46QTQ929vKfbRsJ2xh7gulfc6RKHil0LFjhdCrU0WAqZ85Y3z+Nkste/bdqj8qg5gAfbL2PDcenSq+Uzn2w4dgZFCmdMVqpckb0krskgoRSZHJzCKL/5d2uTtOsBoi54/LZyg5pALfqKbB7O9IWeHLKjxVqsNDPw7+aZ17tujIHpDvXyrc0jF9KLwRGmTNmgk+ljWRHbNmzBbGbEF9eOBon63xchl/feRTzGVYZhwTTSjb3ExvPtrIkxFP6OtPb91+h7wWJCuhM0RBa5cWPYE2cIY/gXJE7l6JgxRRlTUVdgyRUxBa7PdVkpA/14HXR0sF/RqhBtv9zU2rUEB8Pc8mCYZVWXWg920i6IsjFmSxUr3V+mhBc3jnRXceyG8+QqWzp+LmLHtVso18/cGEy6rbYft3UdvdycW8wrmidYSlAFGnbVpp7xChRi5QsttdRTWgjzitFB0Z/gYtcBbEqT5zhGO5CbOCOcF+wR5+tza8a+LVKcxpjmk3budr2RG1dC+OIK76eLmOtT0L8PVcTL20Udr/KtxUH5pe"),
      encryptedBase64URLSafeDefault = Some("Np4J98FcHCNdPVMHGoE0Q5vUjc6w_1Z_IjGUXYCi4BDZpEq0GcL955a6Sj5zuWO42K5Q4m5KmzOelYk92WyW_GWen8MWEQc0sKXoffEwhvd92a1ttDRac6F1PB2__atkUOGOAiKCpBrTSY9hspk-Gihmr1v6Zm7B9dw6n6Q4EURq8Y84AO6bqzR7dTh0CyKElsraSd6cMfIoQkQqKBangMIJpWQFdVMzLgBqxalgBXbn42OMWJoYvIIQyw2aSBzSdUJt1ZKXriaqf9dz9fjHuS_ir76DTZpIrS9Tt1JP04Cozb1Cmmptge_jyzW3k3IejWEtB-Mhx6y4_boe6iZ8d1Q1JoptF1FsY3Om-_s5d932bFTzY_jzhYapGFKZSOAr4ylJ14ATzQnMhlG83ihATqq60kodN51QENbNTEeeyPDAj_EV4UT9CAKSZTK6fbJGMyPfYoByXimMtcdrnoIdtHV-0v-2sjhL-ZNfyXn4U2a-sq3uJQG3c4uxbwWImsfmxss9xAaXqLe6c9nWx9oXJj741IkYk_o9N-uIDhSwV2N6wjbK_qJMhnEe5LNIuOnOA6lOinbDuhX_pdGk4PmdIpNv5vrA-11TDbDD-uspX4do7mRyMD0NHNrDBEYSRHbFP2ls_QfmkAZ0lH36faNWoEQISFLhNtPkK1Kcb6rgc5OhjUlzfciuZJdmXhvUuogH7uMiIfocSttaX6RCVGqpM5fqBO9WbPDUEPRTgyyONgf1yITVZV61R7yfFxHfmcnviw7TwXGJDqlj7o96FpRKFlDHjZ18zYyuXvN5gHWCfnwZBrCCPWjJgi9ixOP5WC_4jnXJiFBt0hDT9yit_YnNxxAKPzv9B3X9Hz38KRwmwi85pDPuC8bEWtE5XTyowsbvFmo6UjYBS2dgX1QcTCeh3tXQ1ZV6JaqZQmpjjSsf2hw9tHDba8JPu0oyBA-Amlbh"),
      encryptedBase64Authenticated = Some("TmcFV0DJIVDvfJVstbE6qfgQAQUPrXH4MFU5g8lPvcOp0PW1S7XhdztB28SvhM7aI7HYFTn/B8pJO7MSL8SVRjcKs/R7eBUnmjppgY6YxKBWJH7ymk3R57lM4VoEx9+yJ/799xhE40JtE9eYvEf4ZQ5hRUG6/nICARip1/S3RzueQYivd5/pMwMO5WZvzs3+H9De0K6BwZHHZPXcbEbYaN46qLNQPQ6DS4JrtTNV94gIgoayrzi4AN3FrJ2qIM0ZrEam7NH1AvXktZmgN8e2+mKecGwBYUAlWt8GZqKipXPMzlWnbRtQAicd932sQGFoS+mO6F0uJUh8fD6/ejjlM22Z8ODNa2M4dPR4YeX/cMx+ziKzpbgXxtFM+Qb5fQ/XK4yXFzKCGzxsNEQmSlyQjUHUc9F8bnyRQy4MPU+iQ/0duDUAVL+Q0L9TjmzKPwpq2IWIxfHSXGKJYqDR770UauXRxtv61+Xcgp0na0uxsaIIPkUxlBJzDC+1jhLN+ftS4RlgZsFCOAhagJj/VYdP655QUrxyrLTvwCWed3yjIHVlEpjmt+IOuLG15157w9x2o/+ieS6fzNTvZgLDCFoN+VcEfUMYk0YBjUe3zY/fJOjxC6VKBM2a371eoRik0kfQXIyBH7T2bqdt6OxmEch18BXyWRCmyP066uEMgxI6Oxmy8uVZOh5rrDv9VulDCDYc+G7xwf0qfMvME4ixuNhwmnk4aJah55fWojhYgnm/f4bEkMFQCD+zPXrNdpe2j5Z69pS70unczvrrKPz5NX04NIWR1L9PjRCv2VaHgrD4L+9qihJ8KuEXn8YzqiJYKLuLa6jnQ3RNAIXksKIJ5Zew3Qh9Mi+PUIHu5YYIWhczmzt9ZNvB2U1yjaXgJvNVZ5eAXP/bUPko5w3zmlrSqikUWOpFvTydeda6crMIIhm4keVUcsX2b6DyNyRGhH5aAV3MbkX3GLAqXQ=="),
      encryptedBase64URLSafeAuthenticated = Some("t6nvsXB1nC_ria2p6iX6IpqB7YbzsB0VXVqozcgT3aJdYVHnPPVQ6AHQP-QaTFYy5IifNc3GV_XKeUWLuJAwk0dhSrKuq2TfShcWU4ggPbh-JrJ-BymGj4K2BUDsRo7DKw1bx9v5wWS-8J3YyjRy12oG-GihtJ4B1dIli1Obsxyoi8wm_KymsB4OuiTJyNRU7cbAnJxx3S3NgKMpD2h4DB7WkReIJZV2yeLeUA6TXUu-gAxN7Bm9JWY4jOqIF7axk3Q4C4QjnbWZV5Jfi4Vpmm8cNy87c-fh0CAf5rPj1A2dIYYYgvZzLhqzHnaMzdv5MkknUE5L2WJjrTQUbdj5o4F9qshcEONFkO7piSmflyoUTQBnWO4INhfLEb0CVBHGFt2cGv7nPOtCK42gyIEAafcoV2gOsGALJdAhKc398fr3PY1_GBGckAPpfy-Zn0YeciOtMUsjUtsSHIcPKWGKdBuIv5ZGNjcRC_QouQkep4GiSv1XtSh1mbJlWWO6seasxws4S5XATE5OmEL2foC5eMTnZyBd4rJQsoA68kicEsbvzXUT67kHHqGiQ8cDQClM1rfOI28LftRR3gbBM3S_tvjv-C5ic3V7gpalvn4SHTAACben9cNBmxbFt5f8rkDkniecj9uozD47qyksR4ar5_fgzwQRmRPCHFfwlsul4D70Sin2RqUdEkmY4yTWNSwvUFNBTc7aL8e2GPFxNE4x9ZeYMbmA3xfcEmzAvrRe2PHxEtX7hMM3n4tWSjic3HmGtIecdAOonBdLt10SAC8T_-d_dvLAl1PYKsQY6tXOrsShT5kBrMalhekpCNSKoUN6wGIc_x7It8F4v5gMgggcLxbG7AnnNnG21kNwAfQl7Morfxp-gG0m_wHM4vmXtp2i70zrMMpVBRlNJrez57VAL04nvXM6vqVn_qLsCQO-0uFf5CFmR2pgBbQ6HJZf_JOnoNChLCGWiA==")
    )
  }

  private def encrypt(
    key: String,
    msg: String,
    macHex: Option[String] = None,
    macBase64: Option[String] = None,
    macBase64URLSafe: Option[String] = None,
    encryptedBase64Default: Option[String] = None,
    encryptedBase64URLSafeDefault: Option[String] = None,
    encryptedBase64Authenticated: Option[String] = None,
    encryptedBase64URLSafeAuthenticated: Option[String] = None
  ): Unit = {
    val defaultCrypto: Crypto = Crypto.defaultCipherForRawKey(key.getBytes(UTF_8))
    val authenticatedCrypto: Crypto = Crypto.authenticatedCipherForRawKey(key.getBytes(UTF_8))

    List(
      defaultCrypto,
      authenticatedCrypto
    ).foreach{ c: Crypto =>
      c.decryptBase64String(c.encryptBase64String(msg)) shouldBe msg
      c.decryptBase64String(c.encryptBase64StringURLSafe(msg)) shouldBe msg

      c.tryDecryptBase64String(c.encryptBase64String(msg)) shouldBe Some(msg)
      c.tryDecryptBase64String(c.encryptBase64StringURLSafe(msg)) shouldBe Some(msg)

      if (macHex.isDefined) c.macHex(msg) shouldBe macHex.get
      if (macBase64.isDefined) c.macBase64(msg) shouldBe macBase64.get
      if (macBase64URLSafe.isDefined) c.macBase64URLSafe(msg) shouldBe macBase64URLSafe.get
    }

    if (encryptedBase64Default.isDefined) defaultCrypto.decryptBase64String(encryptedBase64Default.get) shouldBe msg
    if (encryptedBase64URLSafeDefault.isDefined) defaultCrypto.decryptBase64String(encryptedBase64URLSafeDefault.get) shouldBe msg

    if (encryptedBase64Authenticated.isDefined) authenticatedCrypto.decryptBase64String(encryptedBase64Authenticated.get) shouldBe msg
    if (encryptedBase64URLSafeAuthenticated.isDefined) authenticatedCrypto.decryptBase64String(encryptedBase64URLSafeAuthenticated.get) shouldBe msg

    Vector(
      "not_valid_encrypted_value",
      "not_valid_encrypted_value_not_valid_encrypted_value_not_valid_encrypted_value_not_valid_encrypted_value_not_valid_encrypted_value",
      "12345678",
      "1234567812345678",
      "123456781234567812345678",
      "12345678123456781234567812345678",
      "1234567812345678123456781234567812345678",
      "123456781234567812345678123456781234567812345678"
    ).foreach { invalid: String =>
      an [Exception] should be thrownBy authenticatedCrypto.decryptBase64String(invalid)
      authenticatedCrypto.tryDecryptBase64String(invalid) shouldBe None

      an [Exception] should be thrownBy authenticatedCrypto.decryptBase64String(Base64.encode(invalid.getBytes(UTF_8)))
      authenticatedCrypto.tryDecryptBase64String(Base64.encode(invalid.getBytes(UTF_8))) shouldBe None
    }
  }
}
