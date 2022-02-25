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
import fm.common.NodeType
import org.scalajs.dom.raw.{Element, NodeList, NodeSelector}
import scala.reflect.{classTag, ClassTag}

final class RichNodeSelector(val self: NodeSelector) extends AnyVal {
  /** Typesafe helper on top of querySelector */
  def selectFirst[T : NodeType : ClassTag](selector: String): T = selectFirstOption[T](selector).getOrElse{ throw new NoSuchElementException(s"No Such Element: $selector") }
  
  /** Alias for selectFirst[T]("*") */
  def selectFirst[T : NodeType : ClassTag]: T = selectFirst[T]("*")
  
  /** Typesafe helper on top of querySelector */
  def selectFirstOption[T : NodeType : ClassTag](selector: String): Option[T] = {
    val targetClass: Class[_] = classTag[T].runtimeClass
    val elem: Element = try{ self.querySelector(selector) } catch { case ex: Exception => throw new IllegalArgumentException("Invalid Selector for querySelector: "+selector) }
    Option(elem).filter{ targetClass.isInstance }.map{ _.asInstanceOf[T] }
  }
  
  /** Alias for selectFirstOption[T]("*") */
  def selectFirstOption[T : NodeType : ClassTag]: Option[T] = selectFirstOption[T]("*")
  
  /** Typesafe helper on top of querySelectorAll */
  def selectAll[T : NodeType : ClassTag](selector: String): IndexedSeq[T] = {
    val targetClass: Class[_] = classTag[T].runtimeClass
    val results: NodeList[Element] = try{ self.querySelectorAll(selector) } catch { case ex: Exception => throw new IllegalArgumentException("Invalid Selector for querySelectorAll: "+selector) }
    results.filter{ targetClass.isInstance }.map{ _.asInstanceOf[T] }
  }
  
  /** Alias for selectAll[T]("*") */
  def selectAll[T : NodeType : ClassTag]: IndexedSeq[T] = selectAll[T]("*")
}