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

import java.io._
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{Files, StandardCopyOption}

object FileUtil extends Logging {
  def md5(f: File)    : Array[Byte] = Resource.using(new FileInputStream(f)){ DigestUtils.md5(_) }
  def md5Hex(f: File) : String      = Resource.using(new FileInputStream(f)){ DigestUtils.md5Hex(_) }
  def sha1(f: File)   : Array[Byte] = Resource.using(new FileInputStream(f)){ DigestUtils.sha1(_) }
  def sha1Hex(f: File): String      = Resource.using(new FileInputStream(f)){ DigestUtils.sha1Hex(_) }

  def detectCharset(f: File): Option[Charset] = InputStreamResource.forFileOrResource(f).detectCharset()
  def detectCharsetName(f: File): Option[String] = InputStreamResource.forFileOrResource(f).detectCharsetName()

  def getDirectoryForFile(f: File): File = {
    val tmp: File = if (f.isDirectory) f else f.getParentFile
    assert(tmp.isDirectory)
    tmp
  }

  def writeRawFile(f: File, overwrite: Boolean)(fun: OutputStream => Unit): Unit = {
    FileOutputStreamResource(f, overwrite = overwrite, autoCompress = false).use(fun)
  }
  
  def writeRawFile(f: File, is: InputStream, overwrite: Boolean): Unit = writeRawFile(f, overwrite){ os: OutputStream =>
    IOUtils.copy(is, os)
  }
  
  def writeFile[T](f: File, overwrite: Boolean)(fun: OutputStream => T): T = {
    FileOutputStreamResource(f, overwrite = overwrite).use(fun)
  }

  def writeFile(f: File, contents: String, overwrite: Boolean): Unit = {
    writeFile(f, contents.getBytes(UTF_8), overwrite)
  }
  
  def writeFile(f: File, bytes: Array[Byte], overwrite: Boolean): Unit = {
    writeFile(f, overwrite) { os: OutputStream =>
      os.write(bytes)
    }
  }
  
  def writeFile(f: File, is: InputStream, overwrite: Boolean): Unit = writeFile(f, overwrite){ os: OutputStream =>
    IOUtils.copy(is, os)
  }
  
  /**
   * Creates a tmp file that can be written to and then will be atomically renamed to the target
   */
  def writeFileWithTemp[T](target: File)(f: File => T): T = {
    val tmp: File = File.createTempFile(".fm_tmp", target.getName, getDirectoryForFile(target))
    
    try {
      val res: T = f(tmp)
      Files.move(tmp.toPath, target.toPath, StandardCopyOption.ATOMIC_MOVE)
      res
    } catch {
      case ex: Throwable =>
        if (tmp.isFile) tmp.delete()
        throw ex
    }
  }

  /**
   * Creates a temp file, passes it to the function, and then deletes the temp file
   */
  def withTempFile[T](prefix: String, suffix: String)(f: File => T): T = {
    useFileThenDelete(File.createTempFile(prefix, suffix))(f)
  }

  /**
   * Creates a temp file, passes it to the function, and then deletes the temp file
   */
  def withTempFile[T](prefix: String, suffix: String, directory: File)(f: File => T): T = {
    useFileThenDelete(File.createTempFile(prefix, suffix, directory))(f)
  }

  /**
   * Passes the file into the passed in function and then delete the file
   */
  def useFileThenDelete[T](file: File)(f: File => T): T = {
    try {
      f(file)
    } finally {
      if (file.isFile) file.delete()
    }
  }
  
  def copy(src: File, dst: File, overwrite: Boolean = true): Unit = {
    def bothEndWith(s: String): Boolean = src.getName.endsWith(s) && dst.getName.endsWith(s)
    
    // Only enable autoDecompress/autoCompress if the compression formats don't already match 
    val compression: Boolean = if (bothEndWith(".gz") || bothEndWith(".zip") || bothEndWith(".snappy")) false else true
    
    InputStreamResource.forFile(src, autoDecompress = compression).use { is =>
      FileOutputStreamResource(dst, autoCompress = compression).use { os =>
        IOUtils.copy(is, os)
      }
    }
  }
  
  def fileExists(file: String): Boolean = fileExists(new File(file))
  def fileExists(file: File): Boolean = file.isFile
  
  def resourceExists(file: String): Boolean = ClassUtil.classpathFileExists(file)
  def resourceExists(file: File): Boolean = ClassUtil.classpathFileExists(file)
  
  def fileOrResourceExists(file: String): Boolean = fileExists(file) || resourceExists(file)
  def fileOrResourceExists(file: File): Boolean = fileExists(file) || resourceExists(file)
  
  def readFile(file: String): String = readFile(file, UTF_8)
  def readFile(file: String, encoding: String): String = readFile(new File(file), encoding)
  def readFile(file: String, encoding: Charset): String = readFile(new File(file), encoding)

  def readFile(f: File): String = readFile(f, UTF_8)
  def readFile(f: File, encoding: String): String = InputStreamResource.forFile(f).readToString(encoding)
  def readFile(f: File, encoding: Charset): String = InputStreamResource.forFile(f).readToString(encoding)

  def readResource(file: String): String = readResource(file, UTF_8)
  def readResource(file: String, encoding: String): String = readResource(new File(file), encoding)
  def readResource(file: String, encoding: Charset): String = readResource(new File(file), encoding)

  def readResource(f: File): String = readResource(f, UTF_8)
  def readResource(f: File, encoding: String): String = InputStreamResource.forResource(f).readToString(encoding)
  def readResource(f: File, encoding: Charset): String = InputStreamResource.forResource(f).readToString(encoding)

  def readFileOrResource(file: String): String = readFileOrResource(file, UTF_8)
  def readFileOrResource(file: String, encoding: String): String = readFileOrResource(new File(file), encoding)
  def readFileOrResource(file: String, encoding: Charset): String = readFileOrResource(new File(file), encoding)

  def readFileOrResource(f: File): String = readFileOrResource(f, UTF_8)

  def readFileOrResource(f: File, encoding: String): String = {
    InputStreamResource.forFileOrResource(f).readToString(encoding)
  }

  def readFileOrResource(f: File, encoding: Charset): String = {
    InputStreamResource.forFileOrResource(f).readToString(encoding)
  }
  
  def readLines(file: File)(f: String => Unit): Unit = {
    readLines(InputStreamResource.forFile(file).bufferedReader())(f)
  }
  
  def readLines(is: InputStream)(f: String => Unit): Unit = {
    readLines(InputStreamResource.forInputStream(is).bufferedReader())(f)
  }
  
  def readLines(resource: Resource[BufferedReader])(f: String => Unit): Unit = resource.use{ reader: BufferedReader =>
    var line: String = reader.readLine
    while (null != line) {
      f(line)
      line = reader.readLine
    }
  }

  def readBytes(file: String): Array[Byte] = readBytes(new File(file))

  def readBytes(f: File): Array[Byte] = InputStreamResource.forFile(f).readBytes()

  def readInputStream(is: InputStream): String = readInputStream(is, UTF_8)
  
  def readInputStream(is: InputStream, encoding: String): String = readInputStream(is, CharsetUtil.forName(encoding))
  
  def readInputStream(is: InputStream, charset: Charset): String = {
    val reader: BufferedReader = new BufferedReader(new InputStreamReader(is, charset))
    val writer: StringWriter = new StringWriter()

    IOUtils.copy(reader, writer)
    reader.close()
    writer.toString
  }

  def rm_rf(dir: File, keepDirectory: Boolean = false): Boolean = {
    logger.warn("rm -rf " + dir.getAbsolutePath)
    if (dir.isDirectory) {
      val children: Array[String] = dir.list
      children.foreach { child =>
        if (!rm_rf(new File(dir, child))) return false
      }

      if (!keepDirectory) dir.delete() else true
    } else {
      val success: Boolean = dir.delete()
      if (!success) logger.warn(s"File deletion for ${dir.getAbsolutePath} failed")
      success
    }
  }

}

