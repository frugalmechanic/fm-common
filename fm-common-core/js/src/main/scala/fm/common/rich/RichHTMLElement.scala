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
import org.scalajs.dom.raw.{HTMLDocument, HTMLElement}
import scala.scalajs.js

object RichHTMLElement {
  /** A cache of html element names and their default css display values */
  private val defaultDisplayValueCache: js.Dictionary[String] = (new js.Object).asInstanceOf[js.Dictionary[String]]
  
  private val SavedDisplayKey: String = "fm-common-saved-display-value"
}

final class RichHTMLElement(val elem: HTMLElement) extends AnyVal {

  /** Similar to jQuery.css */
  def css(ruleName: String): Option[String] = elem.computedStyle.getPropertyValue(ruleName).toBlankOption
  
  /** Similar to jQuery.css */
  def css(ruleName: String, value: String): Unit = elem.style.setProperty(ruleName, value)
  
  /** The default CSS "display" value for this element */
  private def defaultDisplay: String = {
    val nodeName: String = elem.nodeName
    
    var display: String = RichHTMLElement.defaultDisplayValueCache.get(nodeName).orNull
    if (null != display) return display
    
    val doc: HTMLDocument = elem.ownerDocument
    val tmp: HTMLElement = doc.body.appendChild(doc.createElement(nodeName)).asInstanceOf[HTMLElement]
    
    display = tmp.computedStyle.display
    
    tmp.parentNode.removeChild(tmp)
    
    if (display === "none") display = "block"
    
    RichHTMLElement.defaultDisplayValueCache(nodeName) = display
    
    display
  }
  
  /** Hide the Element */
  def hide(): Unit = {
    val display: String = elem.style.display
    
    if (display =!= "none") {
      elem.style.display = "none"
      elem.data(RichHTMLElement.SavedDisplayKey) = display
    }
  }
  
  /** Show the Element */
  def show(): Unit = {
    if (elem.style.display === "none") {
      // Restore a saved value or default to ""
      val display: String = elem.data.get(RichHTMLElement.SavedDisplayKey) match {
        case None => ""
        case Some(saved) => 
          elem.data.remove(RichHTMLElement.SavedDisplayKey)
          saved
      }
      
      elem.style.display = display
    }
    
    // If the element is still hidden then set display to it's default value for this element
    if (elem.style.display === "" && elem.computedStyle.display === "none") elem.style.display = defaultDisplay
  }
  
  /** If the element is hidden then show it.  If the element is shown then hide it */
  def toggle(): Unit = if (elem.computedStyle.display === "none") show() else hide()
  
  /** Toggle visibility of the element based on the passed in boolean */
  def toggle(showElem: Boolean): Unit = if (showElem) show() else hide()
  
}