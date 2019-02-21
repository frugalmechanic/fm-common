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
import org.scalajs.dom.raw.Node
import scala.annotation.tailrec
import scala.reflect.{classTag, ClassTag}

final class RichNode[T <: Node](val self: T) extends AnyVal {
  def hasParent: Boolean = null != self.parentNode
  
  /** Remove this node from the DOM */
  def remove(): Unit = if (hasParent) self.parentNode.removeChild(self)
  
  /** Detatch this node from the DOM */
  def detatch(): T = if (hasParent) self.parentNode.removeChild(self).asInstanceOf[T] else self
  
  /** Find the closest ancestor of the current element (or the current element itself) that matches a class. */
  def closest[A <: Node : ClassTag]: A = closestImpl(self)

  def hasNextSibling: Boolean = self.nextSibling.isNotNull

  @tailrec
  private def closestImpl[A <: Node : ClassTag](node: Node): A = {
    if (classTag[A].runtimeClass.isInstance(node)) node.asInstanceOf[A]
    else if (node.hasParent) closestImpl[A](node.parentNode)
    else throw new NoSuchElementException(s"No Matching Parent for ${classTag[A]}")
  }
  
  /** Like appendChild but add to the beginning */
  def prependChild(node: Node): Node = self.insertBefore(node, self.firstChild)
  
  /** Like insertBefore but insert after the passed in node */
  def insertAfter(node: Node, refChild: Node): Node = {
    if (refChild.hasNextSibling) self.insertBefore(node, refChild.nextSibling)
    else self.appendChild(node)
  }
  
  /** Insert the passed in node before the current node */
  def insertBefore(node: Node): Node = self.parentNode.insertBefore(node, self)
  
  /** Insert the passed in node after the current node */
  def insertAfter(node: Node): Node = {
    if (self.hasNextSibling) self.parentNode.insertBefore(node, self.nextSibling)
    else self.parentNode.appendChild(node)
  }
}
