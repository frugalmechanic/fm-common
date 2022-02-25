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

import org.scalajs.dom.raw.Node

sealed trait NodeType[T]

/**
 * Some type class hackery to make our $ and $$ methods NOT default to Nothing
 * when trying to do something like $("#some_id").parentNode.  You'd think this
 * would work:
 * 
 * def $[T <: Node](selector: String): T = ???
 * $("#some_id").parentNode
 * 
 * But it doesn't:
 * 
 * Error: "value parentNode is not a member of Nothing"
 */
object NodeType {
  implicit object DefaultNodeType extends NodeType[Node]
  implicit def anyNodeType[T <: Node]: NodeType[T] = nodeType.asInstanceOf[NodeType[T]]
  
  private object nodeType extends NodeType[AnyRef]
}