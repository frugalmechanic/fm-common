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

import java.io.{File, FileOutputStream, OutputStream}
import java.nio.file.{Files, StandardCopyOption}
import java.util.zip.Deflater

object FileOutputStreamResource {
  def apply(
    file: File,
    fileName: String = "",
    overwrite: Boolean = true,
    append: Boolean = false,
    useTmpFile: Boolean = true,
    autoCompress: Boolean = true,
    compressionLevel: Int = Deflater.BEST_SPEED,
    buffered: Boolean = true,
    internalArchiveFileName: Option[String] = None
  ): OutputStreamResource = {
    val finalFileName: String = fileName.toBlankOption.getOrElse{ file.getName }

    val resource: Resource[OutputStream] = new FileOutputStreamResource(
      file = file,
      overwrite = overwrite,
      append = append,
      useTmpFile = useTmpFile
    )
    
    OutputStreamResource(
      resource = resource,
      fileName = finalFileName,
      autoCompress = autoCompress,
      compressionLevel = compressionLevel,
      buffered = buffered,
      internalArchiveFileName = internalArchiveFileName
    )
  }
}

// Most of the logic in here was refactored into the OutputStreamResource class.  This still exists to handle the actual writing to the file and tmp file renaming stuff.
// It's not meant to be used directly.
final private class FileOutputStreamResource private (
  file: File,
  overwrite: Boolean = true,
  append: Boolean = false,
  useTmpFile: Boolean = true
) extends Resource[OutputStream] with Logging {
  if (overwrite) require(!append, "You've specified both append and overwrite!")
  
  def isUsable: Boolean = true
  def isMultiUse: Boolean = true
  
  def use[T](f: OutputStream => T): T = {   
    require(!file.exists || (file.exists && (overwrite || append)), s"File (${file.getAbsolutePath()}) already exists and overwrite is false: "+f)
    
    val usingTmpFile: Boolean = useTmpFile && !(append && file.exists)
    
    logger.debug(s"Writing to file: $file  Overwrite: $overwrite  Append: $append  useTmpFile: $useTmpFile   usingTmpFile: $usingTmpFile")
    
    val outFile: File = if (usingTmpFile) {
      val tmp = File.createTempFile(".fm_tmp", file.getName, getDirectoryForFile(file))
      // DO NOT USE File.deleteOnExit() since it uses an append-only LinkedHashSet
      //tmp.deleteOnExit()
      tmp
    } else file
    
    val res: T = try {
      SingleUseResource(new FileOutputStream(outFile, append)).use { f }
    } catch {
      case ex: Throwable =>
        logger.error(s"Caught Exception Writing File: $file")
        if (usingTmpFile) outFile.delete()
        throw ex
    }
    
    // Do an atomic move to the final location
    if (usingTmpFile) {
      try {
        Files.move(outFile.toPath, file.toPath, StandardCopyOption.ATOMIC_MOVE)
      } catch {
        case ex: Throwable =>
          logger.error(s"Caught Exception performing atomic move of $outFile to $file")
          outFile.delete()
          throw ex
      }
    }
    
    res
  }
  
  private def getDirectoryForFile(f: File): File = {
    val tmp = if(f.isDirectory) f else f.getParentFile
    assert(tmp.isDirectory, s"$tmp must be a directory")
    tmp
  }
}

