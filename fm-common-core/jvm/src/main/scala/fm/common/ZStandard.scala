/*
 * Copyright 2021 Frugal Mechanic (http://frugalmechanic.com)
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

import java.io.{InputStream, OutputStream}
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

object ZStandard {
  private val HasZstd: Boolean = ClassUtil.classExists("org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream")
  private def requireZstd(): Unit = if (!HasZstd) throw new ClassNotFoundException("""ZStandard support missing.  Please include zstd-jni:  https://github.com/luben/zstd-jni   e.g.: libraryDependencies += "com.github.luben" % "zstd-jni" % "1.4.8-1"""")

  /**
   * Create a new ZstdCompressorOutputStream
   */
  def newOutputStream(os: OutputStream): OutputStream = {
    requireZstd()
    Impl.newOS(os)
  }

  /**
   * Create a new ZstdCompressorInputStream
   */
  def newInputStream(is: InputStream): InputStream = {
    requireZstd()
    Impl.newIS(is)
  }

  /**
   * If ZStandard is available then create a new ZstdCompressorOutputStream otherwise use a GZIPOutputStream
   */
  def newZStdOrGzipOutputStream(os: OutputStream): OutputStream = {
    if (HasZstd) Impl.newOS(os) else new GZIPOutputStream(os)
  }

  /**
   * If ZStandard is available then create a new ZstdCompressorInputStream otherwise use a GZIPInputStream
   */
  def newZStdOrGzipInputStream(is: InputStream): InputStream = {
    if (HasZstd) Impl.newIS(is) else new GZIPInputStream(is)
  }

  // This is a separate object to prevent NoClassDefFoundError
  private object Impl {
    import org.apache.commons.compress.compressors.zstandard.{ZstdCompressorInputStream, ZstdCompressorOutputStream}
    def newOS(os: OutputStream): OutputStream = new ZstdCompressorOutputStream(os)
    def newIS(is: InputStream): InputStream = new ZstdCompressorInputStream(is)
  }
}