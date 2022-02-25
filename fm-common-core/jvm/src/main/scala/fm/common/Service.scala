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

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._
import scala.reflect.{classTag, ClassTag}

object Service {
  private val defaultMaxRetries: Int = 12
  private val defaultExceptionHandler: PartialFunction[Exception,Unit] = null
  private val defaultDelayBetweenCalls: FiniteDuration = Duration.Zero
  private val defaultBackOffStrategy: BackOffStrategy = BackOffStrategy.exponentialForRemote()
  
  object LoggingOption {
    implicit def toLoggingOption(logger: Logger): LoggingOption = UseLogger(logger)
    implicit def toSLF4JLoggingOption(logger: org.slf4j.Logger): LoggingOption = SLF4JLogger(logger)
  }
  
  sealed trait LoggingOption {
    def calling(msg: String): Unit
    def retrying(msg: String): Unit
    def done(msg: String, totalTimeMillis: Long): Unit
    def exception(ex: Throwable): Unit
  }
  
  case object UseStdOut extends LoggingOption {
    def calling(msg: String): Unit = print(msg+"... ")
    def retrying(msg: String): Unit = print("Retrying: "+msg+"... ")
    def done(msg: String, totalTimeMillis: Long): Unit = println(" Done ("+totalTimeMillis+"ms)")
    def exception(ex: Throwable): Unit = println("Caught unhandled exception: "+ex)
  }
  
  object UseLogger {
    def apply(name: String): UseLogger = UseLogger(Logger.getLogger(name))
    def apply(ref: AnyRef): UseLogger = UseLogger(Logger.getLogger(ref.getClass))
    def apply[T](clazz: Class[T]): UseLogger = UseLogger(Logger.getLogger(clazz))
    def apply[T: ClassTag](): UseLogger = UseLogger(Logger.getLogger(classTag[T].runtimeClass))
  }
  
  final case class UseLogger(logger: Logger) extends LoggingOption {
    def calling(msg: String): Unit = logger.info(msg+"... ")
    def retrying(msg: String): Unit = logger.warn("Retrying: "+msg)
    def done(msg: String, totalTimeMillis: Long): Unit = logger.info(msg+"... Done ("+totalTimeMillis+"ms)")
    def exception(ex: Throwable): Unit = logger.warn("Caught unhandled exception: "+ex)
  }
  
  final case class SLF4JLogger(logger: org.slf4j.Logger) extends LoggingOption {
    def calling(msg: String): Unit = logger.info(msg+"... ")
    def retrying(msg: String): Unit = logger.warn("Retrying: "+msg)
    def done(msg: String, totalTimeMillis: Long): Unit = logger.info(msg+"... Done ("+totalTimeMillis+"ms)")
    def exception(ex: Throwable): Unit = logger.warn("Caught unhandled exception: "+ex)
  }
  
  case object NoLogging extends LoggingOption {
    def calling(msg: String): Unit = {}
    def retrying(msg: String): Unit = {}
    def done(msg: String, totalTimeMillis: Long): Unit = {}
    def exception(ex: Throwable): Unit = {}
  }
  
  object BackOffStrategy {
    def noWait: BackOffStrategy = NoWait
    def constantWait(duration: FiniteDuration): BackOffStrategy = ConstantWait(duration)
    def exponentialBackoff(duration: FiniteDuration): BackOffStrategy = ExponentialBackoff(duration)
    
    def exponentialForRemote(): BackOffStrategy = exponentialBackoff(1.second)
    def exponentialForLocal(): BackOffStrategy = exponentialBackoff(100.milliseconds)
  }
  
  sealed trait BackOffStrategy {
    def wait(tryCount: Int): Unit
    def millis(tryCount: Int): Long
  }
  
  /** Don't wait between retries */
  case object NoWait extends BackOffStrategy {
    def wait(tryCount: Int): Unit = {}
    def millis(tryCount: Int): Long = 0
  }
  
  /** Wait a constant amount of time between retries */
  final case class ConstantWait(duration: FiniteDuration) extends BackOffStrategy {
    def wait(tryCount: Int): Unit = duration.unit.sleep(duration.length)
    def millis(tryCount: Int): Long = duration.toMillis
  }
  
  /**
   * Simple form of exponential backoff that is simply (tryCount * tryCount * baseAmount)
   * 
   * For a base amount of 1 second you get:
   *   retry 1 - 1 Second
   *   retry 2 - 4 Seconds
   *   retry 3 - 9 seconds
   *   retry 4 - 16 seconds
   *   
   * For a base amount of 500 milliseconds you get:
   *   retry 1 - 500 milliseconds
   *   retry 2 - 2,000 milliseconds
   *   retry 3 - 4,500 milliseconds
   *   retry 4 - 8,000 milliseconds
   */
  final case class ExponentialBackoff(baseAmount: FiniteDuration) extends BackOffStrategy {
    require(baseAmount > Duration.Zero, "Can't have a Zero Duration for exponential backoff!")
    
    def wait(tryCount: Int): Unit = baseAmount.unit.sleep(tryCount * tryCount * baseAmount.length)
    def millis(tryCount: Int): Long = baseAmount.toMillis * tryCount * tryCount
  }
                    
  def call[X](msg: String = "Calling Service",
              logging: LoggingOption = UseStdOut,
              exceptionHandler: PartialFunction[Exception,Unit] = Service.defaultExceptionHandler, 
              delayBetweenCalls: FiniteDuration = Service.defaultDelayBetweenCalls, // This is a constant delay before making the call (e.g. for rate limiting to external services)
              backOffStrategy: BackOffStrategy = Service.defaultBackOffStrategy,
              maxRetries: Int = Service.defaultMaxRetries)(f: => X): X = {
    call0(msg, logging, exceptionHandler, delayBetweenCalls, backOffStrategy, maxRetries, f, 0, System.currentTimeMillis(), null)
  }
  
  @tailrec
  private def call0[X](
      msg: String, 
      logging: LoggingOption, 
      exceptionHandler: PartialFunction[Exception,Unit], 
      delayBetweenCalls: FiniteDuration, 
      backOffStrategy: BackOffStrategy, 
      maxRetries: Int, 
      f: => X, 
      tryCount: Int,
      startTime: Long,
      lastException: Exception): X = {
    
    if (tryCount >= maxRetries) throw new Exception(s"Service Failed after $maxRetries retries", lastException)
    
    if (delayBetweenCalls > Duration.Zero) delayBetweenCalls.unit.sleep(delayBetweenCalls.length)
  
    if (tryCount > 0) {
      backOffStrategy.wait(tryCount)
      logging.retrying(msg)
    } else {
      logging.calling(msg)
    }
    
    var caughtException: Exception = null
    
    val result: Option[X] = try {
      val ret: X = f
      logging.done(msg, System.currentTimeMillis() - startTime)
      Some(ret)
    } catch {
      case ex: Exception =>
        if (null != exceptionHandler && exceptionHandler.isDefinedAt(ex)) exceptionHandler(ex)
        else logging.exception(ex)
        
        caughtException = ex
        
        None
    }
    
    // Retry if the exception handler doesn't throw an exception
    if (result.isDefined) result.get else call0(msg, logging, exceptionHandler, delayBetweenCalls, backOffStrategy, maxRetries, f, tryCount + 1, startTime, caughtException)
  }
  
  
  def callAsync[X](
    msg: String = "Calling Async Service",
    logging: LoggingOption = UseStdOut,
    exceptionHandler: PartialFunction[Exception,Unit] = Service.defaultExceptionHandler,
    delayBetweenCalls: FiniteDuration = Service.defaultDelayBetweenCalls, // This is a constant delay before making the call (e.g. for rate limiting to external services)
    backOffStrategy: BackOffStrategy = Service.defaultBackOffStrategy,
    maxRetries: Int = Service.defaultMaxRetries
  )(f: => Future[X])(implicit executionContext: ExecutionContext = ExecutionContext.global, timer: ScheduledTaskRunner = ScheduledTaskRunner.global): Future[X] = {
    callAsync0(msg, logging, exceptionHandler, delayBetweenCalls, backOffStrategy, maxRetries, f, 0, System.currentTimeMillis(), null)
  }
  
  private def callAsync0[X](
    msg: String,
    logging: LoggingOption,
    exceptionHandler: PartialFunction[Exception,Unit],
    delayBetweenCalls: FiniteDuration,
    backOffStrategy: BackOffStrategy,
    maxRetries: Int,
    f: => Future[X],
    tryCount: Int,
    startTime: Long,
    lastException: Exception
  )(implicit executionContext: ExecutionContext, timer: ScheduledTaskRunner): Future[X] = {
    
    if (tryCount >= maxRetries) return Future.failed(new Exception(s"Service Failed after $maxRetries retries", lastException))
    
    val sleepMillis: Long = if (tryCount > 0) {
      logging.retrying(msg)
      delayBetweenCalls.toMillis + backOffStrategy.millis(tryCount)
    } else {
      logging.calling(msg)
      delayBetweenCalls.toMillis
    }

    val res: Future[X] = if (sleepMillis > 0) {
      val p: Promise[X] = Promise()
      timer.schedule(sleepMillis.milliseconds) { p.completeWith(f) }
      p.future
    } else {
      f
    }

    res.foreach { (_: X) =>
      // If the Future is successful then log the total time it took
      logging.done(msg, System.currentTimeMillis() - startTime)
    }
    
    res.recoverWith {
      case ex: Exception =>
        if (null != exceptionHandler && exceptionHandler.isDefinedAt(ex)) exceptionHandler(ex)
        else logging.exception(ex)
        
        callAsync0(msg, logging, exceptionHandler, delayBetweenCalls, backOffStrategy, maxRetries, f, tryCount + 1, startTime, ex)
    }
  }
}