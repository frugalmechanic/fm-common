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

import java.io.{File, OutputStream, PrintStream}
import ch.qos.logback.classic.{Level => LogbackLevel, Logger => LogbackLogger}
import org.apache.commons.io.output.TeeOutputStream
import org.slf4j.Logger.ROOT_LOGGER_NAME

/**
 * This can be extended for basic logging functionality
 */
trait Logging {
  @transient protected lazy val logger: Logger = Logger.getLogger(this)
}

// TODO: move this?
final case class LoggingCaptureConfig(logger: org.slf4j.Logger, pattern: String, file: File, overwrite: Boolean)

/**
* This has SLF4J/Logback Helpers that depend on SLF4j/Logback
*
* TODO: Clean this up!!
*/
object Logging {

 // Some helpers to set the logging level at runtime
 def setLevelToTrace(logger: AnyRef): Unit = setLevel(logger, LogbackLevel.TRACE)
 def setLevelToDebug(logger: AnyRef): Unit = setLevel(logger, LogbackLevel.DEBUG)
 def setLevelToInfo(logger: AnyRef):  Unit = setLevel(logger, LogbackLevel.INFO)
 def setLevelToWarn(logger: AnyRef):  Unit = setLevel(logger, LogbackLevel.WARN)
 def setLevelToError(logger: AnyRef): Unit = setLevel(logger, LogbackLevel.ERROR)
 def setLevelToOff(logger: AnyRef): Unit = setLevel(logger, LogbackLevel.OFF)

 // Set the ROOT logger
 def setRootLevelToTrace(): Unit = setRootLevel(LogbackLevel.TRACE)
 def setRootLevelToDebug(): Unit = setRootLevel(LogbackLevel.DEBUG)
 def setRootLevelToInfo():  Unit = setRootLevel(LogbackLevel.INFO)
 def setRootLevelToWarn():  Unit = setRootLevel(LogbackLevel.WARN)
 def setRootLevelToError(): Unit = setRootLevel(LogbackLevel.ERROR)
 def setRootLevelToOff(): Unit = setRootLevel(LogbackLevel.OFF)

 // Set the ROOT logger
 def setRootLevel(level: LogbackLevel): Unit = setLevel(ROOT_LOGGER_NAME, level)

 // Private to avoid exposing LogbackLevel which causes ProGuard issues
 private def setLevel(logger: AnyRef, level: LogbackLevel): Unit = setLevel(Logger.SLF4JLogger(logger).underlying, level)

 // Private to avoid exposing org.slf4j.Logger which causes ProGuard issues
 private def setLevel(logger: org.slf4j.Logger, level: LogbackLevel): Unit = logger match { case logback: LogbackLogger => logback.setLevel(level) }

 /**
  * Capture logging to a file
  *
  * Note: Private to avoid exposing org.slf4j.Logger which causes ProGuard issues
  */
 private def capture[T](
   logger: org.slf4j.Logger,
   pattern: String, // = """%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n""",
   file: File,
   overwrite: Boolean
 )(fun: => T): T = {
   FileUtil.writeFile(file, overwrite){ os => capture(logger.asInstanceOf[ch.qos.logback.classic.Logger], pattern, os)(fun) }
 }

 /**
  * Capture logging based on a LoggingCaptureConfig
  * Note: Private to avoid exposing org.slf4j.Logger (via LoggingCaptureConfig) which causes ProGuard issues
  */
 private def capture[T](configs: LoggingCaptureConfig*)(fun: => T): T = {
   if (configs.isEmpty) return fun

   val head: LoggingCaptureConfig = configs.head
   capture(head.logger, head.pattern, head.file, head.overwrite) {
     capture(configs.tail:_*)(fun)
   }
 }


 /**
  * Capture logging to an output stream
  *
  * Note: Private to avoid exposing ch.qos.logback.classic.Logger which causes ProGuard issues
  */
 private def capture[T](logger: ch.qos.logback.classic.Logger, pattern: String, os: OutputStream)(fun: => T): T = {
   import ch.qos.logback.classic.LoggerContext

   val ctx = org.slf4j.LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
   val encoder = new ch.qos.logback.classic.encoder.PatternLayoutEncoder
   encoder.setContext(ctx)
   encoder.setPattern(pattern)
   encoder.start

   val osAppender = new ch.qos.logback.core.OutputStreamAppender[ch.qos.logback.classic.spi.ILoggingEvent]
   osAppender.setContext(ctx)
   osAppender.setLayout(encoder.getLayout)
   osAppender.setEncoder(encoder)
   osAppender.setOutputStream(os)
   osAppender.start()

   try {
     withAppender(logger.asInstanceOf[ch.qos.logback.classic.Logger], osAppender)(fun)
   } finally {
     osAppender.stop()
     encoder.stop()
   }
 }

 /**
  * Capture Standard Out and Standard Error to a file
  */
 def captureStdOutStdErr[T](file: File, overwrite: Boolean, append: Boolean = false, useTmpFile: Boolean = true)(fun: => T): T = {
   FileOutputStreamResource(file, overwrite = overwrite, append = append, useTmpFile = useTmpFile).use{ os => captureStdOutStdErr(os)(fun) }
 }

 /**
  * Capture Standard Out and Standard Error to an Output Stream
  */
 def captureStdOutStdErr[T](os: OutputStream)(f: => T): T = {
   captureScalaStdOutStdErr(os){ captureJavaStdOutStdErr(os)(f) }
 }

 /**
  * Capture Java's Standard Out and Standard Error to an Output Stream
  */
 private def captureJavaStdOutStdErr[T](os: OutputStream)(f: => T): T = {
   val out: PrintStream = System.out
   val err: PrintStream = System.err

   try {
     System.setOut(new PrintStream(new TeeOutputStream(out, os), true, "UTF-8"))
     System.setErr(new PrintStream(new TeeOutputStream(err, os), true, "UTF-8"))

     f
   } finally {
     System.setOut(out)
     System.setErr(err)
   }
 }

 /**
  * Capture Scala's Standard Out and Standard Error (Console.out and Console.err) to an Output Stream
  */
 private def captureScalaStdOutStdErr[T](os: OutputStream)(f: => T): T = {
   Console.withOut(new PrintStream(new TeeOutputStream(Console.out, os), true, "UTF-8")) {
     Console.withErr(new PrintStream(new TeeOutputStream(Console.err, os), true, "UTF-8")) {
       f
     }
   }
 }

 private def withAppender[T](logger: ch.qos.logback.classic.Logger, appender: ch.qos.logback.core.Appender[ch.qos.logback.classic.spi.ILoggingEvent])(fun: => T): T = {
   try {
     logger.addAppender(appender)
     fun
   } finally {
     logger.detachAppender(appender)
   }
 }
}