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

import org.scalajs.dom.document
import org.scalajs.dom.raw.NodeSelector
import scala.reflect.ClassTag

object Global extends Global

trait Global {
  /**
   * Like jQuery's $("selector...") method but only returns the first match.  Scoped to the document.
   */
  def $[T : NodeType : ClassTag](selector: String): T = $(selector, document)
  
  /**
   * Like jQuery's $("selector...") method but only returns the first match.  Scoped to the passed in node.
   */
  def $[T : NodeType : ClassTag](selector: String, node: NodeSelector): T = node.selectFirst(selector)
  
  /**
   * Like jQuery's $("selector...") returning all matches.  Scoped to the document
   */
  def $$[T : NodeType : ClassTag](selector: String): IndexedSeq[T] = $$(selector, document)
  
  /**
   * Like jQuery's $("selector...") returning all matches.  Scoped to the passed in node.
   */
  def $$[T : NodeType : ClassTag](selector: String, node: NodeSelector): IndexedSeq[T] = node.selectAll(selector)
}
