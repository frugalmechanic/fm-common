package fm.common

import org.scalajs.dom.raw.HTMLDocument
import scala.reflect.{classTag, ClassTag}

/**
 * Used in RichHTMLDocument
 */
final class HTMLDocumentById(val document: HTMLDocument) extends AnyVal {
  def apply[T : HTMLElementType : ClassTag](id: String): T = get[T](id).getOrElse{ throw new NoSuchElementException(s"No matching element for id: $id") }
  def get[T : HTMLElementType : ClassTag](id: String): Option[T] = Option(document.getElementById(id.stripLeading("#"))).filter{ classTag[T].runtimeClass.isInstance }.map{ _.asInstanceOf[T] }
}