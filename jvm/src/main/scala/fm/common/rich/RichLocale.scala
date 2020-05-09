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

import fm.common.Implicits.{toRichCharSequence, toTypeSafeEquals}
import fm.common.{ImmutableArray, LoadingCache}
import java.text.Collator
import java.util.ResourceBundle.Control
import java.util.{Comparator, Locale}
import scala.util.Try
import scala.collection.JavaConverters._

object RichLocale {
  import fm.common.Implicits.toRichString

  private def DefaultCacheSize: Int = 256
  
  private val LocaleCacheSize: Int = System.getProperty("fm.common.LocaleCacheSize").toIntOptionCached.getOrElse(DefaultCacheSize)

  private val comparatorCache: LoadingCache[Locale, Comparator[String]] = {
    LoadingCache(maxSize = LocaleCacheSize){ locale: Locale =>
      Option(Collator.getInstance(locale)).map{ c => c.setStrength(Collator.PRIMARY); c.setDecomposition(Collator.CANONICAL_DECOMPOSITION); c }.map{ _.asInstanceOf[Comparator[String]] } getOrElse String.CASE_INSENSITIVE_ORDER
    }
  }
  
  private val stringOrderingCache: LoadingCache[Locale, Ordering[String]] = {
    LoadingCache(maxSize = LocaleCacheSize){ locale: Locale =>
      Ordering.comparatorToOrdering(comparatorCache.get(locale))
    }
  }
  
  private val stringOptionOrderingCache: LoadingCache[Locale, Ordering[Option[String]]] = {
    LoadingCache(maxSize = LocaleCacheSize){ locale: Locale =>
      Ordering.Option(stringOrderingCache.get(locale))
    }
  }
  
  private val reversedComparatorCache: LoadingCache[Locale, Comparator[String]] = {
    LoadingCache(maxSize = LocaleCacheSize){ locale: Locale =>
      comparatorCache.get(locale).reversed()
    }
  }
  
  private val reversedStringOrderingCache: LoadingCache[Locale, Ordering[String]] = {
    LoadingCache(maxSize = LocaleCacheSize){ locale: Locale =>
      Ordering.comparatorToOrdering(reversedComparatorCache.get(locale))
    }
  }
  
  private val reversedStringOptionOrderingCache: LoadingCache[Locale, Ordering[Option[String]]] = {
    LoadingCache(maxSize = LocaleCacheSize){ locale: Locale =>
      Ordering.Option(reversedStringOrderingCache.get(locale))
    }
  }

  // Note: It looks like there is already some caching in Control.getCandidateLocales.  However there might still
  //       be benefit in caching the resulting ImmutableArray here to prevent excessive allocation since it looks like
  //       Control.getCandidateLocales makes an ArrayList copy on every lookup which is not ideal.
  private val candidateLocalesCache: LoadingCache[Locale, ImmutableArray[Locale]] = {
    LoadingCache(maxSize = LocaleCacheSize){ locale: Locale =>
      // We do not directly use the ResourceBundle class but we do make use of
      // the ResourceBundle.Control class to handle the lookup logic via the
      // getCandidateLocales method.
      val control: Control = Control.getNoFallbackControl(Control.FORMAT_DEFAULT)

      ImmutableArray.copy(control.getCandidateLocales("", locale).asScala)
    }
  }
}

final class RichLocale(val self: Locale) extends AnyVal {

  def isChinese: Boolean = matchesLanguage(Locale.CHINESE)
  def isEnglish: Boolean = matchesLanguage(Locale.ENGLISH)
  def isFrench: Boolean = matchesLanguage(Locale.FRENCH)
  def isGerman: Boolean = matchesLanguage(Locale.GERMAN)
  def isItalian: Boolean = matchesLanguage(Locale.ITALIAN)
  def isJapanese: Boolean = matchesLanguage(Locale.JAPANESE)
  def isKorean: Boolean = matchesLanguage(Locale.KOREAN)

  @inline private def matchesLanguage(other: Locale): Boolean = null != other && other.getLanguage === self.getLanguage

  def languageTag: String = self.toLanguageTag()

  // Note: This is package private since there isn't currently a use case for this method but we have tests setup for it
  /** Does this locale have a valid non-blank language? */
  private[common] def hasNonBlankValidLanguage: Boolean = Try{ self.getISO3Language.isNotNullOrBlank }.getOrElse(false)

  // Note: This is package private since there isn't currently a use case for this method but we have tests setup for it
  /** Does this locale have a valid non-blank country set? */
  private[common] def hasNonBlankValidCountry: Boolean = Try { self.getISO3Country.isNotNullOrBlank }.getOrElse(false)

  /** The Locale is considered valid if there is a valid (or blank) language and a valid (or blank) country */
  def isValid: Boolean = {
    Try {
      self.getISO3Language()
      self.getISO3Country()
    }.isSuccess
  }

  def candidateLocales: ImmutableArray[Locale] = RichLocale.candidateLocalesCache.get(self)
  
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