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

import org.scalajs.dom.raw._

sealed abstract class ElementType[+T <: Element](val name: String)

object ElementType {
  implicit case object Anchor extends ElementType[HTMLAnchorElement]("a")
  implicit case object Div extends ElementType[HTMLDivElement]("div")
  implicit case object IFrame extends ElementType[HTMLIFrameElement]("iframe")
  implicit case object Input extends ElementType[HTMLInputElement]("input")
  implicit case object Option extends ElementType[HTMLOptionElement]("option")
  implicit case object Span extends ElementType[HTMLSpanElement]("span")
//  implicit case object TH extends ElementType[HTMLTableHeaderCellElement]("th")
  implicit case object TD extends ElementType[HTMLTableCellElement]("td")
  implicit case object TR extends ElementType[HTMLTableRowElement]("tr")
  
}