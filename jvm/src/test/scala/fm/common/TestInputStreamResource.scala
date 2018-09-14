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

import org.scalatest.{FunSuite, Matchers}
import java.io.File

final class TestInputStreamResource extends FunSuite with Matchers {
  test(".tar.gz")    { checkCompression("hello_world.txt.tar.gz") }
  test(".tgz")       { checkCompression("hello_world.txt.tgz") }
  test(".tar.bz")    { checkCompression("hello_world.txt.tar.bz") }
  test(".tar.bz2")   { checkCompression("hello_world.txt.tar.bz2") }
  test(".tar.bzip2") { checkCompression("hello_world.txt.tar.bzip2") }
  test(".tbz2")      { checkCompression("hello_world.txt.tbz2") }
  test(".tbz")       { checkCompression("hello_world.txt.tbz") }
  test(".tar.xz")    { checkCompression("hello_world.txt.tar.xz") }
  test(".tar")       { checkCompression("hello_world.txt.tar") }
  test(".gz")        { checkCompression("hello_world.txt.gz") }
  test(".bzip2")     { checkCompression("hello_world.txt.bzip2") }
  test(".bz2")       { checkCompression("hello_world.txt.bz2") }
  test(".bz")        { checkCompression("hello_world.txt.bz") }
  test(".snappy")    { checkCompression("hello_world.txt.snappy") }
  test(".xz")        { checkCompression("hello_world.txt.xz") }
  test(".zip")       { checkCompression("hello_world.txt.zip") }
  test(".jar")       { checkCompression("hello_world.txt.jar") }
  
  private def checkCompression(name: String): Unit = {
    InputStreamResource.forResource(new File(s"compression/$name")).readToString("UTF-8") should equal ("Hello World!\n")
  }
  
  /*
   * Test Reading from Different File Encodings
   */
  
  test("UTF-8 with BOM")    { checkEncoding("quickbrown-UTF-8-with-BOM.txt") }
  test("UTF-8 no BOM")      { checkEncoding("quickbrown-UTF-8-no-BOM.txt") }
  
  test("UTF-16BE with BOM") { checkEncoding("quickbrown-UTF-16BE-with-BOM.txt") }
  //test("UTF-16BE no BOM")   { checkEncoding("quickbrown-UTF-16BE-no-BOM.txt") }
  
  test("UTF-16LE with BOM") { checkEncoding("quickbrown-UTF-16LE-with-BOM.txt") }
  //test("UTF-16LE no BOM")   { checkEncoding("quickbrown-UTF-16LE-no-BOM.txt") }
  
  test("UTF-32BE with BOM") { checkEncoding("quickbrown-UTF-32BE-with-BOM.txt") }
  //test("UTF-32BE no BOM")   { checkEncoding("quickbrown-UTF-32BE-no-BOM.txt") }
  
  test("UTF-32LE with BOM") { checkEncoding("quickbrown-UTF-32LE-with-BOM.txt") }
  //test("UTF-32LE no BOM")   { checkEncoding("quickbrown-UTF-32LE-no-BOM.txt") }
  
  private val QuickBrownTest: String = {
    InputStreamResource.forResource(new File(s"encoding/quickbrown-UTF-8-no-BOM.txt")).readToString("UTF-8")
  }
  
  private def checkEncoding(file: String): Unit = {
    InputStreamResource.forResource(new File(s"encoding/$file")).readToString() should equal(QuickBrownTest)
  }
}