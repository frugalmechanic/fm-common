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

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

trait TypeSafeEqualsOps {
  implicit def toTypeSafeEquals[L](left: L): TypeSafeEquals[L] = new TypeSafeEquals(left)
}

/**
 * Simple attempt at providing a macro based implementation of type-safe equals
 */
object TypeSafeEquals {
  def equals[L,R](left: L, right: R): Boolean = macro equalsMacro[L,R]
  def notEquals[L,R](left: L, right: R): Boolean = macro notEqualsMacro[L,R]
  
  def equalsMacro[L: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(left: c.Expr[L], right: c.Expr[R]): c.Tree = {
    equalsMacroImpl(c)(left, right, true)
  }

  def notEqualsMacro[L: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(left: c.Expr[L], right: c.Expr[R]): c.Tree = {
    equalsMacroImpl(c)(left, right, false)
  }
  
  private def equalsMacroImpl[L, R](c: Context)(left: c.Expr[L], right: c.Expr[R], isEquals: Boolean)(implicit L: c.WeakTypeTag[L], R: c.WeakTypeTag[R]): c.Tree = {
    import c.universe._
    requireWeakTypeTagSubRelationship[L,R](c)
    if (isEquals) q"$left == $right" else q"$left != $right"
  }
  
  def tripleEqualsMacro[R: c.WeakTypeTag](c: Context)(right: c.Expr[R]): c.Tree = {
    tripleEqualsMacroImpl(c)(right, true)
  }

  def notTripleEqualsMacro[R: c.WeakTypeTag](c: Context)(right: c.Expr[R]): c.Tree = {
    tripleEqualsMacroImpl(c)(right, false)
  }
  
  private def tripleEqualsMacroImpl[R](c: Context)(right: c.Expr[R], isEquals: Boolean)(implicit R: c.WeakTypeTag[R]): c.Tree = {
    import c.universe._
    
    val (leftTpe: c.Type, leftTree: c.Tree) = extractLeftHandSide(c)
    
    requireSubTypeRelationship(c)(leftTpe, R.tpe)
    if (isEquals) q"$leftTree == $right" else q"$leftTree != $right"
  }
  
  // The left hand side isn't passed into the macro when using === so we have to extract it
  private def extractLeftHandSide(c: Context): (c.Type, c.Tree) = {
    import c.universe._
    
    val (tpe: Option[c.Tree], arg: c.Tree) = c.prefix.tree match {
      case q"""$method[..$types](..$args)""" => (types.headOption, args.head)
      case q"""new $clazz[..$types](..$args)""" => (types.headOption, args.head)
      case t => c.abort(c.enclosingPosition,"Cannot extract subject of operator (tree = %s)" format t)
    }
    
    (tpe.getOrElse(arg).tpe, arg)
  }
  
  private def requireWeakTypeTagSubRelationship[L, R](c: Context)(implicit L: c.WeakTypeTag[L], R: c.WeakTypeTag[R]): Unit = {
    requireSubTypeRelationship(c)(L.tpe, R.tpe)
  }
  
  private def requireSubTypeRelationship(c: Context)(l: c.Type, r: c.Type): Unit = {
    val isSubType: Boolean = l =:= r || l <:< r || r <:< l
    if (!isSubType) c.abort(c.enclosingPosition, s"TypeSafeEquals requires ${l} and ${r} to be in a subtype relationship!")
  }
}

final class TypeSafeEquals[L](val left: L) extends AnyVal {
  def ===[R](right: R): Boolean = macro TypeSafeEquals.tripleEqualsMacro[R]
  def =!=[R](right: R): Boolean = macro TypeSafeEquals.notTripleEqualsMacro[R]

  @deprecated("Use =!= instead (which avoids any operator precedence issues)","")
  def !==[R](right: R): Boolean = macro TypeSafeEquals.notTripleEqualsMacro[R]

  // Using these for testing since ScalaTest defines it's own === method
  def ≡[R](right: R): Boolean = macro TypeSafeEquals.tripleEqualsMacro[R]
  def ≠[R](right: R): Boolean = macro TypeSafeEquals.notTripleEqualsMacro[R]
}