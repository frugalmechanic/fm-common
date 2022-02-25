package fm.common

trait PlatformCompat {
  implicit class InputStreamCompat(self: java.io.InputStream) {
    // Scala.js is missing readAllBytes() so we have a very dumb implementation
    def toByteArray(): Array[Byte] = {
      val out: GrowableByteArray = new GrowableByteArray()

      val buf: Array[Byte] = new Array(8192) // Arbitrary buffer size (but matches java.io.InputStream.DEFAULT_BUFFER_SIZE
      var read: Int = self.read(buf)

      while (-1 != read) {
        out.write(buf, 0, read)
        read = self.read(buf)
      }

      out.toByteArray
    }
  }
}
