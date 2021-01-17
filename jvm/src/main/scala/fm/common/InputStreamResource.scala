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
package fm.common

import java.io._
import java.nio.{ByteBuffer, MappedByteBuffer}
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.io.ByteOrderMark
import org.apache.commons.io.input.BOMInputStream

object InputStreamResource {
  implicit def toInputStreamResource(resource: Resource[InputStream]): InputStreamResource =  resource match {
    case isr: InputStreamResource => isr
    case _ => InputStreamResource(resource, autoDecompress = false, autoBuffer = false)
  }
  
  def forBytes(bytes: Array[Byte], fileName: String = "", autoDecompress: Boolean = true): InputStreamResource = {
    InputStreamResource(MultiUseResource{ new ByteArrayInputStream(bytes) }, fileName = fileName, autoDecompress = autoDecompress, autoBuffer = false)
  }
  
  @deprecated("Use InputStreamResource.forInputStream instead","")
  def wrap(is: InputStream, fileName: String = "", autoDecompress: Boolean = true, autoBuffer: Boolean = true): InputStreamResource = {
    InputStreamResource(SingleUseResource(is), fileName = fileName, autoDecompress = autoDecompress, autoBuffer = autoBuffer)
  }
  
  def forInputStream(is: InputStream, fileName: String = "", autoDecompress: Boolean = true, autoBuffer: Boolean = true): InputStreamResource = {
    InputStreamResource(SingleUseResource(is), fileName = fileName, autoDecompress = autoDecompress, autoBuffer = autoBuffer)
  }
  
  def forFileOrResource(
    file: File,
    originalFileName: String = "",
    autoDecompress: Boolean = true,
    autoBuffer: Boolean = true,
    classLoader: ClassLoader = defaultClassLoader
  ): InputStreamResource = {
    val resource: Resource[InputStream] = MultiUseResource{
      if (file.isFile && file.canRead) newFileInputStream(file, autoDecompress) else classLoader.getResourceAsStream(file.toResourcePath)
    }.map{ is: InputStream =>
      if (null == is) throw new IOException("Missing File or Classpath Resource: "+file)
      is
    }

    forFileImpl(resource, file, originalFileName = originalFileName, autoDecompress = autoDecompress, autoBuffer = autoBuffer)
  }
  
  def forFile(
    file: File,
    originalFileName: String = "",
    autoDecompress: Boolean = true,
    autoBuffer: Boolean = true
  ): InputStreamResource = {
    val resource: Resource[InputStream] = MultiUseResource{ newFileInputStream(file, autoDecompress) }.map{ is: InputStream =>
      if (null == is) throw new IOException("Missing File: "+file)
      is
    }

    forFileImpl(resource, file, originalFileName = originalFileName, autoDecompress = autoDecompress, autoBuffer = autoBuffer)
  }
  
  def forRandomAccessFile(
    raf: RandomAccessFile,
    originalFileName: String = "",
    autoDecompress: Boolean = true,
    autoBuffer: Boolean = true
  ): InputStreamResource = {
    val bufs: Vector[MappedByteBuffer] = ByteBufferUtil.map(raf, FileChannel.MapMode.READ_ONLY)
    forByteBuffers(bufs, originalFileName = originalFileName, autoDecompress = autoDecompress, autoBuffer = autoBuffer)
  }
  
  def forByteBuffer(
    buf: ByteBuffer,
    originalFileName: String = "",
    autoDecompress: Boolean = true,
    autoBuffer: Boolean = true
  ): InputStreamResource = {
    val resource: Resource[InputStream] = MultiUseResource{ new ByteBufferInputStream(buf.duplicate()) }
    InputStreamResource(resource, fileName = originalFileName, autoDecompress = autoDecompress, autoBuffer = autoBuffer)
  }
  
  def forByteBuffers(
    bufs: Vector[ByteBuffer],
    originalFileName: String = "",
    autoDecompress: Boolean = true,
    autoBuffer: Boolean = true
  ): InputStreamResource = {
    val resource: Resource[InputStream] = MultiUseResource{ ByteBufferInputStream(bufs) }
    InputStreamResource(resource, fileName = originalFileName, autoDecompress = autoDecompress, autoBuffer = autoBuffer)
  }
  
  def forResource(
    file: File,
    originalFileName: String = "",
    autoDecompress: Boolean = true,
    autoBuffer: Boolean = true,
    classLoader: ClassLoader = defaultClassLoader
  ): InputStreamResource = {
    val resource: Resource[InputStream] = MultiUseResource{ classLoader.getResourceAsStream(file.toResourcePath) }.map{ is: InputStream =>
      if (null == is) throw new IOException("Missing Classpath Resource: "+file)
      is
    }
    forFileImpl(resource, file, originalFileName = originalFileName, autoDecompress = autoDecompress, autoBuffer = autoBuffer)
  }
  
  private def forFileImpl(
    resource: Resource[InputStream],
    file: File,
    originalFileName: String,
    autoDecompress: Boolean,
    autoBuffer: Boolean
  ): InputStreamResource = {
    val fileName: String = originalFileName.toBlankOption.getOrElse{ file.getName }
    InputStreamResource(resource, fileName = fileName, autoDecompress = autoDecompress, autoBuffer = autoBuffer)
  }
  
  private def defaultClassLoader: ClassLoader = {
    val cl: ClassLoader = Thread.currentThread.getContextClassLoader
    if (null != cl) cl else getClass().getClassLoader()
  }
  
  // Not using this any more since it doesn't play nicely with Proguard (the ScalaSig references org.apache.commons.io.ByteOrderMark which breaks on 2.10.x)
  // Note - This cannot be an array since the BOMInputStream class modifies it
  //private val BOMs: Vector[ByteOrderMark] = Vector(ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE)
  
  private val BOMCharsets: Set[Charset] = {
    // Keep this in sync with newBOMInputStream
    val BOMs: Vector[ByteOrderMark] = Vector(
      ByteOrderMark.UTF_8,
      ByteOrderMark.UTF_16LE,
      ByteOrderMark.UTF_16BE,
      ByteOrderMark.UTF_32LE,
      ByteOrderMark.UTF_32BE
    )
    
    // Avoiding closures so no methods have ByteOrderMark in their signature (so Proguard doesn't complain)
    val setBuilder = Set.newBuilder[Charset]
    var i = 0
    
    while (i < BOMs.length) {
      setBuilder += CharsetUtil.forName(BOMs(i).getCharsetName)
      i += 1
    }

    // Important: Also add our custom UTF_8_BOM Charset that wraps UTF-8
    setBuilder += UTF_8_BOM

    setBuilder.result
  }
  
  // Keep this in sync with BOMCharsets
  private def newBOMInputStream(is: InputStream): InputStream = {
    new BOMInputStream(is, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE)
  }
  
  // Keep this in sync with BOMCharsets
  private def newBOMInputStreamReader(is: InputStream): Reader = {
    val bis: BOMInputStream = new BOMInputStream(is, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE)
    val charset: String = if (bis.hasBOM()) bis.getBOMCharsetName else "UTF-8"
    new InputStreamReader(bis, charset)
  }

  /**
    * Wraps creating a FileInputStream with special support for detecting and handling a 7z file which cannot be
    * decompressed from an InputStream and only work directly on the File or a SeekableByteChannel
    */
  private def newFileInputStream(file: File, autoDecompress: Boolean): InputStream = {
    if (autoDecompress && file.getName.toLowerCase.endsWith(".7z")) new SevenZipInputStream(file)
    else new FileInputStream(file)
  }

  /**
    * Simple wrapper over the commons-compress SevenZFile that allows us to read the first file in the archive
    */
  private class SevenZipInputStream(file: SevenZFile) extends InputStream {
    // Advance to the first entry
    file.getNextEntry

    def this(file: File) = this(new SevenZFile(file))
    def read(): Int = file.read()
    override def read(b: Array[Byte]): Int = file.read(b)
    override def read(b: Array[Byte], off: Int, len: Int): Int = file.read(b, off, len)
    override def close(): Unit = file.close()
  }
}

final case class InputStreamResource(
  resource: Resource[InputStream],
  fileName: String = "",
  autoDecompress: Boolean = true,
  autoBuffer: Boolean = true
) extends Resource[InputStream] with Logging {
  import InputStreamResource.{BOMCharsets, newBOMInputStream, newBOMInputStreamReader}
  
  def isUsable: Boolean = resource.isUsable
  def isMultiUse: Boolean = resource.isMultiUse
  
  // If the input stream is null for some reason (e.g. file/resource doesn't exist) then this will
  // cause an exception to be thrown before map/flatMap the resource to make the exceptions easier to understand
  private def nullProtectedResource: Resource[InputStream] = resource.map { is: InputStream =>
    if (null == is) throw new IOException("InputStream is null!"+fileName.toBlankOption.map{ n: String => s"  fileName: $n"  })
    is
  }
  
  def use[T](f: InputStream => T): T = bufferedFilter(decompressFilter(nullProtectedResource)).use { is: InputStream =>
    try {
     f(is)
    } catch {
     case ex: Exception =>
       logger.error(s"InputStreamResource Exception, working on: $fileName, exception: ${ex.getMessage}")
       throw ex
   }
  }
  
  /**
   * Create a reader for this InputStream and use auto-detection for the charset encoding with a fallback of UTF-8 if the charset cannot be detected
   */
  def reader(): Resource[Reader] = readerWithDetectedCharset()
  
  /**
   * Create a reader for this InputStream using the given encoding or auto-detect the encoding if the parameter is blank
   */
  def reader(encoding: String): Resource[Reader] = {
    if (encoding.isNotNullOrBlank) reader(CharsetUtil.forName(encoding))
    else readerWithDetectedCharset()
  }
  
  /**
   * Create a reader for this InputStream using the given encoding or auto-detect the encoding if the parameter is blank
   */
  def reader(charset: Charset): Resource[Reader] = flatMap { is: InputStream =>
    val wrappedInputStream: InputStream = if (BOMCharsets.contains(charset)) newBOMInputStream(is) else is
    
    SingleUseResource(new InputStreamReader(wrappedInputStream, charset))
  }
  
  /**
   * Creates a UTF-8/16/32 reader based on the BOM encoding with UTF-8 being a default
   */
  def utfReader(): Resource[Reader] = flatMap { is: InputStream =>
    SingleUseResource(newBOMInputStreamReader(is))
  }
  
  def bufferedUTFReader(): Resource[Reader] = utfReader() flatMap { r => Resource(new BufferedReader(r)) }
  
  def readToString(): String = readToString("")
  
  /** A helper to read the input stream to a string */
  def readToString(encoding: String): String = reader(encoding).use { IOUtils.toString }
  
  /** A helper to read the input stream to a string */
  def readToString(charset: Charset): String = reader(charset).use { IOUtils.toString }
  
  def readBytes(): Array[Byte] = use{ is: InputStream =>
    val os = new fm.common.ByteArrayOutputStream
    IOUtils.copy(is, os)
    os.toByteArray()
  }
  
  def md5: Array[Byte]  = use{ DigestUtils.md5     }
  def md5Hex: String    = use{ DigestUtils.md5Hex  }
  def sha1: Array[Byte] = use{ DigestUtils.sha1    }
  def sha1Hex: String   = use{ DigestUtils.sha1Hex }
  
  def writeTo(output: Resource[OutputStream]): Unit = output.use{ writeTo }
  
  def writeTo(output: OutputStream): Unit = use{ input: InputStream => IOUtils.copy(input, output) }
  
  def buffered(): Resource[BufferedInputStream] = flatMap{ _.toBufferedInputStream }

  def readerWithDetectedCharset(): Resource[Reader] = flatMap{ is: InputStream =>
    // Need a mark supported InputStream (e.g. BufferedInputStream) for doing the charset detection
    val markSupportedInputStream: InputStream = if (is.markSupported) is else new BufferedInputStream(is)
    
    val charsetName: String = IOUtils.detectCharsetName(markSupportedInputStream, useMarkReset = true).orElse{
      // If this is a multi-use resource let's go ahead and try charset detection on the full stream
      if (isMultiUse) use{ IOUtils.detectCharsetName(_, useMarkReset = false) } else None
    }.getOrElse("UTF-8")
    
    // Use org.apache.commons.io.input.BOMInputStream to filter out the BOM bytes if they exist
    SingleUseResource(new InputStreamReader(newBOMInputStream(markSupportedInputStream), charsetName))
  }
  
  /** Requires use() to be called so it will consume the Resource */
  def detectCharset(): Option[Charset] = detectCharsetName().map{ CharsetUtil.forName }
  
  /** Requires use() to be called so it will consume the Resource */
  def detectCharsetName(): Option[String] = use { is: InputStream => IOUtils.detectCharsetName(is, useMarkReset = false) }
  
  def bufferedReader(): Resource[BufferedReader] = reader() flatMap { r => Resource(new BufferedReader(r)) }
  def bufferedReader(encoding: String): Resource[BufferedReader] = reader(encoding) flatMap { r => Resource(new BufferedReader(r)) }
  def bufferedReader(cs: Charset): Resource[BufferedReader] = reader(cs) flatMap { r => Resource(new BufferedReader(r)) }
  
  def dataInput(): Resource[DataInput] = flatMap{ is => Resource(new DataInputStream(is)) }

  private def decompressFilter(resource: Resource[InputStream]): Resource[InputStream] = {
    val lowerFileName: String = fileName.toLowerCase
    
    if (!autoDecompress) resource
    else if (lowerFileName.endsWith(".tar.gz"))    untar(gunzip(resource))
    else if (lowerFileName.endsWith(".tgz"))       untar(gunzip(resource))
    else if (lowerFileName.endsWith(".tar.bz"))    untar(bunzip2(resource))
    else if (lowerFileName.endsWith(".tar.bz2"))   untar(bunzip2(resource))
    else if (lowerFileName.endsWith(".tar.bzip2")) untar(bunzip2(resource))
    else if (lowerFileName.endsWith(".tbz2"))      untar(bunzip2(resource))
    else if (lowerFileName.endsWith(".tbz"))       untar(bunzip2(resource))
    else if (lowerFileName.endsWith(".tar.xz"))    untar(unxz(resource))
    else if (lowerFileName.endsWith(".tar"))       untar(resource)
    else if (lowerFileName.endsWith(".gz"))        gunzip(resource)
    else if (lowerFileName.endsWith(".bzip2"))     bunzip2(resource)
    else if (lowerFileName.endsWith(".bz2"))       bunzip2(resource)
    else if (lowerFileName.endsWith(".bz"))        bunzip2(resource)
    else if (lowerFileName.endsWith(".snappy"))    unsnappy(resource)
    else if (lowerFileName.endsWith(".xz"))        unxz(resource)
    else if (lowerFileName.endsWith(".zip"))       unzip(resource)
    else if (lowerFileName.endsWith(".jar"))       unjar(resource)
    else if (lowerFileName.endsWith(".zst"))       unzstd(resource)
//    else if (lowerFileName.endsWith(".7z"))        un7zip(resource) // Does not work since you cannot stream .7z files
    else resource
  }
  
  private def gunzip(r: Resource[InputStream]):   Resource[InputStream] = r.flatMap{ _.gunzip   }
  private def unsnappy(r: Resource[InputStream]): Resource[InputStream] = r.flatMap{ _.unsnappy }
  private def bunzip2(r: Resource[InputStream]):  Resource[InputStream] = r.flatMap{ _.bunzip2  }
  private def unxz(r: Resource[InputStream]):     Resource[InputStream] = r.flatMap{ _.unxz     }
  private def unzip(r: Resource[InputStream]):    Resource[InputStream] = r.flatMap{ _.unzip    }
  private def unjar(r: Resource[InputStream]):    Resource[InputStream] = r.flatMap{ _.unjar    }
  private def untar(r: Resource[InputStream]):    Resource[InputStream] = r.flatMap{ _.untar    }
  private def unzstd(r: Resource[InputStream]):   Resource[InputStream] = r.flatMap{ _.unzstd   }
//  private def un7zip(r: Resource[InputStream]):   Resource[InputStream] = r.flatMap{ _.un7zip   }

  def showArchiveEntries(): Unit = resource.use { is: InputStream =>
    val bufferedIS: InputStream = if (is.markSupported()) is else is.toBufferedInputStream
    bufferedIS.showArchiveEntries()
  }
  
  private def bufferedFilter(resource: Resource[InputStream]): Resource[InputStream] = {
    if (autoBuffer) resource.flatMap{ _.toBufferedInputStream } else resource
  }
}