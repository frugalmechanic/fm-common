/*
 * Copyright 2018 Frugal Mechanic (http://frugalmechanic.com)
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

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

trait AnyRefNullCheckOps {
  implicit def toAnyRefNullChecks[A <: AnyRef](a: A): AnyRefNullChecks[A] = new AnyRefNullChecks(a)
}

/**
 * Macro based null check helpers so you can do something like mightBeNull.isNull
 */
object AnyRefNullChecks {
  def isNullMacro(c: Context): c.Tree = {
    isNullMacroImpl(c)(true)
  }

  def isNotNullMacro(c: Context): c.Tree = {
    isNullMacroImpl(c)(false)
  }

  private def isNullMacroImpl[A](c: Context)(isNull: Boolean)(implicit A: c.WeakTypeTag[A]): c.Tree = {
    import c.universe._
    val expr: c.Tree = extractLeftHandSide(c)
    if (isNull) q"null == $expr" else q"null != $expr"
  }

  // The left hand side isn't passed into the macro when using so we have to extract it
  private def extractLeftHandSide(c: Context): c.Tree = {
    import c.universe._

    // Looking for something like "toAnyRefNullChecks[type](arg)" or "new AnyRefNullChecks[type](arg)"
    val arg: c.Tree = c.prefix.tree match {
      case q"""$method[..$types](..$args)""" => args.head
      case q"""new $clazz[..$types](..$args)""" => args.head
      case t => c.abort(c.enclosingPosition,"Cannot extract subject of operator (tree = %s)" format t)
    }

    arg
  }
}

final class AnyRefNullChecks[A <: AnyRef](val a: A) extends AnyVal {
  def isNull: Boolean = macro AnyRefNullChecks.isNullMacro
  def nonNull: Boolean = macro AnyRefNullChecks.isNotNullMacro
  def isNotNull: Boolean = macro AnyRefNullChecks.isNotNullMacro
}