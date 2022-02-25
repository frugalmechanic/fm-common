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
import org.scalatest.AppendedClues
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

final class TestClassUtil extends AnyFunSuite with Matchers with AppendedClues {
  import fm.common.test.classutil._
  
  private val testDirPath: String = "fm/common/test/classutil"
  private val testDirPaths: Seq[String] = Seq(testDirPath, testDirPath+"/", "/"+testDirPath, "/"+testDirPath+"/")
  private val testDirFiles: Seq[File] = testDirPaths.map{ new File(_) }

  private val testPath: String = "fm/common/test/classutil/lorem %20ipsum.txt"
  private val testPaths: Seq[String] = Seq(testPath, "/"+testPath)
  private val testFiles: Seq[File] = testPaths.map{ new File(_) }

  private val testClasses: Set[String] = Set(
    "fm.common.test.classutil.TestClass",
    "fm.common.test.classutil.TestClass$",
    "fm.common.test.classutil.TestClassExtendsTestTrait",
    "fm.common.test.classutil.subpackage.TestSubPackageClass",
    "fm.common.test.classutil.TestTrait",
    "fm.common.test.classutil.TestJavaAnnotatedClass",
    "fm.common.test.classutil.TestObject",
    "fm.common.test.classutil.TestObject$",
    "fm.common.test.classutil.TestObjectExtendsTestTrait",
    "fm.common.test.classutil.TestObjectExtendsTestTrait$"
  )

  // Starting with Scala 3.0 .tasty files are generated alongside the .class files
  val hasTASTYFiles: Boolean = ClassUtil.classpathFileExists("fm/common/test/classutil/TestClass.tasty")

  test("classForName") {
    ClassUtil.classForName("fm.common.test.classutil.TestClass") shouldBe classOf[TestClass]
    ClassUtil.classForName("fm.common.test.classutil.TestClass$") shouldBe TestClass.getClass
  }

  test("getClassForName") {
    ClassUtil.getClassForName("fm.common.test.classutil.TestClass") shouldBe Some(classOf[TestClass])
    ClassUtil.getClassForName("fm.common.test.classutil.TestClass$") shouldBe Some(TestClass.getClass)
  }

  test("companionObject") {
    ClassUtil.companionObject(classOf[TestClass]) shouldBe TestClass
  }

  test("getCompanionObject") {
    ClassUtil.getCompanionObject(classOf[TestClass]) shouldBe Some(TestClass)
  }

  test("isScalaObject") {
    ClassUtil.isScalaObject("fm.common.test.classutil.TestObject$") shouldBe true
    ClassUtil.isScalaObject(TestObject.getClass) shouldBe true
  }

  test("getScalaObject") {
    ClassUtil.getScalaObject(TestObject.getClass) shouldBe Some(TestObject)
  }

  test("getScalaObjectAs") {
    ClassUtil.getScalaObjectAs[TestClass](TestObject.getClass) shouldBe Some(TestObject)
    ClassUtil.getScalaObjectAs(TestObject.getClass, classOf[TestClass]) shouldBe Some(TestObject)

    ClassUtil.getScalaObjectAs[TestTrait](TestObject.getClass) shouldBe None
    ClassUtil.getScalaObjectAs(TestObject.getClass, classOf[TestTrait]) shouldBe None
  }

  // classExists

  test("classExists - defaultClassLoader - w/package") {
    ClassUtil.classExists("fm.common.ClassUtil") shouldBe true
  }

  test("classExists - defaultClassLoader - w/o package") {
    ClassUtil.classExists("ClassUtil") shouldBe false
  }

  test("classExists - defaultClassLoader - no class") {
    ClassUtil.classExists("fm.common.FooBar") shouldBe false
  }

  //test("classExists - custom classLoader") { }

  test("classpathContentLength") {
    // Simpler to just hard code the length of the classutil/lorem-ipsum.txt file here
    // ls -al lorem-ipsum.txt
    // -rw-r--r--@ 1 eric  staff  2771 Aug  8 13:27 lorem-ipsum.txt

    testPaths.foreach{ ClassUtil.classpathContentLength(_) shouldBe 2771 }
    testFiles.foreach{ ClassUtil.classpathContentLength(_) shouldBe 2771 }
  }

  // classpathDirExists

  test("classpathDirExists - directories") {
    testDirPaths.foreach { ClassUtil.classpathDirExists(_) shouldBe true }
    testDirFiles.foreach { ClassUtil.classpathDirExists(_) shouldBe true }
  }

  test("classpathDirExists - files") {
    testPaths.foreach{ ClassUtil.classpathDirExists(_) shouldBe false }
    testFiles.foreach{ ClassUtil.classpathDirExists(_) shouldBe false }
  }

  // classpathFileExists

  test("classpathFileExists - directories") {
    // Test Directories
    testDirPaths.foreach{ ClassUtil.classpathFileExists(_) shouldBe false }
    testDirFiles.foreach{ ClassUtil.classpathFileExists(_) shouldBe false }
  }

  test("classpathFileExists - files") {
    testPaths.foreach { ClassUtil.classpathFileExists(_) shouldBe true }
    testFiles.foreach { ClassUtil.classpathFileExists(_) shouldBe true }
  }

  // classpathFileExists
  /*
  // Directories timestamps get changed every time they get moved to the new resource directory/jar file/etc,
  // This was used to do a manual test for directories, but commenting out to get tests to pass
  test("classpathLastModified - directories") {
    // This is the project-relative path for the directory, and test assumes being ran in the project home
    val f: File = new File("fm-common-core/jvm/src/test/resources/test/classutil")
    assert(f.isDirectory, s"$f must be a directory (is the working directory the project home?)")

    testDirPaths.foreach{ ClassUtil.classpathLastModified(_) shouldBe f.lastModified }
    testDirFiles.foreach{ ClassUtil.classpathLastModified(_) shouldBe f.lastModified }
  }*/

  test("classpathLastModified - files") {
    val f: File = new File(s"fm-common-core/jvm/src/test/resources/$testPath")
    assert(f.isFile, s"$f must be a file (is the working directory the project home?)")

    testPaths.foreach { (path: String) => ClassUtil.classpathLastModified(path) shouldBe f.lastModified withClue path }
    testFiles.foreach { (file: File) => ClassUtil.classpathLastModified(file) shouldBe f.lastModified withClue file }
  }

  test("findAnnotatedClasses") {
    ClassUtil.findAnnotatedClasses("fm.common.test.classutil", classOf[java.lang.Deprecated]) shouldBe Set(classOf[TestJavaAnnotatedClass])
  }

  test("findClassNames") {
    ClassUtil.findClassNames("fm.common.test.classutil") shouldBe testClasses
  }

  test("findClassNames - defaultClassLoader - jar file") {
    ClassUtil.findClassNames("scala.collection.immutable") should contain ("scala.collection.immutable.List")
  }

  // Includes recursive file(s)
  test("findClasspathFiles") {
    // Normal Resource Diretory + Class Files
    val expectedFiles: Set[File] = {
      val res: Set[String] = testClasses.map{ convertResourceToClassFile(_) } ++ Set(testPath, "fm/common/test/classutil/subdirectory/subfile.txt")
      if (hasTASTYFiles) res ++ testClasses.flatMap{ convertResourceToTastyFile(_) } else res
    }.map{ new File(_) }

    ClassUtil.findClasspathFiles("fm.common.test.classutil") shouldBe expectedFiles

    // Empty Paths
    ClassUtil.findClasspathFiles("") should not be empty
    ClassUtil.findClasspathFiles("/") should not be empty

    // Jar Files
    ClassUtil.findClasspathFiles("scala.collection") should contain (new File("scala/collection/immutable/List.class"))
  }

  test("findImplementingObjects") {
    ClassUtil.findImplementingObjects("fm.common.test.classutil", classOf[TestClass]) shouldBe Set(TestObject)
    ClassUtil.findImplementingObjects("fm.common.test.classutil", TestObject.getClass) shouldBe Set(TestObject)
  }


  test("findImplementingClasses") {
    ClassUtil.findImplementingClasses("fm.common.test.classutil", classOf[TestTrait]) shouldBe Set(classOf[TestClassExtendsTestTrait], TestObjectExtendsTestTrait.getClass)
  }

  /*
    test("def findLoadedClass(cls: String, classLoader: ClassLoader = defaultClassLoader): Option[Class[_]]") { }

    test("def isClassLoaded(cls: String, classLoader: ClassLoader = defaultClassLoader): Boolean") { }
  */

  // Does NOT include recursive file(s)
  test("listClasspathFiles - defaultClassLoader") {
    // Normal Resource Diretory + Class Files
    val updatedTestClasses: Set[String] = testClasses - "fm.common.test.classutil.subpackage.TestSubPackageClass" // don't include subpackage class

    val expectedFiles: Set[File] = {
      val res: Set[String] = updatedTestClasses.map{ convertResourceToClassFile(_) } ++ Set(testPath, "fm/common/test/classutil/subpackage", "fm/common/test/classutil/subdirectory")
      if (hasTASTYFiles) res ++ updatedTestClasses.flatMap{ convertResourceToTastyFile(_) } else res
    }.map{ new File(_) }

    ClassUtil.listClasspathFiles("fm.common.test.classutil") shouldBe expectedFiles

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

  private def convertResourceToClassFile(path: String): String = {
    path.replace(".", "/") + ".class"
  }

  private def convertResourceToTastyFile(path: String): Option[String] = {
    // Tasty files are not generated for Scala Object files or Java files (which have "Java" in their name by our test convention)
    if (path.endsWith("$") || path.contains("Java")) None
    else Some(path.replace(".", "/") + ".tasty")
  }
}
