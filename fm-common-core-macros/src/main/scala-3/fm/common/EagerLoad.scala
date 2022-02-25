/*
 * Copyright 2021 Tim Underwood (https://github.com/tpunder)
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

import scala.quoted.{Expr, quotes, Quotes, Type}

object EagerLoad {
  inline def eagerLoadNestedObjects[T](obj: T): Unit = ${ eagerLoadNestedObjectsMacro('obj) }

  def eagerLoadNestedObjectsMacro[T: Type](obj: Expr[T])(using Quotes): Expr[Unit] = {
    import quotes.reflect.*

    val tpe: TypeRepr = TypeRepr.of[T]
    val symbol: Symbol = tpe.typeSymbol

    val statements: List[Term] = getNestedModuleExprs(tpe, Nil)
    Expr.block(statements.map{ _.asExprOf[Any] }, '{ () })
  }

  private def getNestedModuleExprs(using q: Quotes)(tpe: q.reflect.TypeRepr, path: List[q.reflect.Symbol]): List[q.reflect.Term] = {
    import quotes.reflect.*

    def makeTree(path: List[Symbol]): Term = {
      path match {
        case Nil => ???
        case head :: Nil => Ref(head)
        case head :: tail => makeTree(tail).select(head)
      }
    }

    def isObject(sym: Symbol): Boolean = sym.isValDef && !sym.companionModule.isNoSymbol

    tpe.typeSymbol.declaredFields.filter { isObject }.flatMap { (sym: Symbol) =>
      val nestedTpe: TypeRepr = tpe.memberType(sym)
      val newPath: List[Symbol] = sym :: path
      makeTree(newPath) +: getNestedModuleExprs(nestedTpe, newPath)
    }
  }
}
