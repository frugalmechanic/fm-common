/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 by Lloyd Chan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * This is from: https://github.com/lloydmeta/enumeratum
 */
package fm.common

object EnumContextUtils {

  type Context = scala.reflect.macros.blackbox.Context

  // Constant types
  type CTLong = Long
  type CTInt  = Int
  type CTChar = Char

  /**
   * Returns a TermName
   */
  def termName(c: Context)(name: String): c.universe.TermName = {
    c.universe.TermName(name)
  }

  /**
   * Returns a companion symbol
   */
  def companion(c: Context)(sym: c.Symbol): c.universe.Symbol = sym.companion

  /**
   * Returns a PartialFunction for turning symbols into names
   */
  def constructorsToParamNamesPF(
    c: Context
  ): PartialFunction[c.universe.Symbol, List[c.universe.Name]] = {
    case m if m.isConstructor =>
      m.asMethod.paramLists.flatten.map(_.asTerm.name)
  }

  /**
   * Returns the reserved constructor name
   */
  def constructorName(c: Context): c.universe.TermName = {
    c.universe.termNames.CONSTRUCTOR
  }

  /**
   * Returns a named arg extractor
   */
  def namedArg(c: Context) = c.universe.AssignOrNamedArg
}