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

import scala.scalajs.js.URIUtils

object StringEscapeUtils extends StringEscapeUtilsBase {

  // JavaScript's encodeURIComponent doesn't convert the same chars as the JVMs URLEncoder so
  // we convert those ourselves.  See notes on https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent
  override def encodeURIComponent(s: String): String = URIUtils.encodeURIComponent(s).replace("%20","+").flatMap{
    case ch @ ('!' | '\'' | '(' | ')' | '*') => "%"+ch.toInt.toHexString
    case ch => ch.toString
  }

  // Convert + back into %20
  override def decodeURIComponent(s: String): String = URIUtils.decodeURIComponent(s.replace("+","%20"))

  override def escapeHTML(s: String): String = {
    // Using advice from http://benv.ca/2012/10/02/you-are-probably-misusing-DOM-text-methods/

    // Note this might not be the most efficient since Scala.js compiles this to a bunch of .split and .join calls
    s.replace("&", "&amp;")
     .replace("<", "&lt;")
     .replace(">", "&gt;")
     .replace("\"", "&quot;")
     .replace("'", "&#039;")
     .replace("/", "&#x2F;")

    // Was trying to use this but document isn't always available and it's wrong according to the above website
//    val div: Div = document.createElement(ElementType.Div)
//    div.appendChild(document.createTextNode(s))
//    div.innerHTML
  }

  // XML escaping is close to HTML escaping so lets just use HTML escaping for now
  override def escapeXML(s: String): String = escapeHTML(s)

  override def escapeECMAScript(s: String): String = {
    // Simple and very naive implementation based on
    // https://github.com/linkedin/dustjs/blob/3fc12efd153433a21fd79ac81e8c5f5d6f273a1c/dist/dust-core.js#L1099

    // Note this might not be the most efficient since Scala.js compiles this to a bunch of .split and .join calls
    s.replace("\\", "\\\\")
     .replace("/", "\\/")
     .replace("'", "\\'")
     .replace("\"", "\\\"")
     .replace("\n", "\\n")
     .replace("\r", "\\r")
     .replace("\t", "\\t")
     .replace("\b", "\\b")
     .replace("\f", "\\f")
     .replace("\u2028", "\\u2028")
     .replace("\u2029", "\\u2029")
  }
}