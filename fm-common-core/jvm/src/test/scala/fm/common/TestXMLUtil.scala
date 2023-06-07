/*
 * Copyright 2014 Frugal Mechanic (http://frugalmechanic.com)
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

final class TestXMLUtil extends AnyFunSuite with Matchers {
  
  test("isXml") {
    def isXML(s: String): Boolean = {
      XMLUtil.isXML(s) && XMLUtil.isXML(new ByteArrayInputStream(s.getBytes(UTF_8)))
    }
    
    isXML("foo") shouldBe false
    isXML("foo<hello>") shouldBe false
    
    isXML("<hello>") shouldBe true // This looks like XML which is why it's true
    isXML("<!-- foo --><hello>") shouldBe true
    isXML("""<?xml version="1.0" encoding="ISO-8859-1"?><ACES version="2.0">""") shouldBe true
  }

  test("isValidXML") {
    def isValidXML(s: String): Boolean = XMLUtil.isValidXML(s)

    isValidXML("foo") shouldBe false
    isValidXML("foo<hello>") shouldBe false

    isValidXML("<hello>") shouldBe false
    isValidXML("<hello></foo>") shouldBe false
    isValidXML("<hello></hello>") shouldBe true
    isValidXML("  <hello>  </hello>  ") shouldBe true
    isValidXML("<hello></hello>asd") shouldBe false
    isValidXML("<hello></hello>  <!-- foo -->  ") shouldBe true
    isValidXML("<!-- foo --><hello>") shouldBe false
    isValidXML("<!-- foo --><hello></foo>") shouldBe false
    isValidXML("<!-- foo --><hello></hello>") shouldBe true
    isValidXML("""<?xml version="1.0" encoding="ISO-8859-1"?><ACES version="2.0">""") shouldBe false
    isValidXML("""<?xml version="1.0" encoding="ISO-8859-1"?><ACES version="2.0"><foo></foo></ACES>""") shouldBe true
  }
}