package fm.common

import java.io.{InputStream, InputStreamReader, Reader}
import java.nio.charset.Charset
import org.apache.commons.io.ByteOrderMark
import org.apache.commons.io.input.{ProxyInputStream, BOMInputStream => ApacheBOMInputStream}

object BOMInputStreamReader {
  def apply(is: InputStream): InputStreamReader = {
    val bis: BOMInputStream = BOMInputStream(is)

    val charset: String = if (bis.hasBOM()) bis.getBOMCharsetName else "UTF-8"
    new InputStreamReader(bis, charset)
  }
}

/**
  * This Apache BOMInputStream only defaults to the UTF_8 BOM for detection
  *
  * This implements the boiler plate to detect all of the common BOM formats and strip them from the InputStream
  */
object BOMInputStream {
  // Not using this any more since it doesn't play nicely with Proguard (the ScalaSig references org.apache.commons.io.ByteOrderMark which breaks on 2.10.x)
  // Note - This cannot be an array since the BOMInputStream class modifies it
  //private val BOMs: Vector[ByteOrderMark] = Vector(ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE)

  private val BOMCharsets: Set[Charset] = {
    // Keep this in sync with the BOMInputStream apply
    val BOMs: Vector[ByteOrderMark] = Vector(ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE)

    // Avoiding closures so no methods have ByteOrderMark in their signature (so Proguard doesn't complain)
    val set = Set.newBuilder[Charset]
    var i = 0

    while (i < BOMs.length) {
      set += Charset.forName(BOMs(i).getCharsetName)
      i += 1
    }

    set.result
  }

  def apply(is: InputStream): BOMInputStream = {
    BOMInputStream(new ApacheBOMInputStream(is, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE))
  }

  def get(is: InputStream, charset: Charset): Option[BOMInputStream] = if (BOMCharsets.contains(charset)) Some(apply(is)) else None
}

final class BOMInputStream private (bis: ApacheBOMInputStream) extends ProxyInputStream(bis) {
  def reader(): Reader = {
    val charset: String = if (bis.hasBOM()) bis.getBOMCharsetName else "UTF-8"
    new InputStreamReader(bis, charset)
  }

  def hasBOM(): Boolean = bis.hasBOM()
  def hasBOM(mark: ByteOrderMark): Boolean = bis.hasBOM(mark)
  def getBOMCharsetName: String = bis.getBOMCharsetName
}

