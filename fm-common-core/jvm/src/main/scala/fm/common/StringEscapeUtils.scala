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

import java.net.{URLDecoder,URLEncoder}
import org.apache.commons.text.{StringEscapeUtils => Apache}

object StringEscapeUtils extends StringEscapeUtilsBase {
  // Only requires JDK
  override def encodeURIComponent(s: String): String = URLEncoder.encode(s, "UTF-8")
  override def decodeURIComponent(s: String): String = URLDecoder.decode(s, "UTF-8")

  // Requires Apache's common-text
  override def escapeHTML(s: String): String = Apache.escapeHtml4(s)
  override def escapeXML(s: String): String = Apache.escapeXml11(s)
  override def escapeECMAScript(s: String): String = Apache.escapeEcmaScript(s)
  def escapeJSON(s: String): String = Apache.escapeJson(s)
  def escapeJava(s: String): String = Apache.escapeJava(s)
}