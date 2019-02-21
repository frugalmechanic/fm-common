/*
 * Copyright 2019 Frugal Mechanic (http://frugalmechanic.com)
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
package fm.common.rich

import org.scalajs.dom.document
import org.scalajs.dom.raw.Element
import org.scalatest.{FunSuite, Matchers}
import scala.scalajs.js.JavaScriptException

final class TestRichNode extends FunSuite with Matchers {
  import fm.common.Implicits.toRichNode

  private def newListItem(): Element = {
    val newNode = document.createElement("li")
    newNode.innerHTML = "new node"
    newNode.id = "new"

    newNode
  }

  private def setupInsertAfterUL(): Unit = {
    document.body.innerHTML =
      s"""
         |<ul id="outer-ul">
         |  <li id="one">one</li>
         |  <li id="two">
         |    <ul id="inner-ul">
         |      <li id="inner-one">inner-one</li>
         |      <li id="inner-two">inner-two</li>
         |    </ul>
         |  </li>
         |  <li id="three">three</li>
         |</ul>
       """.stripMargin.linesIterator.map{ _.trim }.mkString("") // remove whitespace/newlines
  }



  private def testFullInsertAfterImpl(parentId: String = "outer-ul", targetId: String, useParent: Boolean = true, expectedCount: Int = 4): Unit = {
    setupInsertAfterUL()

    val ul: Element = document.getElementById(parentId)
    ul.childElementCount should equal(3)

    testInsertAfterImpl(parentId, targetId, useParent, expectedCount)
  }

  private def testInsertAfterImpl(parentId: String = "outer-ul", targetId: String, useParent: Boolean = true, expectedCount: Int): Unit = {
    withClue(s"testInsertAfterImpl($parentId, $targetId, $useParent, $expectedCount)"){
      val ul: Element = document.getElementById(parentId)
      val targetChild: Element = document.getElementById(targetId)

      // Insert after
      val newNode: Element = newListItem()
      if (useParent) ul.insertAfter(newNode, targetChild)
      else targetChild.insertAfter(newNode)

      // Verify Insert
      ul.childElementCount should equal(expectedCount)
      targetChild.nextElementSibling should equal(newNode)
    }
  }

  test("hasNextSibling") {
    def testHasNextSiblingImpl(id: String, expected: Boolean): Unit = {
      withClue(s"hasNextSibling: $id") {
        document.getElementById(id).hasNextSibling should equal(expected)
      }
    }

    setupInsertAfterUL()

    testHasNextSiblingImpl("outer-ul", false)
    testHasNextSiblingImpl("one", true)
    testHasNextSiblingImpl("two", true)
    testHasNextSiblingImpl("three", false)
    testHasNextSiblingImpl("inner-ul", false)
    testHasNextSiblingImpl("inner-one", true)
    testHasNextSiblingImpl("inner-two", false)
  }

  test("insertAfter(node: Node, refChild: Node)") {
    testFullInsertAfterImpl(targetId = "one")
    testFullInsertAfterImpl(targetId = "two")
    testFullInsertAfterImpl(targetId = "three")

    // Add another new node to ensure it is targetable
    testInsertAfterImpl(targetId = "new", expectedCount = 5)

    assertThrows[JavaScriptException] {
      testInsertAfterImpl(targetId = "no-target", expectedCount = 0)
    }
  }

  test("insertAfter(node: Node)") {
    testFullInsertAfterImpl(targetId = "one", useParent = false)
    testFullInsertAfterImpl(targetId = "two", useParent = false)
    testFullInsertAfterImpl(targetId = "three", useParent = false)

    // Add another new node again
    testInsertAfterImpl(targetId = "new", useParent = false, expectedCount = 5)

    assertThrows[JavaScriptException] {
      testInsertAfterImpl(targetId = "no-target", useParent = false, expectedCount = 0)
    }
  }

  test("insertAfter - inner append") {
    setupInsertAfterUL()

    val ul = document.getElementById("inner-ul")
    ul.childElementCount should equal(2)

    testInsertAfterImpl("inner-ul", targetId = "inner-one", expectedCount = 3)
    testInsertAfterImpl("inner-ul", targetId = "inner-two", expectedCount = 4)
  }

  test("insertAfter - inner append - from outer - throws exception") {
    setupInsertAfterUL()

    val ul = document.getElementById("inner-ul")
    ul.childElementCount should equal(2)

    assertThrows[JavaScriptException] {
      testInsertAfterImpl("outer-ul", targetId = "inner-one", useParent = true, expectedCount = 3)
    }
  }
}
