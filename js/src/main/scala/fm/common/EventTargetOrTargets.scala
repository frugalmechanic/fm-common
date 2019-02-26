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

import org.scalajs.dom.raw.Event
import org.scalajs.jquery.{JQuery, JQueryEventObject}
import scala.scalajs.js

trait EventAttachments[T] {
  def apply[E](f: T => E): Unit
  def apply(f: js.Function1[T, _]): Unit
}

trait JQueryEventAttachments {
  def apply[E](f: JQueryEventObject => E): Unit
  def apply[E](f: (JQueryEventObject, js.Any) => E): Unit

  def apply(f: js.Function1[JQueryEventObject, _]): Unit
  def apply(f: js.Function2[JQueryEventObject, js.Any, _]): Unit
}

trait EventTargetOrTargets extends Any {
  protected def jQueryElements: JQuery
  
  final def onJQueryEvent(tpe: String): JQueryEventAttachments = new JQueryEventAttachments {
    def apply[E](f: JQueryEventObject => E): Unit = jQueryElements.on(tpe, f)
    def apply[E](f: (JQueryEventObject, js.Any) => E): Unit = jQueryElements.on(tpe, f)

    def apply(f: js.Function1[JQueryEventObject, _]): Unit = jQueryElements.on(tpe, f)
    def apply(f: js.Function2[JQueryEventObject, js.Any, _]): Unit = jQueryElements.on(tpe, f)
  }

  final def offJQueryEvent(tpe: String): Unit = jQueryElements.off(tpe)
  final def offJQueryEvent(tpe: String, f: js.Function1[JQueryEventObject, js.Any]): Unit = jQueryElements.off(tpe, null, f)

  final def oneJQueryEvent(tpe: String): JQueryEventAttachments = new JQueryEventAttachments {
    def apply[E](f: JQueryEventObject => E): Unit = jQueryElements.one(tpe, f)
    def apply[E](f: (JQueryEventObject, js.Any) => E): Unit = jQueryElements.one(tpe, f)

    def apply(f: js.Function1[JQueryEventObject, _]): Unit = jQueryElements.one(tpe, f)
    def apply(f: js.Function2[JQueryEventObject, js.Any, _]): Unit = jQueryElements.one(tpe, f)
  }

  def addEventListener[T <: Event](tpe: String)(f: js.Function1[T,_]): Unit
  def removeEventListener[T <: Event](tpe: String)(f: js.Function1[T,_]): Unit
  
  final def addEventListener[T <: Event](tpe: EventType[T])(f: js.Function1[T,_]): Unit = addEventListener(tpe.name)(f)
  final def removeEventListener[T <: Event](tpe: EventType[T])(f: js.Function1[T,_]): Unit = removeEventListener(tpe.name)(f)

  final def on[T <: Event](tpe: String): EventAttachments[T] = new EventAttachments[T] {
    def apply[E](f: T => E): Unit = addEventListener(tpe)(f)
    def apply(f: js.Function1[T, _]): Unit = addEventListener(tpe)(f)
  }

  final def on[T <: Event](tpe: EventType[T]): EventAttachments[T] = new EventAttachments[T] {
    def apply[E](f: T => E): Unit = addEventListener(tpe)(f)
    def apply(f: js.Function1[T, _]): Unit = addEventListener(tpe)(f)
  }

  /**
   * Only execute the event once
   *
   * Note: This is NOT compatible with off()
   */
  final def one[T <: Event, E](tpe: String)(f: T => E): Unit = {

    // Our function definition needs to be able to reference
    // this in order to call removeEventListener.
    var handler: js.Function1[T, _] = null

    // Note: this gets implicitly converted to a js.Function1[T, Unit]
    //       which is the only way the removeEventListener call can work.
    handler = { event: T =>
      removeEventListener(tpe)(handler)
      f(event)
    }

    addEventListener(tpe)(handler)
  }
  
  /** Only execute the event once */
  final def one[T <: Event, E](tpe: EventType[T])(f: T => E): Unit = one(tpe.name)(f)

  final def off[T <: Event](tpe: String)(f: js.Function1[T, _]): Unit = removeEventListener(tpe)(f)
  final def off[T <: Event](tpe: EventType[T])(f: js.Function1[T, _]): Unit = removeEventListener(tpe)(f)
}