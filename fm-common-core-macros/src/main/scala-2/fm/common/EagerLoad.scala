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

object EagerLoad {
  def eagerLoadNestedObjects[T](obj: T): Unit = macro eagerLoadNestedObjectsMacro[T]

  def eagerLoadNestedObjectsMacro[T: c.WeakTypeTag](c: Context)(obj: c.Expr[T]): c.Tree = {
    import c.universe._

    val tpe: c.Type = c.weakTypeOf[T]

    val enclosingModule: ModuleDef = c.enclosingClass match {
      case mod: ModuleDef => mod
      case _ => c.abort(c.enclosingPosition, "Eager Loading of nested objects only works inside of an object")
    }

//    println("enclosingClassOrModule: "+enclosingModule)

    if (tpe.termSymbol != enclosingModule.symbol) c.abort(c.enclosingPosition, "For Scala 2/3 compatibility this macro must be called within the object and the object must also be passed in (e.g.: 'EagerLoad.eagerLoadNestedObjects(this)')")

    val nestedModuleExprs: Seq[c.Tree] = getNestedModuleExprs(c)(enclosingModule, Nil)

//    println("Idents: "+nestedModuleExprs)

    q"..$nestedModuleExprs"
  }

  private def getNestedModuleExprs(c: Context)(module: c.universe.ModuleDef, path: List[c.universe.Symbol]): Seq[c.Tree] = {
    import c.universe._

    def makeTree(path: List[Symbol]): c.Tree = {
      path match {
        case Nil => ???
        case head :: Nil => Ident(head)
        case head :: tail => Select(makeTree(tail), head)
      }
    }

    module.impl.body.flatMap{
      case mod: ModuleDef =>
        val newPath: List[Symbol] = mod.symbol :: path
        makeTree(newPath) +: getNestedModuleExprs(c)(mod, newPath)

      case _ => Nil
    }
  }
}
