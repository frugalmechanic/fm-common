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
import org.scalajs.dom.raw.HTMLElement

final class RichHTMLElementIterable(val elems: Iterable[HTMLElement]) extends AnyVal {
  
  /** Similar to jQuery.css */
  def css(ruleName: String, value: String): Unit = elems.foreach{ _.css(ruleName, value) }
  
  def hide(): Unit = elems.foreach{ _.hide() }
  
  def show(): Unit = elems.foreach{ _.show() }
  
  /** If the element is hidden then show it.  If the element is shown then hide it */
  def toggle(): Unit = elems.foreach{ _.toggle() }
  
  /** Toggle visibility of the element based on the passed in boolean */
  def toggle(showElem: Boolean): Unit = elems.foreach{ _.toggle(showElem) }
}
