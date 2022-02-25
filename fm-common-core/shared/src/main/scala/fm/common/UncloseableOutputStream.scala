package fm.common

import java.io.{FilterOutputStream, OutputStream}

object UncloseableOutputStream {
  def apply(in: OutputStream): UncloseableOutputStream = new UncloseableOutputStream(in)
}

/**
 * Wraps an OutputStream and makes the close() method do nothing
 */
final class UncloseableOutputStream(in: OutputStream) extends FilterOutputStream(in) {
  override def close(): Unit = { }
}