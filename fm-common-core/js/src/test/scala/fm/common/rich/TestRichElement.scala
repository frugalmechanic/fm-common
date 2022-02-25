package fm.common.rich

import fm.common.ElementType
import org.scalajs.dom.document
import org.scalajs.dom.raw.{Element, HTMLDivElement, HTMLInputElement}
import fm.common.Implicits.toRichElement
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import scala.reflect.ClassTag

final class TestRichElement extends AnyFunSuite with Matchers with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = {
    document.body.innerHTML =
      s"""
         |<!-- Closest test examples -->
         |<input type="text" class="target-one" id="closest-in-body-target-one" value="closest-in-body-target-one"/>
         |<input type="text" class="target-two" id="closest-in-body-target-two" value="closest-in-body-target-two"/>
         |<button type="button" id="closest-in-body-trigger-element"></button>
         |
         |<input type="text" id="failure-1"/>
         |<div class="input-group" id="closest-is-sibling">
         |  <input type="text" class="target-one" id="closest-sibling-closest-target-one" value="closest-sibling-closest-target-one"/>
         |  <input type="text" class="target-two" id="closest-sibling-closest-target-two" value="closest-sibling-closest-target-two"/>
         |  <span>foo</span>
         |  <button type="button" id="closest-sibling-trigger-element">closest-sibling-trigger-element</button>
         |  <input type="password" id="failure-2"/>
         |</div>
         |
         |<div class="input-group" id="closest-is-child-of-ancestor">
         |  <input type="text" class="target-one" id="closest-is-child-of-ancestor-target-one" value="closest-is-child-of-ancestor-target-one"/>
         |  <span>foo</span>
         |  <div>
         |    <input type="text" class="target-two" id="closest-is-child-of-ancestor-target-two" value="closest-is-child-of-ancestor-target-two"/>
         |  </div>
         |  <div>
         |    <button type="button" id="closest-is-child-of-ancestor-trigger-element">closest-is-child-of-ancestor-trigger-element</button>
         |    <input type="text" id="failure-3"/>
         |  </div>
         |  <input type="text" id="failure-4"/>
         |</div>
         |
         |<div class="input-group" id="closest-is-deep-child-of-ancestor">
         |  <div class="one">
         |    <input type="text" class="target-one" id="closest-is-deep-child-of-ancestor-target-one" value="closest-is-deep-child-of-ancestor-target-one"/>
         |    <div class="two">
         |      <input type="text" class="target-two" id="closest-is-deep-child-of-ancestor-target-two" value="closest-is-deep-child-of-ancestor-target-two"/>
         |    </div>
         |    <input type="text" class="target-three" id="closest-is-deep-child-of-ancestor-target-three" value="closest-is-deep-child-of-ancestor-target-three"/>
         |  </div>
         |  <div>
         |    <button type="button" id="closest-is-deep-child-of-ancestor-trigger-element">closest-is-deep-child-of-ancestor-trigger-element</button>
         |    <input type="text"/>
         |  </div>
         |  <input type="text" value="closest-is-deep-child-of-ancestor-target-four"/>
         |</div>
         |
         |<div class="nested-div" id="nested-div-outside">
         |  <div id="nested-div-inside-one">
         |
         |    <div class="nested-div" id="nested-div-inside-one-child-one">
         |      <div class="nested-div" id="nested-div-inside-one-child-one-grandchild-one"></div>
         |      <div id="nested-div-inside-one-child-two-grandchild-two"></div>
         |    </div>
         |    <div class="nested-div" id="nested-div-inside-one-child-two">
         |      <div class="nested-div" id="nested-div-inside-one-child-two-grandchild-one"></div>
         |      <div class="nested-div" id="nested-div-inside-one-child-two-grandchild-two"></div>
         |    </div>
         |    <div id="nested-div-inside-one-child-three">
         |      <div class="nested-div" id="nested-div-inside-one-child-three-grandchild-one"></div>
         |      <div class="nested-div" id="nested-div-inside-one-child-three-grandchild-two"></div>
         |    </div>
         |  </div>
         |
         |  <div class="nested-div" id="nested-div-inside-two">
         |  </div>
         |</div>
         |
         |<!-- toggle class -->
         |
         |<i class="glyphicon glyphicon-eye-open" id="toggle-class-icon"></i>
         |
         |<!-- toggle attribute value -->
         |
         |<input type="password" id="toggle-attribute-value">
       """.stripMargin.linesIterator.map{ _.trim }.mkString("") // remove whitespace/newlines
  }

  // With Selector
  private def closestWithAncestorChildrenTest[T <: Element: ClassTag](fromElementID: String, selector: String, expectedID: String)(implicit ev: ElementType[T]): Unit = {
    val element: Element = document.getElementById(fromElementID)
    val closestElement: Element = element.closestWithAncestorChildren[T](selector)
    val closestID: String = closestElement.id

    withClue(s"closestWithAncestorChildrenTest($fromElementID, $selector, $expectedID)") {
      closestID should equal(expectedID)
    }
  }

  // Without Selector
  private def closestWithAncestorChildrenTest[T <: Element: ClassTag](fromElementID: String, expectedID: String)(implicit ev: ElementType[T]): Unit = {
    val element: Element = document.getElementById(fromElementID)
    val closestElement = element.closestWithAncestorChildren[T]
    val closestID: String = closestElement.id

    withClue(s"closestWithAncestorChildrenTest($fromElementID, $expectedID)") {
      closestID should equal(expectedID)
    }
  }

  test("closestWithAncestorChildren - finding the nearest input") {
    closestWithAncestorChildrenTest[HTMLInputElement]("closest-in-body-trigger-element", "closest-in-body-target-two")
    closestWithAncestorChildrenTest[HTMLInputElement]("closest-in-body-trigger-element", "input", "closest-in-body-target-two")
    closestWithAncestorChildrenTest[HTMLInputElement]("closest-in-body-trigger-element", "input[class='target-one']", "closest-in-body-target-one")

    closestWithAncestorChildrenTest[HTMLInputElement]("closest-sibling-trigger-element", "closest-sibling-closest-target-two")
    closestWithAncestorChildrenTest[HTMLInputElement]("closest-sibling-trigger-element", "input", "closest-sibling-closest-target-two")
    closestWithAncestorChildrenTest[HTMLInputElement]("closest-sibling-trigger-element", "input[class='target-one']", "closest-sibling-closest-target-one")

    closestWithAncestorChildrenTest[HTMLInputElement]("closest-is-child-of-ancestor-trigger-element", "closest-is-child-of-ancestor-target-two")
    closestWithAncestorChildrenTest[HTMLInputElement]("closest-is-child-of-ancestor-trigger-element", "input", "closest-is-child-of-ancestor-target-two")
    closestWithAncestorChildrenTest[HTMLInputElement]("closest-is-child-of-ancestor-trigger-element", "input[class='target-one']", "closest-is-child-of-ancestor-target-one")

    closestWithAncestorChildrenTest[HTMLInputElement]("closest-is-deep-child-of-ancestor-trigger-element", "closest-is-deep-child-of-ancestor-target-three")
    closestWithAncestorChildrenTest[HTMLInputElement]("closest-is-deep-child-of-ancestor-trigger-element", "input", "closest-is-deep-child-of-ancestor-target-three")
    closestWithAncestorChildrenTest[HTMLInputElement]("closest-is-deep-child-of-ancestor-trigger-element", "input[class='target-one']", "closest-is-deep-child-of-ancestor-target-one")
    closestWithAncestorChildrenTest[HTMLInputElement]("closest-is-deep-child-of-ancestor-trigger-element", "input[class='target-two']", "closest-is-deep-child-of-ancestor-target-two")

    closestWithAncestorChildrenTest[HTMLDivElement]("nested-div-inside-one", "div[class='nested-div']", "nested-div-outside")
    closestWithAncestorChildrenTest[HTMLDivElement]("nested-div-inside-one-child-two-grandchild-two", "div[class='nested-div']", "nested-div-inside-one-child-one-grandchild-one")
    closestWithAncestorChildrenTest[HTMLDivElement]("nested-div-inside-one-child-three", "div[class='nested-div']", "nested-div-inside-one-child-two-grandchild-two")
  }


  test("toggleClass") {
    val element: Element = document.getElementById("toggle-class-icon")
    // Starts open
    element.hasClass("glyphicon-eye-open") should equal(true)

    // Toggle it
    element.toggleClass("glyphicon-eye-open", "glyphicon-eye-close")

    // Should be closed
    element.hasClass("glyphicon-eye-open") should equal(false)
    element.hasClass("glyphicon-eye-close") should equal(true)

    // Toggle again
    element.toggleClass("glyphicon-eye-open", "glyphicon-eye-close")

    // Should now be open again
    element.hasClass("glyphicon-eye-open") should equal(true)
    element.hasClass("glyphicon-eye-close") should equal(false)
  }


  test("toggleAttributeValue") {
    val element: Element = document.getElementById("toggle-attribute-value")
    // Starts password
    element.getAttribute("type") should equal("password")

    // Toggle it
    element.toggleAttributeValue("type", "password", "text")

    // Should be text
    element.getAttribute("type") should equal("text")

    // Toggle again
    element.toggleAttributeValue("type", "password", "text")

    // Should now be password
    element.getAttribute("type") should equal("password")
  }
}
