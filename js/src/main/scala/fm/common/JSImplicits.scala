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

import fm.common.rich._
import org.scalajs.dom.raw._
import org.scalajs.jquery.JQuery

trait JSImplicitsBase extends ImplicitsBase {
  implicit def toRichJQuery(jquery: JQuery): RichJQuery = new RichJQuery(jquery)
  
  implicit def toRichEvent(event: Event): RichEvent = new RichEvent(event)
  implicit def toRichEventTarget(target: EventTarget): RichEventTarget = new RichEventTarget(target)
  implicit def toRichDocument(doc: Document): RichDocument = new RichDocument(doc)
  implicit def toRichHTMLDocument(doc: HTMLDocument): RichHTMLDocument = new RichHTMLDocument(doc)
  implicit def toRichNode[T <: Node](node: T): RichNode[T] = new RichNode(node)
  implicit def toRichElement(elem: Element): RichElement = new RichElement(elem)
  implicit def toRichHTMLElement(elem: HTMLElement): RichHTMLElement = new RichHTMLElement(elem)
  implicit def toRichHTMLImageElement(elem: HTMLImageElement): RichHTMLImageElement = new RichHTMLImageElement(elem)
  implicit def toRichNodeSelector(selector: NodeSelector): RichNodeSelector = new RichNodeSelector(selector)
  implicit def toRichDOMList[T](list: DOMList[T]): RichDOMList[T] = new RichDOMList(list)
  implicit def toRichNodeList(list: NodeList): RichNodeList = new RichNodeList(list)
}