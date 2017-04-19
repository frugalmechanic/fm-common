package fm.common

import java.util.concurrent.{ConcurrentHashMap => JavaConcurrentHashMap}
import java.util.Locale
import java.util.ResourceBundle.Control
import scala.collection.JavaConverters._

// This is near-duplicate to fm-common-web/ta.i18n.I18N object, changed the package name to make this available to other libraries
object I18N {
  import Implicits._
  
  /**
   * We don't directly use the ResourceBundle class but we do make use of
   * the ResourceBundle.Control class to handle the lookup logic via the
   * getCandidateLocales method.
   */
  private[this] val control: Control = Control.getNoFallbackControl(Control.FORMAT_DEFAULT)
  
  /**
   * We cache the candidate locale lookup lists
   */
  private[this] val candidateCache: JavaConcurrentHashMap[Locale,ImmutableArray[Locale]] = new JavaConcurrentHashMap()
  
  def candidateLocales(implicit locale: Locale): ImmutableArray[Locale] = {
    var res: ImmutableArray[Locale] = candidateCache.get(locale)
    
    if (null == res) {
      res = control.getCandidateLocales("", locale).asScala.toImmutableArray
      candidateCache.putIfAbsent(locale, res)
    }
    
    res
  }
}