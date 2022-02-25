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

import org.scalajs.dom.raw.Element

/**
 * Helpers for accessing the "user-" attributes
 * 
 * https://developer.mozilla.org/en-US/docs/Web/Guide/HTML/Using_data_attributes
 */
final class UserDataAttributes(val elem: Element) extends AnyVal {
  def apply(key: String): String = get(key).getOrElse{ throw new NoSuchElementException(key) }
  def get(key: String): Option[String] = elem.getAttribute(makeKey(key)).toBlankOption
  def getOrElse(key: String, orElse: => String): String = get(key).getOrElse(orElse)
  
  def update(key: String, value: String): Unit = elem.setAttribute(makeKey(key), value)
  def update(key: String, value: Option[String]): Unit = elem.setAttribute(makeKey(key), value.getOrElse(""))

  def remove(key: String): Unit = elem.removeAttribute(makeKey(key))
  
  private def makeKey(key: String): String = "data-"+key
}