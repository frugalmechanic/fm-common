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

import java.io.File
import org.scalatest.{FunSuite, Matchers}

final class TestClassUtil extends FunSuite with Matchers {
  private val testDirPath: String = "test/classutil"
  private val testDirPaths: Seq[String] = Seq(testDirPath, testDirPath+"/", "/"+testDirPath, "/"+testDirPath+"/")
  private val testDirFiles: Seq[File] = testDirPaths.map{ new File(_) }

  private val testPath: String = "test/classutil/lorem %20ipsum.txt"
  private val testPaths: Seq[String] = Seq(testPath, "/"+testPath)
  private val testFiles: Seq[File] = testPaths.map{ new File(_) }

  private val testClasses: Set[String] = Set("test.classutil.TestClass", "test.classutil.TestClassExtendsTestTrait", "test.classutil.subpackage.TestSubPackageClass", "test.classutil.TestTrait", "test.classutil.TestJavaAnnotatedClass")

  // classExists

  test("classExists - defaultClassLoader - w/package") {
    ClassUtil.classExists("fm.common.ClassUtil") should equal(true)
  }

  test("classExists - defaultClassLoader - w/o package") {
    ClassUtil.classExists("ClassUtil") should equal(false)
  }

  test("classExists - defaultClassLoader - no class") {
    ClassUtil.classExists("fm.common.FooBar") should equal(false)
  }

  //test("classExists - custom classLoader") { }

  test("classpathContentLength") {
    // Simpler to just hard code the length of the classutil/lorem-ipsum.txt file here
    // ls -al lorem-ipsum.txt
    // -rw-r--r--@ 1 eric  staff  2771 Aug  8 13:27 lorem-ipsum.txt

    testPaths.foreach{ ClassUtil.classpathContentLength(_) should equal(2771) }
    testFiles.foreach{ ClassUtil.classpathContentLength(_) should equal(2771) }
  }

  // classpathDirExists

  test("classpathDirExists - directories") {
    testDirPaths.foreach { ClassUtil.classpathDirExists(_) should equal(true) }
    testDirFiles.foreach { ClassUtil.classpathDirExists(_) should equal(true) }
  }

  test("classpathDirExists - files") {
    testPaths.foreach{ ClassUtil.classpathDirExists(_) should equal(false) }
    testFiles.foreach{ ClassUtil.classpathDirExists(_) should equal(false) }
  }

  // classpathFileExists

  test("classpathFileExists - directories") {
    // Test Directories
    testDirPaths.foreach{ ClassUtil.classpathFileExists(_) should equal(false) }
    testDirFiles.foreach{ ClassUtil.classpathFileExists(_) should equal(false) }
  }

  test("classpathFileExists - files") {
    testPaths.foreach { ClassUtil.classpathFileExists(_) should equal(true) }
    testFiles.foreach { ClassUtil.classpathFileExists(_) should equal(true) }
  }

  // classpathFileExists
  /*
  // Directories timestamps get changed every time they get moved to the new resource directory/jar file/etc,
  // This was used to do a manual test for directories, but commenting out to get tests to pass
  test("classpathLastModified - directories") {
    // This is the project-relative path for the directory, and test assumes being ran in the project home
    val f: File = new File("jvm/src/test/resources/test/classutil")
    assert(f.isDirectory, s"$f must be a directory (is the working directory the project home?)")

    testDirPaths.foreach{ ClassUtil.classpathLastModified(_) should equal(f.lastModified) }
    testDirFiles.foreach{ ClassUtil.classpathLastModified(_) should equal(f.lastModified) }
  }*/

  test("classpathLastModified - files") {
    val f: File = new File(s"jvm/src/test/resources/$testPath")
    assert(f.isFile, s"$f must be a file (is the working directory the project home?)")

    testPaths.foreach { ClassUtil.classpathLastModified(_) should equal(f.lastModified) }
    testFiles.foreach { ClassUtil.classpathLastModified(_) should equal(f.lastModified) }
  }

  test("findAnnotatedClasses") {
    ClassUtil.findAnnotatedClasses("test.classutil", classOf[java.lang.Deprecated]) should equal(Set(classOf[_root_.test.classutil.TestJavaAnnotatedClass]))
  }

  test("findClassNames") {
    ClassUtil.findClassNames("test.classutil") should equal(testClasses)
  }

  test("findClassNames - defaultClassLoader - jar file") {
    ClassUtil.findClassNames("scala.collection.immutable") should contain ("scala.collection.immutable.List")
  }

  // Includes recursive file(s)
  test("findClasspathFiles") {
    // Normal Resource Diretory + Class Files
    val expectedFiles: Set[File] = {
      testClasses.map{ _.replace(".", "/") + ".class" } ++ Set(testPath, "test/classutil/subdirectory/subfile.txt")
    }.map{ new File(_) }


    ClassUtil.findClasspathFiles("test.classutil") shouldBe expectedFiles

    // Empty Paths
    ClassUtil.findClasspathFiles("") should not be empty
    ClassUtil.findClasspathFiles("/") should not be empty

    // Jar Files
    ClassUtil.findClasspathFiles("scala.collection") should contain (new File("scala/collection/immutable/List.class"))
  }


  test("findImplementingClasses") {
    ClassUtil.findImplementingClasses("test.classutil", classOf[_root_.test.classutil.TestTrait]) should equal(Set(classOf[_root_.test.classutil.TestClassExtendsTestTrait]))
  }

  /*
    test("def findLoadedClass(cls: String, classLoader: ClassLoader = defaultClassLoader): Option[Class[_]]") { }

    test("def isClassLoaded(cls: String, classLoader: ClassLoader = defaultClassLoader): Boolean") { }
  */

  // Does NOT include recursive file(s)
  test("listClasspathFiles - defaultClassLoader") {
    // Normal Resource Diretory + Class Files
    val updatedTestClasses: Set[String] = testClasses - "test.classutil.subpackage.TestSubPackageClass" // don't include subpackage class

    val expectedFiles: Set[File] = {
      updatedTestClasses.map{ _.replace(".", "/") + ".class" } ++ Set(testPath, "test/classutil/subpackage", "test/classutil/subdirectory")
    }.map{ new File(_) }

    ClassUtil.listClasspathFiles("test.classutil") should equal(expectedFiles)

    // Empty Paths
    ClassUtil.listClasspathFiles("") should not be empty
    ClassUtil.listClasspathFiles("/") should not be empty

    // Jar Files
    ClassUtil.listClasspathFiles("scala.collection") should contain (new File("scala/collection/Seq.class"))
    ClassUtil.listClasspathFiles("scala.collection") should not contain (new File("scala/collection/immutable/List.class"))
  }

  test("requireClass") {
    // This shouldn't throw an exception
    ClassUtil.requireClass("fm.common.ClassUtil", "ClassUtil must exist")

    val msg: String = "my custom exception message"
    val caughtException: Exception = intercept[Exception] { ClassUtil.requireClass("ClassUtil", msg) }

    // Error message is something like ""Missing Class: ClassUtil - my custom exception message", so just look for words containing custom msg
    caughtException.getMessage should include(msg)
  }
}
