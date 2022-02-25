package fm.common

trait PlatformCompat {
  // Scala.js is missing some methods on InputStream
  implicit class InputStreamCompat(self: java.io.InputStream) {
    def toByteArray(): Array[Byte] = self.readAllBytes()
  }
}
