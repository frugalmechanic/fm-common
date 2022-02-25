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

trait TypeSafeEqualsOps {
  extension[L] (left: L) {
    inline def ===[R](right: R): Boolean = ${ TypeSafeEquals.equals('left, 'right) }
    inline def =!=[R](right: R): Boolean = ${ TypeSafeEquals.notEquals('left, 'right) }

    // Using these for testing since ScalaTest defines it's own === method
    inline def ≡[R](right: R): Boolean = ${ TypeSafeEquals.equals('left, 'right) }
    inline def ≠[R](right: R): Boolean = ${ TypeSafeEquals.notEquals('left, 'right) }
  }
}

/**
 * Simple attempt at providing a macro based implementation of type-safe equals
 */
object TypeSafeEquals {
    def equals[L: Type, R: Type](left: Expr[L], right: Expr[R])(using Quotes): Expr[Boolean] = {
      requireSubTypeRelationship(left, right)
      '{ $left == $right }
    }

    def notEquals[L: Type, R: Type](left: Expr[L], right: Expr[R])(using Quotes): Expr[Boolean] = {
      requireSubTypeRelationship(left, right)
      '{ $left != $right }
    }

    private def requireSubTypeRelationship[L: Type, R: Type](left: Expr[L], right: Expr[R])(using Quotes): Unit = {
      import quotes.reflect.{report, TypeRepr}

      val l: TypeRepr = TypeRepr.of[L]
      val r: TypeRepr = TypeRepr.of[R]

      val isSubType: Boolean = l =:= r || l <:< r || r <:< l
      if (!isSubType) report.error(s"TypeSafeEquals requires ${Type.show[L]} and ${Type.show[R]} to be in a subtype relationship!")
    }
}