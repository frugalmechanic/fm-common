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

sealed abstract class EventType[+T <: Event](val name: String)

object EventType {
  //case object Abort extends EventType[UIEvent]("abort") // Has multiple Event Types depending on the usage
  case object Blur extends EventType[FocusEvent]("blur")
  case object Click extends EventType[MouseEvent]("click")
  case object Change extends EventType[Event]("change")
  case object CompositionEnd extends EventType[CompositionEvent]("compositionend")
  case object CompositionStart extends EventType[CompositionEvent]("compositionstart")
  case object CompositionUpdate extends EventType[CompositionEvent]("compositionupdate")
  case object ContextMenu extends EventType[MouseEvent]("contextmenu")
  case object DblClick extends EventType[MouseEvent]("dblclick")
  case object DOMContentLoaded extends EventType[Event]("DOMContentLoaded")
  case object Drag extends EventType[Event]("drag")
  case object DragEnd extends EventType[Event]("dragend")
  case object DragEnter extends EventType[Event]("dragenter")
  case object DragLeave extends EventType[Event]("dragleave")
  case object DragOver extends EventType[Event]("dragover")
  case object DragStart extends EventType[Event]("dragstart")
  case object Drop extends EventType[Event]("drop")
  //case object Error extends EventType[UIEvent]("error") // Has multiple Event Types depending on the usage
  case object Focus extends EventType[FocusEvent]("focus")
  case object HashChange extends EventType[HashChangeEvent]("hashchange")
  case object Input extends EventType[Event]("input")
  case object Invalid extends EventType[Event]("invalid")
  case object KeyDown extends EventType[KeyboardEvent]("keydown")
  case object KeyPress extends EventType[KeyboardEvent]("keypress")
  case object KeyUp extends EventType[KeyboardEvent]("keyup")
//  case object Load extends EventType[UIEvent]("load")
//  case object LoadProgress extends EventType[ProgressEvent]("load")
  case object LoadEnd extends EventType[ProgressEvent]("loadend")
  case object LoadStart extends EventType[ProgressEvent]("loadstart")
  case object MouseDown extends EventType[MouseEvent]("mousedown")
  case object MouseEnter extends EventType[MouseEvent]("mouseenter")
  case object MouseLeave extends EventType[MouseEvent]("mouseleave")
  case object MouseMove extends EventType[MouseEvent]("mousemove")
  case object MouseOut extends EventType[MouseEvent]("mouseout")
  case object MouseOver extends EventType[MouseEvent]("mouseover")
  case object MouseUp extends EventType[MouseEvent]("mouseup")
//  case object PageHide extends EventType[PageTransitionEvent]("pagehide")
//  case object PageShow extends EventType[PageTransitionEvent]("pageshow")
  case object PopState extends EventType[PopStateEvent]("popstate")
  case object Progress extends EventType[ProgressEvent]("progress")
  case object Reset extends EventType[Event]("reset")
  case object Resize extends EventType[UIEvent]("resize")
  case object Scroll extends EventType[UIEvent]("scroll")
  case object Show extends EventType[MouseEvent]("show")
  case object Submit extends EventType[Event]("submit")
  case object Timeout extends EventType[ProgressEvent]("timeout")
  case object TouchCancel extends EventType[TouchEvent]("touchcancel")
  case object TouchEnd extends EventType[TouchEvent]("touchend")
  case object TouchMove extends EventType[TouchEvent]("touchmove")
  case object TouchStart extends EventType[TouchEvent]("touchstart")
  case object TransitionEnd extends EventType[TransitionEvent]("transitionend")
  case object Unload extends EventType[UIEvent]("unload")
  case object Wheel extends EventType[WheelEvent]("wheel")
}