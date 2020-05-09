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
package fm.common.rich

import fm.common.Implicits._
import fm.common.{ElementType, UserDataAttributes}
import org.scalajs.dom.window
import org.scalajs.dom.raw.{CSSStyleDeclaration, Element, Node}
import scala.annotation.tailrec
import scala.reflect.{ClassTag, classTag}
import scala.scalajs.js


/**
 * Facade for additional native Element dom methods not currently in scala.js
 *
 * https://developer.mozilla.org/en/docs/Web/API/Element
 */
@js.native
trait RichJSElement extends js.Object {
  /**
   * Supported by FF&gt;34, Opera&gt;21, Chrome&gt;34, IE&gt;9, Safari&gt;4
   *
   * @see https://developer.mozilla.org/en-US/docs/Web/API/Element/matches
   * @see https://github.com/scala-js/scala-js-dom/pull/345 (This is now merged, but not published)
   */
  def matches(selector: String): Boolean = js.native
}

object RichJSElement {
  implicit def toRichJSElement(element: Element): RichJSElement = element.asInstanceOf[RichJSElement]
}

final class RichElement(val elem: Element) extends AnyVal {
  /**
   * Helpers for accessing the "user-" attributes
   * 
   * https://developer.mozilla.org/en-US/docs/Web/Guide/HTML/Using_data_attributes
   */
  def data: UserDataAttributes = new UserDataAttributes(elem)

  /** Shortcut for window.getComputedStyle(elem) */
  def computedStyle: CSSStyleDeclaration = window.getComputedStyle(elem)

  def hasParentNode: Boolean = elem.parentNode.isNotNull

  def hasChildNodes: Boolean = elem.childNodes.isNotNull && elem.childNodes.nonEmpty

  /** Shortcut for window.getComputedStyle(elem, pseudoElem) */
  def computedStyle(pseudoElem: String): CSSStyleDeclaration = window.getComputedStyle(elem, pseudoElem)
  
  /** Shortcut for window.getComputedStyle(elem, pseudoElem) */
  def computedStyle(pseudoElem: Option[String]): CSSStyleDeclaration = pseudoElem match {
    case Some(pseudo) => computedStyle(pseudo)
    case None => computedStyle
  }
  
  def hasClass(className: String): Boolean = elem.classList.contains(className)
  
  def addClass(className: String): Unit = if (!elem.classList.contains(className)) elem.classList.add(className)
  
  def removeClass(className: String): Unit = elem.classList.remove(className)

  /** Toggle between two classes, e.g. ({{{icon.toggleClass("glyphicon-eye-close", "glyphicon-eye-open")}}})*/
  def toggleClass(valueA: String, valueB: String): Unit = {
    if (elem.hasClass(valueA)) {
      elem.removeClass(valueA)
      elem.addClass(valueB)
    } else {
      elem.removeClass(valueB)
      elem.addClass(valueA)
    }
  }

  def getParentElement: Option[Element] = {
    if (elem.parentNode.isNull) return None

    elem.parentNode match {
      case e: Element => Some(e)
      case _ => None
    }
  }

  def hasParentElement: Boolean = getParentElement.isDefined

  def hasChildElements: Boolean = {
    if (!hasChildNodes) return false

    elem.childNodes.exists{ _.isInstanceOf[Element] }
  }

  def childElements: Seq[Element] = {
    if (!hasChildNodes) return Nil

    elem.childNodes.collect {
      case e: Element => e
    }
  }

  /** Gets the current index of the element in relationship to it's parent's children */
  private def parentChildIndex: Option[Int] = getParentElement.flatMap{ parent: Element =>
    parent.childElements.zipWithIndex.collectFirst{ case (childElement: Element, idx: Int) if childElement === elem => idx }
  }

  private def elementMatchesTypeAndSelector[A <: Element : ClassTag](e: Element, selector: Option[String]): Boolean = {
    classTag[A].runtimeClass.isInstance(e) && (selector.isEmpty || selector.exists{ e.matches })
  }

  /** 
   * Find the closest (this element, ancestor, or a child of an ancestor) that matches a class.
   * (e.g. closestWithAncestorChildren[Input] will find the closest input field that was defined before the current element)
   */
  def closestWithAncestorChildren[A <: Element : ClassTag](implicit elementType: ElementType[A]): A = {
    if (elementMatchesTypeAndSelector(elem, None)) return elem.asInstanceOf[A]

    closestWithAncestorChildrenImpl[A](parentChildIndex, elem.getParentElement.get, None)
  }

  /** 
   * Check the element and ancestors' children to find the closest (previous) element that matches a class AND selector.
   * (e.g. closestWithAncestorChildren("input[type='password']") will find the closest password input field that was defined before the current element)
   */
  def closestWithAncestorChildren[A <: Element : ClassTag](selector: String)(implicit elementType: ElementType[A]): A = {
    if (elementMatchesTypeAndSelector(elem, selector.toBlankOption)) return elem.asInstanceOf[A]

    closestWithAncestorChildrenImpl(parentChildIndex, elem.getParentElement.get, selector.toBlankOption)
  }

  @tailrec
  private def closestWithAncestorChildrenImpl[A <: Element : ClassTag](childLimit: Option[Int], element: Element, selector: Option[String])(implicit elementType: ElementType[A]): A = {

    // Look for the deepest child element first before looking at element.
    if (element.hasChildElements) {
      val children: Seq[Element] = element.childElements
      // When we scan children, we don't want to scan siblings of the original calling element defined after the element
      var i: Int = childLimit.getOrElse(children.size) - 1

      while (i >= 0) {
        val child: Element = children(i)

        // Look for the deepest match first
        val reverseMatches: Iterator[Node] = child.querySelectorAll(selector.getOrElse(elementType.name)).reverseIterator
        while (reverseMatches.hasNext) {
          val next: Node = reverseMatches.next()

          next match {
            case v: A if elementMatchesTypeAndSelector(v, selector) => return v
            case _ =>
          }
        }

        // Else this sibling is the closest if it matches
        if (elementMatchesTypeAndSelector(child, selector)) return child.asInstanceOf[A]

        i -= 1
      }
    }

    // If no matching children-matches, look at the element
    if (elementMatchesTypeAndSelector(element, selector)) return element.asInstanceOf[A]

    // If no match look at the parent
    if (element.hasParentElement) {
      val parentElement: Element = element.getParentElement.get

      closestWithAncestorChildrenImpl[A](new RichElement(element).parentChildIndex, parentElement, selector)
    } else {
      throw new NoSuchElementException(s"No Matching Parent for element: ${element}")
    }
  }

  /** Toggle between two attribute values, e.g. ({{{passwordField.toggleAttributeValue("type", "text", "password")}}})*/
  def toggleAttributeValue(attrName: String, valueA: String, valueB: String): Unit = {
    val newValue: String = if (elem.getAttribute(attrName) === valueA) valueB else valueA
    elem.setAttribute(attrName, newValue)
  }
}
