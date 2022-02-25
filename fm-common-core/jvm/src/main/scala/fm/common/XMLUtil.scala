/*
 * Copyright 2014 Frugal Mechanic (http://frugalmechanic.com)
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

import com.ctc.wstx.stax.WstxInputFactory
import java.io.{File, InputStream, StringReader}
import java.nio.charset.Charset
import javax.xml.stream.{XMLInputFactory, XMLStreamReader}
import javax.xml.stream.XMLStreamConstants._
import org.apache.commons.io.input.BoundedInputStream
import org.codehaus.stax2.XMLStreamReader2

object XMLUtil {
  /** Does this look like valid XML (i.e. it starts with an opening XML element)? */
  def isXML(f: File): Boolean = InputStreamResource.forFile(f).buffered().use{ isXML(_) }

  /** Does this look like valid XML (i.e. it starts with an opening XML element)? */
  def isXML(is: InputStream): Boolean = isXML(is, true)

  /** Does this look like valid XML (i.e. it starts with an opening XML element)? */
  def isXML(is: InputStream, useMarkReset: Boolean): Boolean = {
    val markLimit: Int = 1024
    
    if (useMarkReset) {
      require(is.markSupported, "Need an InputStream that supports mark()/reset()")
      is.mark(markLimit)
    }
    
    try {
      val wrappedIs: InputStream = if (useMarkReset) new BoundedInputStream(is, markLimit) else is
      withXMLStreamReader2(wrappedIs){ isXML(_) }
    } catch {
      case _: Exception => false
    } finally {
      if (useMarkReset) is.reset()
    }
  }

  /** Does this look like valid XML (i.e. it starts with an opening XML element)? */
  def isXML(s: String): Boolean = withXMLStreamReader2(s){ isXML(_) }

  /** Is this complete and valid XML? */
  def isValidXML(s: String): Boolean = withXMLStreamReader2(s){ isValidXML(_) }
  
  def detectXMLCharset(is: InputStream): Option[Charset] = detectXMLCharset(is, true)
  def detectXMLCharset(is: InputStream, useMarkReset: Boolean): Option[Charset] = detectXMLCharsetName(is, useMarkReset).map{ CharsetUtil.forName }
  
  def detectXMLCharsetName(is: InputStream): Option[String] = detectXMLCharsetName(is, true)
  
  /** If this looks like an XML document attempt to detect it's encoding */
  def detectXMLCharsetName(is: InputStream, useMarkReset: Boolean): Option[String] = {
    val markLimit: Int = 1024
    
    if (useMarkReset) {
      require(is.markSupported, "Need an InputStream that supports mark()/reset()")
      is.mark(markLimit)
    }
    
    try {
      val wrappedIs: InputStream = if (useMarkReset) new BoundedInputStream(is, markLimit) else is
      withXMLStreamReader2(wrappedIs){ detectXMLCharsetName(_) }
    } catch {
      case _: Exception => None
    } finally {
      if (useMarkReset) is.reset()
    }
  }

  private def detectXMLCharsetName(xmlStreamReader: XMLStreamReader): Option[String] = {
    try {
      // Check if there are any START_ELEMENT events
      while (xmlStreamReader.getEventType != START_ELEMENT) xmlStreamReader.next()

      // If we found a START_ELEMENT then this looks like XML
      if (xmlStreamReader.getEventType == START_ELEMENT) Option(xmlStreamReader.getEncoding())
      else None
    } catch {
      case _: Exception => None
    }
  }

  /** Does this look like XML? */
  private def isXML(xmlStreamReader: XMLStreamReader): Boolean = {
    try {
      // Check if there are any START_ELEMENT events
      while (xmlStreamReader.getEventType != START_ELEMENT) xmlStreamReader.next()

      // If we found a START_ELEMENT then this looks like XML
      xmlStreamReader.getEventType == START_ELEMENT
    } catch {
      case _: Exception => false
    }
  }

  /** Is this complete and valid XML? */
  private def isValidXML(xmlStreamReader: XMLStreamReader2): Boolean = {
    try {
      // Check if there are any START_ELEMENT events
      while (xmlStreamReader.getEventType != START_ELEMENT) xmlStreamReader.next()

      // If we found a START_ELEMENT then this looks like XML
      require(xmlStreamReader.getEventType == START_ELEMENT)

      // Skip the root element
      xmlStreamReader.skipElement()

      // Now we should be at the closing tag
      require(xmlStreamReader.getEventType == END_ELEMENT)

      while (xmlStreamReader.hasNext) {
        /*
        public static final int START_ELEMENT=1;
        public static final int END_ELEMENT=2;
        public static final int PROCESSING_INSTRUCTION=3;
        public static final int CHARACTERS=4;
        public static final int COMMENT=5;
        public static final int SPACE=6;
        public static final int START_DOCUMENT=7;
        public static final int END_DOCUMENT=8;
        public static final int ENTITY_REFERENCE=9;
        public static final int ATTRIBUTE=10;
        public static final int DTD=11;
        public static final int CDATA=12;
        public static final int NAMESPACE=13;
        public static final int NOTATION_DECLARATION=14;
        public static final int ENTITY_DECLARATION=15;
        */
        xmlStreamReader.next() match {
          case START_ELEMENT | END_ELEMENT | CHARACTERS | CDATA => return false // I think these are the only ones we care about
          case _ => // Everything else is okay
        }
      }

      true
    } catch {
      case _: Exception => false
    }
  }
  
  // Note: This is duplicated in the fm-xml project
  private def inputFactory: WstxInputFactory = {
    val f: WstxInputFactory = new WstxInputFactory()
    f.setProperty(XMLInputFactory.SUPPORT_DTD, false)
    f.configureForSpeed()
    f
  }

  // Note: This is duplicated in the fm-xml project
  def withXMLStreamReader2[T](s: String)(f: XMLStreamReader2 => T): T = {
    import Resource._
    Resource.using(inputFactory.createXMLStreamReader(new StringReader(s)).asInstanceOf[XMLStreamReader2])(f)
  }

  // Note: This is duplicated in the fm-xml project
  def withXMLStreamReader2[T](is: InputStream)(f: XMLStreamReader2 => T): T = {
    import Resource._
    Resource.using(inputFactory.createXMLStreamReader(is).asInstanceOf[XMLStreamReader2])(f)
  }
  
  def rootTag(f: File): String = InputStreamResource.forFile(f).use{ rootTag }
  
  private def rootTag(is: InputStream): String = withXMLStreamReader2(is){ (xmlStreamReader: XMLStreamReader) =>
    // Skip to the root tag (which is the first START_ELEMENT)
    while(xmlStreamReader.getEventType != START_ELEMENT) xmlStreamReader.next()
    xmlStreamReader.getLocalName
  }
}