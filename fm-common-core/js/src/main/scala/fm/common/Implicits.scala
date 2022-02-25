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

import fm.common.jquery.JQuery
import fm.common.rich._
import org.scalajs.dom._

object Implicits extends Implicits {
  // Duplicated in both the JVM and JS version of Implicits.scala
  implicit def toImmutableArrayByte[COL](col: COL)(implicit toTraversableOnce: COL => TraversableOnce[Byte]): ToImmutableArrayByte = new ToImmutableArrayByte(toTraversableOnce(col))
  implicit def toImmutableArrayShort[COL](col: COL)(implicit toTraversableOnce: COL => TraversableOnce[Short]): ToImmutableArrayShort = new ToImmutableArrayShort(toTraversableOnce(col))
  implicit def toImmutableArrayInt[COL](col: COL)(implicit toTraversableOnce: COL => TraversableOnce[Int]): ToImmutableArrayInt = new ToImmutableArrayInt(toTraversableOnce(col))
  implicit def toImmutableArrayLong[COL](col: COL)(implicit toTraversableOnce: COL => TraversableOnce[Long]): ToImmutableArrayLong = new ToImmutableArrayLong(toTraversableOnce(col))
  implicit def toImmutableArrayFloat[COL](col: COL)(implicit toTraversableOnce: COL => TraversableOnce[Float]): ToImmutableArrayFloat = new ToImmutableArrayFloat(toTraversableOnce(col))
  implicit def toImmutableArrayDouble[COL](col: COL)(implicit toTraversableOnce: COL => TraversableOnce[Double]): ToImmutableArrayDouble = new ToImmutableArrayDouble(toTraversableOnce(col))
  implicit def toImmutableArrayBoolean[COL](col: COL)(implicit toTraversableOnce: COL => TraversableOnce[Boolean]): ToImmutableArrayBoolean = new ToImmutableArrayBoolean(toTraversableOnce(col))
  implicit def toImmutableArrayChar[COL](col: COL)(implicit toTraversableOnce: COL => TraversableOnce[Char]): ToImmutableArrayChar = new ToImmutableArrayChar(toTraversableOnce(col))
  implicit def toImmutableArrayAnyRef[COL, T <: AnyRef](col: COL)(implicit toTraversableOnce: COL => TraversableOnce[T]): ToImmutableArrayAnyRef[T] = new ToImmutableArrayAnyRef(toTraversableOnce(col))

  // Duplicated in both the JVM and JS version of Implicits.scala
  final class ToImmutableArrayByte   (val col: TraversableOnce[Byte])    extends AnyVal { def toImmutableArray: ImmutableArray[Byte]    = ImmutableArray.copy(col) }
  final class ToImmutableArrayShort  (val col: TraversableOnce[Short])   extends AnyVal { def toImmutableArray: ImmutableArray[Short]   = ImmutableArray.copy(col) }
  final class ToImmutableArrayInt    (val col: TraversableOnce[Int])     extends AnyVal { def toImmutableArray: ImmutableArray[Int]     = ImmutableArray.copy(col) }
  final class ToImmutableArrayLong   (val col: TraversableOnce[Long])    extends AnyVal { def toImmutableArray: ImmutableArray[Long]    = ImmutableArray.copy(col) }
  final class ToImmutableArrayFloat  (val col: TraversableOnce[Float])   extends AnyVal { def toImmutableArray: ImmutableArray[Float]   = ImmutableArray.copy(col) }
  final class ToImmutableArrayDouble (val col: TraversableOnce[Double])  extends AnyVal { def toImmutableArray: ImmutableArray[Double]  = ImmutableArray.copy(col) }
  final class ToImmutableArrayBoolean(val col: TraversableOnce[Boolean]) extends AnyVal { def toImmutableArray: ImmutableArray[Boolean] = ImmutableArray.copy(col) }
  final class ToImmutableArrayChar   (val col: TraversableOnce[Char])    extends AnyVal { def toImmutableArray: ImmutableArray[Char]    = ImmutableArray.copy(col) }

  final class ToImmutableArrayAnyRef[T <: AnyRef](val col: TraversableOnce[T]) extends AnyVal { def toImmutableArray: ImmutableArray[T] = ImmutableArray.copy[AnyRef](col.asInstanceOf[TraversableOnce[AnyRef]]).asInstanceOf[ImmutableArray[T]] }
}

trait Implicits extends ImplicitsBase {
  implicit def toRichJQuery(jquery: JQuery): RichJQuery = new RichJQuery(jquery)

  implicit def toRichEvent(event: Event): RichEvent = new RichEvent(event)
  implicit def toRichEventTarget(target: EventTarget): RichEventTarget = new RichEventTarget(target)
  implicit def toRichEventTargetTraversable(target: Traversable[EventTarget]): RichEventTargetTraversable = new RichEventTargetTraversable(target)
  implicit def toRichDocument(doc: Document): RichDocument = new RichDocument(doc)
  implicit def toRichHTMLDocument(doc: HTMLDocument): RichHTMLDocument = new RichHTMLDocument(doc)
  implicit def toRichNode[T <: Node](node: T): RichNode[T] = new RichNode(node)
  implicit def toRichNodeTraversable(elems: Traversable[Node]): RichNodeTraversable = new RichNodeTraversable(elems)
  implicit def toRichElement(elem: Element): RichElement = new RichElement(elem)
  implicit def toRichElementTraversable(elems: Traversable[Element]): RichElementTraversable = new RichElementTraversable(elems)
  implicit def toRichHTMLElement(elem: HTMLElement): RichHTMLElement = new RichHTMLElement(elem)
  implicit def toRichHTMLElementTraversable(elems: Traversable[HTMLElement]): RichHTMLElementTraversable = new RichHTMLElementTraversable(elems)
  implicit def toRichHTMLImageElement(elem: HTMLImageElement): RichHTMLImageElement = new RichHTMLImageElement(elem)
  implicit def toRichNodeSelector(selector: NodeSelector): RichNodeSelector = new RichNodeSelector(selector)
  implicit def toRichDOMList[A](list: DOMList[A]): RichDOMList[A] = new RichDOMList(list)
  implicit def toRichNodeList[A <: Node](list: NodeList[A]): RichNodeList[A] = new RichNodeList(list)
}