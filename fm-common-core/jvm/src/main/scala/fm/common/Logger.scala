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

object Logger {
  private[this] val hasSLF4J: Boolean = ClassUtil.classExists("org.slf4j.Logger")

  def getLogger(obj: AnyRef): Logger = if (hasSLF4J) SLF4JLogger(obj) else NoLogger

  object NoLogger extends Logger {
    def isTraceEnabled: Boolean = false
    def isDebugEnabled: Boolean = false
    def isInfoEnabled : Boolean = false
    def isWarnEnabled : Boolean = false
    def isErrorEnabled: Boolean = false

    def trace(msg: => String): Unit = {}
    def debug(msg: => String): Unit = {}
    def info(msg: => String) : Unit = {}
    def warn(msg: => String) : Unit = {}
    def error(msg: => String): Unit = {}

    def trace(ex: Throwable): Unit = {}
    def debug(ex: Throwable): Unit = {}
    def info(ex: Throwable) : Unit = {}
    def warn(ex: Throwable) : Unit = {}
    def error(ex: Throwable): Unit = {}

    def trace(msg: => String, ex: Throwable): Unit = {}
    def debug(msg: => String, ex: Throwable): Unit = {}
    def info(msg: => String, ex: Throwable) : Unit = {}
    def warn(msg: => String, ex: Throwable) : Unit = {}
    def error(msg: => String, ex: Throwable): Unit = {}
  }

  object SLF4JLogger {
    import org.slf4j.LoggerFactory

    def apply(obj: AnyRef): SLF4JLogger = new SLF4JLogger(getLoggerImpl(obj))

    private def getLoggerImpl(obj: AnyRef) = obj match {
      case s: String   => LoggerFactory.getLogger(s)
      case c: Class[_] => LoggerFactory.getLogger(loggerNameForClass(c.getName))
      case _           => LoggerFactory.getLogger(loggerNameForClass(obj.getClass.getName))
    }

    private def loggerNameForClass(className: String): String = {
      if (className endsWith "$") className.substring(0, className.length - 1)  else className
    }
  }

  final class SLF4JLogger(self: org.slf4j.Logger) extends Logger {
    def underlying: org.slf4j.Logger = self

    @inline def isTraceEnabled: Boolean = self.isTraceEnabled()
    @inline def isDebugEnabled: Boolean = self.isDebugEnabled()
    @inline def isInfoEnabled : Boolean = self.isInfoEnabled()
    @inline def isWarnEnabled : Boolean = self.isWarnEnabled()
    @inline def isErrorEnabled: Boolean = self.isErrorEnabled()

    def trace(msg: => String): Unit = if (isTraceEnabled) self.trace(msg)
    def debug(msg: => String): Unit = if (isDebugEnabled) self.debug(msg)
    def info(msg: => String) : Unit = if (isInfoEnabled) self.info(msg)
    def warn(msg: => String) : Unit = if (isWarnEnabled) self.warn(msg)
    def error(msg: => String): Unit = if (isErrorEnabled) self.error(msg)

    def trace(ex: Throwable): Unit = self.trace("Caught Exception", ex)
    def debug(ex: Throwable): Unit = self.debug("Caught Exception", ex)
    def info(ex: Throwable) : Unit = self.info("Caught Exception", ex)
    def warn(ex: Throwable) : Unit = self.warn("Caught Exception", ex)
    def error(ex: Throwable): Unit = self.error("Caught Exception", ex)

    def trace(msg: => String, ex: Throwable): Unit = if (isTraceEnabled) self.trace(msg, ex)
    def debug(msg: => String, ex: Throwable): Unit = if (isDebugEnabled) self.debug(msg, ex)
    def info(msg: => String, ex: Throwable) : Unit = if (isInfoEnabled) self.info(msg, ex)
    def warn(msg: => String, ex: Throwable) : Unit = if (isWarnEnabled) self.warn(msg, ex)
    def error(msg: => String, ex: Throwable): Unit = if (isErrorEnabled) self.error(msg, ex)
  }
}

trait Logger {
  def isTraceEnabled: Boolean
  def isDebugEnabled: Boolean
  def isInfoEnabled : Boolean
  def isWarnEnabled : Boolean
  def isErrorEnabled: Boolean

  def trace(msg: => String): Unit
  def debug(msg: => String): Unit
  def info(msg: => String) : Unit
  def warn(msg: => String) : Unit
  def error(msg: => String): Unit

  def trace(ex: Throwable): Unit
  def debug(ex: Throwable): Unit
  def info(ex: Throwable) : Unit
  def warn(ex: Throwable) : Unit
  def error(ex: Throwable): Unit

  def trace(msg: => String, ex: Throwable): Unit
  def debug(msg: => String, ex: Throwable): Unit
  def info(msg: => String, ex: Throwable) : Unit
  def warn(msg: => String, ex: Throwable) : Unit
  def error(msg: => String, ex: Throwable): Unit
}
