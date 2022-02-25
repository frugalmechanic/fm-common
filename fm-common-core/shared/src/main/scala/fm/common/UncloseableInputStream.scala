package fm.common

import java.io.{FilterInputStream, InputStream}

object UncloseableInputStream {
  def apply(in: InputStream): UncloseableInputStream = new UncloseableInputStream(in)
}

/**
 * Wraps an InputStream and makes the close() method do nothing
 */
final class UncloseableInputStream(in: InputStream) extends FilterInputStream(in) {
  override def close(): Unit = { }
}