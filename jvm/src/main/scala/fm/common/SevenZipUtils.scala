/*
 * Copyright 2019 Frugal Mechanic (http://frugalmechanic.com)
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

import java.io.{File, FileFilter, FileNotFoundException, IOException, InputStream}
import org.apache.commons.compress.archivers.sevenz.{SevenZArchiveEntry, SevenZFile}

object SevenZipUtils {
  /**
   * Open a 7-Zip File with support for multi-part archives (with names like file.7z.001)
   */
  def open(file: File): SevenZFile = {
    if (file.extension.exists{ _.isInt }) {
      // Check for multi-part - stripping off the numeric extension for the passed in file
      openMultiPart(findMultiPartFiles(file.getParentFile, file.nameWithoutExtension))
    } else if (file.isFile) {
      // Try opening normally
      new SevenZFile(file)
    } else {
      // Check for multi-part
      openMultiPart(findMultiPartFiles(file.getParentFile, file.getName))
    }
  }

  def openMultiPart(files: Seq[File]): SevenZFile = {
    new SevenZFile(MultiReadOnlySeekableByteChannel.forFiles(files))
  }

  private def findMultiPartFiles(dir: File, baseName: String): Seq[File] = {
    require(dir.isDirectory, s"Expected a directory: $dir")

    dir.listFiles(new FileFilter {
      override def accept(file: File): Boolean = file.isFile && file.getName.startsWith(baseName) && file.extension.exists{ _.isInt }
    }).toIndexedSeq.sortBy{ _.extension.map{ _.toInt }.get }
  }

  def inputStreamResourceForEntry(seven7File: File, entryName: String): InputStreamResource = {
    val resource: MultiUseResource[InputStream] = MultiUseResource{ inputStreamForEntry(seven7File, entryName) }
    InputStreamResource(resource = resource, fileName = entryName)
  }

  def getInputStreamResourceForEntry(seven7File: File, entryName: String): Option[InputStreamResource] = {
    if (!entryExists(seven7File, entryName)) return None

    val resource: MultiUseResource[InputStream] = MultiUseResource{ inputStreamForEntry(seven7File, entryName) }
    Some(InputStreamResource(resource = resource, fileName = entryName))
  }

  /**
   * Return an InputStream given an 7Zip file and entry name within the file to read.
   *
   * The caller is responsible for closing the returned InputStream
   */
  def inputStreamForEntry(seven7File: File, entryName: String): InputStream = {
    getInputStreamForEntry(seven7File, entryName).getOrElse{ throw new FileNotFoundException(s"Missing $seven7File or $entryName within $seven7File") }
  }

  /**
   * Return an optional InputStream given an 7Zip file and entry name within the file to read.
   *
   * The caller is responsible for closing the returned InputStream
   */
  def getInputStreamForEntry(seven7File: File, entryName: String): Option[InputStream] = {
    val file: SevenZFile = try {
      new SevenZFile(seven7File)
    } catch {
      case _: IOException => return None
    }

    try {
      var entry: SevenZArchiveEntry = file.getNextEntry()

      while (entry =!= null) {
        if (!entry.isDirectory && entry.getName === entryName) {
          // Note: File is NOT closed.  The caller is responsible for closing.
          return Some(new InputStreamWrapper(file))
        }

        entry = file.getNextEntry()
      }

      // Close the file if no matching entry
      file.close()
      None
    } catch {
      // Make sure the file gets closed on an exception
      case ex: Throwable =>
        file.close()
        throw ex
    }
  }

  def entryExists(seven7File: File, entryName: String): Boolean = {
    val file: SevenZFile = try {
      new SevenZFile(seven7File)
    } catch {
      case _: IOException => return false
    }

    try {
      var entry: SevenZArchiveEntry = file.getNextEntry()

      while (entry =!= null) {
        if (!entry.isDirectory && entry.getName === entryName) {
          return true
        }

        entry = file.getNextEntry()
      }

      false
    } finally {
      file.close()
    }
  }

  private class InputStreamWrapper(file: SevenZFile) extends InputStream {
    override def read(): Int = file.read()
    override def read(b: Array[Byte]): Int = file.read(b)
    override def read(b: Array[Byte], off: Int, len: Int): Int = file.read(b, off, len)
    override def close(): Unit = file.close()
  }
}
