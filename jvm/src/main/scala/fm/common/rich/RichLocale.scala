/*
 * Copyright 2015 Frugal Mechanic (http://frugalmechanic.com)
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
package fm.common.rich

import fm.common.LoadingCache
import java.text.Collator
import java.util.{Comparator, Locale}
import scala.util.Try

object RichLocale {
  private val comparatorCache: LoadingCache[Locale, Comparator[String]] = LoadingCache(){ locale: Locale =>
    Option(Collator.getInstance(locale)).map{ c => c.setStrength(Collator.PRIMARY); c.setDecomposition(Collator.CANONICAL_DECOMPOSITION); c }.map{ _.asInstanceOf[Comparator[String]] } getOrElse String.CASE_INSENSITIVE_ORDER
  }
  
  private val stringOrderingCache: LoadingCache[Locale, Ordering[String]] = LoadingCache(){ locale: Locale =>
    Ordering.comparatorToOrdering(comparatorCache.get(locale))
  }
  
  private val stringOptionOrderingCache: LoadingCache[Locale, Ordering[Option[String]]] = LoadingCache(){ locale: Locale =>
    Ordering.Option(stringOrderingCache.get(locale))
  }
  
  private val reversedComparatorCache: LoadingCache[Locale, Comparator[String]] = LoadingCache(){ locale: Locale =>
    comparatorCache.get(locale).reversed()
  }
  
  private val reversedStringOrderingCache: LoadingCache[Locale, Ordering[String]] = LoadingCache(){ locale: Locale =>
    Ordering.comparatorToOrdering(reversedComparatorCache.get(locale))
  }
  
  private val reversedStringOptionOrderingCache: LoadingCache[Locale, Ordering[Option[String]]] = LoadingCache(){ locale: Locale =>
    Ordering.Option(reversedStringOrderingCache.get(locale))
  }
}

final class RichLocale(val self: Locale) extends AnyVal {
  
  def languageTag: String = self.toLanguageTag()
  
  def isValidLanguage: Boolean = Try{ self.getISO3Language }.filter(_.nonEmpty).toOption.isDefined
  def isValidCountry: Boolean = Try{ self.getISO3Country }.filter(_.nonEmpty).toOption.isDefined
  def hasCountry: Boolean = isValidCountry
  
  def isValid: Boolean = isValidLanguage // && isValidCountry
  
  def displayName(implicit locale: Locale): String = self.getDisplayName(locale)
  def displayCountry(implicit locale: Locale): String = self.getDisplayCountry(locale)
  def displayLanguage(implicit locale: Locale): String = self.getDisplayLanguage(locale)
  def displayScript(implicit locale: Locale): String = self.getDisplayScript(locale)
  def displayVariant(implicit locale: Locale): String = self.getDisplayVariant(locale)
  
  def stringComparator: Comparator[String] = RichLocale.comparatorCache.get(self)
  def stringOrdering: Ordering[String] = RichLocale.stringOrderingCache.get(self)
  def stringOptionOrdering: Ordering[Option[String]] = RichLocale.stringOptionOrderingCache.get(self)
  
  def reversedStringComparator: Comparator[String] = RichLocale.reversedComparatorCache.get(self)
  def reversedStringOrdering: Ordering[String] = RichLocale.reversedStringOrderingCache.get(self)
  def reversedStringOptionOrdering: Ordering[Option[String]] = RichLocale.reversedStringOptionOrderingCache.get(self)
}