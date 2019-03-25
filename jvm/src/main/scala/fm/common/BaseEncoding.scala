package fm.common

import com.google.common.io.{BaseEncoding => GuavaBaseEncoding}
import java.io.{FilterInputStream, IOException, FilterOutputStream}
import java.nio.charset.StandardCharsets.UTF_8

/**
 * Hex (Base16) encoding/decoding
 * 
 * Note: encodes to lowercase by default but will decode both lower/upper case Hex.
 */
object Base16 extends BaseEncoding {
  private[this] val base16Upper: GuavaBaseEncoding = GuavaBaseEncoding.base16()
  private[this] val base16Lower: GuavaBaseEncoding = GuavaBaseEncoding.base16().lowerCase()
  
  def decode(data: Array[Char]): Array[Byte] = try { if (isLower(data)) base16Lower.decode(data) else base16Upper.decode(data) } catch exceptionHandler
  def decode(data: CharSequence): Array[Byte] = try { if (isLower(data)) base16Lower.decode(data) else base16Upper.decode(data) } catch exceptionHandler

  def encode(bytes: Array[Byte]): String = base16Lower.encode(bytes)
  def encode(bytes: Array[Byte], offset: Int, length: Int): String = base16Lower.encode(bytes, offset, length)
  
  def encodeUpper(bytes: Array[Byte]): String = base16Upper.encode(bytes)
  def encodeUpper(bytes: Array[Byte], offset: Int, length: Int): String = base16Upper.encode(bytes, offset, length)
}

/**
 * Base32 encoding/decoding
 * 
 * Note: encodes to lowercase by default but will decode both lower/upper case Base32.
 */
object Base32 extends BaseEncoding {
  private[this] val base32Lower: GuavaBaseEncoding = GuavaBaseEncoding.base32().lowerCase()
  private[this] val base32LowerNoPadding: GuavaBaseEncoding = base32Lower.omitPadding()
  private[this] val base32Upper: GuavaBaseEncoding = GuavaBaseEncoding.base32()
  private[this] val base32UpperNoPadding: GuavaBaseEncoding = base32Upper.omitPadding()
  
  def decode(data: Array[Char]): Array[Byte] = try { if (isLower(data)) base32Lower.decode(data) else base32Upper.decode(data) } catch exceptionHandler
  def decode(data: CharSequence): Array[Byte] = try { if (isLower(data)) base32Lower.decode(data) else base32Upper.decode(data) } catch exceptionHandler

  def encode(bytes: Array[Byte]): String = base32Lower.encode(bytes)
  def encode(bytes: Array[Byte], offset: Int, length: Int): String = base32Lower.encode(bytes, offset, length)
  
  def encodeNoPadding(bytes: Array[Byte]): String = base32LowerNoPadding.encode(bytes)
  def encodeNoPadding(bytes: Array[Byte], offset: Int, length: Int): String = base32LowerNoPadding.encode(bytes, offset, length)
  
  def encodeUpper(bytes: Array[Byte]): String = base32Upper.encode(bytes)
  def encodeUpper(bytes: Array[Byte], offset: Int, length: Int): String = base32Upper.encode(bytes, offset, length)
  
  def encodeUpperNoPadding(bytes: Array[Byte]): String = base32UpperNoPadding.encode(bytes)
  def encodeUpperNoPadding(bytes: Array[Byte], offset: Int, length: Int): String = base32UpperNoPadding.encode(bytes, offset, length)
}

/**
 * Base32Hex encoding/decoding
 * 
 * Note: encodes to lowercase by default but will decode both lower/upper case Base32.
 */
object Base32Hex extends BaseEncoding {
  private[this] val base32Lower: GuavaBaseEncoding = GuavaBaseEncoding.base32Hex().lowerCase()
  private[this] val base32LowerNoPadding: GuavaBaseEncoding = base32Lower.omitPadding()
  private[this] val base32Upper: GuavaBaseEncoding = GuavaBaseEncoding.base32Hex()
  private[this] val base32UpperNoPadding: GuavaBaseEncoding = base32Upper.omitPadding()
  
  def decode(data: Array[Char]): Array[Byte] = try { if (isLower(data)) base32Lower.decode(data) else base32Upper.decode(data) } catch exceptionHandler
  def decode(data: CharSequence): Array[Byte] = try { if (isLower(data)) base32Lower.decode(data) else base32Upper.decode(data) } catch exceptionHandler

  def encode(bytes: Array[Byte]): String = base32Lower.encode(bytes)
  def encode(bytes: Array[Byte], offset: Int, length: Int): String = base32Lower.encode(bytes, offset, length)
  
  def encodeNoPadding(bytes: Array[Byte]): String = base32LowerNoPadding.encode(bytes)
  def encodeNoPadding(bytes: Array[Byte], offset: Int, length: Int): String = base32LowerNoPadding.encode(bytes, offset, length)
  
  def encodeUpper(bytes: Array[Byte]): String = base32Upper.encode(bytes)
  def encodeUpper(bytes: Array[Byte], offset: Int, length: Int): String = base32Upper.encode(bytes, offset, length)
  
  def encodeUpperNoPadding(bytes: Array[Byte]): String = base32UpperNoPadding.encode(bytes)
  def encodeUpperNoPadding(bytes: Array[Byte], offset: Int, length: Int): String = base32UpperNoPadding.encode(bytes, offset, length)
}

/**
 * Base64 encoding/decoding methods.
 * 
 * Note: This will decode normal Base64 and the modified Base64 for URL variant.  If you don't
 *       want this behavior then use Base64Strict or Base64URL directly.
 */
object Base64 extends BaseEncoding {
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // START - Compatibility with old Base64 Java Class
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  @Deprecated val NO_OPTIONS: Int = 0
  @Deprecated val URL_SAFE: Int = 16
  
  @Deprecated def encodeBytes(bytes: Array[Byte]): String = encode(bytes)
  @Deprecated def encodeBytes(bytes: Array[Byte], options: Int): String = if (options == URL_SAFE) encodeURL(bytes) else encode(bytes)
  @Deprecated def decode(data: CharSequence, options: Int): Array[Byte] = decode(data)
  @Deprecated def decode(data: Array[Byte]): Array[Byte] = decode(new String(data, UTF_8))
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // END - Compatibility with old Base64 Java Class
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  final class InputStream(is: java.io.InputStream) extends FilterInputStream(java.util.Base64.getDecoder().wrap(new URLToStrictInputStream(is)))
  final class OutputStream(os: java.io.OutputStream) extends FilterOutputStream(java.util.Base64.getEncoder().wrap(os))
  
  /** This translates from the Base64 URL Variant to Normal Base64 */
  private[common] class URLToStrictInputStream(is: java.io.InputStream) extends FilterInputStream(is) {
    override def read(): Int = {
      val ch: Int = super.read()
      
      if (ch == '-') '+'
      else if (ch == '_') '/'
      else ch
    }
    
    override def read(b: Array[Byte]): Int = fixup(b, 0, super.read(b))
    override def read(b: Array[Byte], off: Int, len: Int): Int = fixup(b, off, super.read(b, off, len))
    
    private def fixup(b: Array[Byte], off: Int, len: Int): Int = {
      if (-1 == len) return -1
      
      var i: Int = off
      
      while (i < off + len) {
        val ch: Int = b(i)
        if (ch == '-') b(i) = '+'
        else if (ch == '_') b(i) = '/'
        
        i += 1
      }
      
      len
    }
  }
  
  def encode(bytes: Array[Byte]): String = Base64Strict.encode(bytes)
  def encode(bytes: Array[Byte], offset: Int, length: Int): String = Base64Strict.encode(bytes, offset, length)
  
  def encodeNoPadding(bytes: Array[Byte]): String = Base64Strict.encodeNoPadding(bytes)
  def encodeNoPadding(bytes: Array[Byte], offset: Int, length: Int): String = Base64Strict.encodeNoPadding(bytes, offset, length)
  
  def encodeURL(bytes: Array[Byte]): String = Base64URL.encode(bytes)
  def encodeURL(bytes: Array[Byte], offset: Int, length: Int): String = Base64URL.encodeNoPadding(bytes, offset, length)
  
  def encodeURLNoPadding(bytes: Array[Byte]): String = Base64URL.encodeNoPadding(bytes)
  def encodeURLNoPadding(bytes: Array[Byte], offset: Int, length: Int): String = Base64URL.encodeNoPadding(bytes, offset, length)
  
  def decode(data: Array[Char]): Array[Byte] = {
    var i: Int = 0
    var isStrict: Boolean = false
    var isURL: Boolean = false
    
    while (i < data.length && !(isStrict || isURL)) {
      val ch: Char = data(i)
      if (ch == '+' || ch == '/') isStrict = true
      else if (ch == '-' || ch == '_') isURL = true
      i += 1
    }
    
    if (isURL) Base64URL.decode(data) else Base64Strict.decode(data)
  }
  
  def decode(data: CharSequence): Array[Byte] = {
    var i: Int = 0
    var isStrict: Boolean = false
    var isURL: Boolean = false
    
    while (i < data.length && !(isStrict || isURL)) {
      val ch: Char = data.charAt(i)
      if (ch == '+' || ch == '/') isStrict = true
      else if (ch == '-' || ch == '_') isURL = true
      i += 1
    }
    
    if (isURL) Base64URL.decode(data) else Base64Strict.decode(data)
  }
}

object Base64Strict extends BaseEncoding {
  private[this] val base64: GuavaBaseEncoding = GuavaBaseEncoding.base64()
  private[this] val base64NoPadding: GuavaBaseEncoding = base64.omitPadding()
  
  def decode(data: Array[Char]): Array[Byte] = try { base64.decode(data) } catch exceptionHandler
  def decode(data: CharSequence): Array[Byte] = try { base64.decode(data) } catch exceptionHandler
  
  def encode(bytes: Array[Byte]): String = base64.encode(bytes)
  def encode(bytes: Array[Byte], offset: Int, length: Int): String = base64.encode(bytes, offset, length)
  
  def encodeNoPadding(bytes: Array[Byte]): String = base64NoPadding.encode(bytes)
  def encodeNoPadding(bytes: Array[Byte], offset: Int, length: Int): String = base64NoPadding.encode(bytes, offset, length)
  
  final class InputStream(is: java.io.InputStream) extends FilterInputStream(java.util.Base64.getDecoder().wrap(is))
  final class OutputStream(os: java.io.OutputStream) extends FilterOutputStream(java.util.Base64.getEncoder().wrap(os))
}

object Base64URL extends BaseEncoding {
  private[this] val base64Url: GuavaBaseEncoding = GuavaBaseEncoding.base64Url()
  private[this] val base64UrlNoPadding: GuavaBaseEncoding = base64Url.omitPadding()
  
  def decode(data: Array[Char]): Array[Byte] = try { base64Url.decode(data) } catch exceptionHandler
  def decode(data: CharSequence): Array[Byte] = try { base64Url.decode(data) } catch exceptionHandler
  
  def encode(bytes: Array[Byte]): String = base64Url.encode(bytes)
  def encode(bytes: Array[Byte], offset: Int, length: Int): String = base64Url.encode(bytes, offset, length)
  
  def encodeNoPadding(bytes: Array[Byte]): String = base64UrlNoPadding.encode(bytes)
  def encodeNoPadding(bytes: Array[Byte], offset: Int, length: Int): String = base64UrlNoPadding.encode(bytes, offset, length)
  
  final class InputStream(is: java.io.InputStream) extends FilterInputStream(java.util.Base64.getUrlDecoder().wrap(is))
  final class OutputStream(os: java.io.OutputStream) extends FilterOutputStream(java.util.Base64.getUrlEncoder().wrap(os))
}

object BaseEncoding {
  final class DecodingException(msg: String, cause: Throwable, stackTrace: Array[StackTraceElement]) extends IOException(msg, cause) {
    override def fillInStackTrace: Throwable = {
      setStackTrace(stackTrace)
      this
    }
  }

  // The Guava Exceptions are wrapped in an IllegalArgument Exception:
  //   https://google.github.io/guava/releases/16.0/api/docs/com/google/common/io/BaseEncoding.html#decode(java.lang.CharSequence)
  private val exceptionHandler: PartialFunction[Throwable,Nothing] = {
    case ex: Exception =>
      ex.getCause match {
        case cause: GuavaBaseEncoding.DecodingException => throw new DecodingException(cause.getMessage, cause.getCause, cause.getStackTrace)
        case other => throw ex
      }
  }
}

trait BaseEncoding {
  final protected def exceptionHandler: PartialFunction[Throwable,Nothing] = BaseEncoding.exceptionHandler
  
  def decode(data: Array[Char]): Array[Byte]
  def decode(data: CharSequence): Array[Byte]
  
  final def tryDecode(data: Array[Char]): Option[Array[Byte]] = try { Option(decode(data)) } catch { case ex: Exception => None }
  final def tryDecode(data: CharSequence): Option[Array[Byte]] = try { Option(decode(data)) } catch { case ex: Exception => None }

  def encode(bytes: Array[Byte]): String
  def encode(bytes: Array[Byte], offset: Int, length: Int): String
  
  protected def isLower(data: Array[Char]): Boolean = {
    var i: Int = 0
    while (i < data.length) {
      val ch: Char = data(i)
      if (Character.isDigit(ch)) { /* Do nothing */ }
      else if (Character.isUpperCase(ch)) return false
      else if (Character.isLowerCase(ch)) return true
      i += 1
    }
    false
  }
  
  protected def isLower(data: CharSequence): Boolean = {
    var i: Int = 0
    while (i < data.length) {
      val ch: Char = data.charAt(i)
      if (Character.isDigit(ch)) { /* Do nothing */ }
      else if (Character.isUpperCase(ch)) return false
      else if (Character.isLowerCase(ch)) return true
      i += 1
    }
    false
  }
}
