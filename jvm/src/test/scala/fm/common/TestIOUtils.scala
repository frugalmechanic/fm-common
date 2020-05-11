/*
 * Copyright 2015 Frugal Mechanic (http://frugalmechanic.com)
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

import java.io.{BufferedInputStream, File}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

final class TestIOUtils extends AnyFunSuite with Matchers {
  // Test detecting charset encoding
  test("detectCharset - UTF-8 with BOM")    { checkDetectCharset("quickbrown-UTF-8-with-BOM.txt", "UTF-8") }
  test("detectCharset - UTF-8 no BOM")      { checkDetectCharset("quickbrown-UTF-8-no-BOM.txt", "UTF-8") }
  
  test("detectCharset - UTF-16BE with BOM") { checkDetectCharset("quickbrown-UTF-16BE-with-BOM.txt", "UTF-16BE") }
  //test("detectCharset - UTF-16BE no BOM")   { checkDetectCharset("quickbrown-UTF-16BE-no-BOM.txt", "UTF-16BE") }
  
  test("detectCharset - UTF-16LE with BOM") { checkDetectCharset("quickbrown-UTF-16LE-with-BOM.txt", "UTF-16LE") }
  //test("detectCharset - UTF-16LE no BOM")   { checkDetectCharset("quickbrown-UTF-16LE-no-BOM.txt", "UTF-16LE") }
  
  test("detectCharset - UTF-32BE with BOM") { checkDetectCharset("quickbrown-UTF-32BE-with-BOM.txt", "UTF-32BE") }
  //test("detectCharset - UTF-32BE no BOM")   { checkDetectCharset("quickbrown-UTF-32BE-no-BOM.txt", "UTF-32BE") }
  
  test("detectCharset - UTF-32LE with BOM") { checkDetectCharset("quickbrown-UTF-32LE-with-BOM.txt", "UTF-32LE") }
  //test("detectCharset - UTF-32LE no BOM")   { checkDetectCharset("quickbrown-UTF-32LE-no-BOM.txt", "UTF-32LE") }
  
  test("detectCharset - Windows-1252")      { checkDetectCharset("quickbrown-modified-Windows-1252.txt", "Windows-1252") }
  
  private def checkDetectCharset(file: String, charsetName: String): Unit = {
    InputStreamResource.forResource(new File(s"encoding/$file")).flatMap { _.toBufferedInputStream }.foreach { bis: BufferedInputStream =>
      IOUtils.detectCharset(bis, true) shouldBe Some(CharsetUtil.forName(charsetName))
    }
  } 
}

