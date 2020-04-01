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

import java.util.concurrent.{RejectedExecutionException, ThreadFactory, ThreadPoolExecutor, TimeUnit}
import java.util.concurrent.atomic.AtomicInteger
import java.io.Closeable
import scala.concurrent.{Future, Promise}
import scala.util.control.NonFatal

object TaskRunnerBase extends Logging {

  private final class ShutdownHookThread(name: String, _runner: TaskRunnerBase) extends Thread("TaskRunner Shutdown Hook - "+name) with Logging {
    // Using a weak reference so this thread doesn't prevent the TaskRunner from being
    // GC'ed (which is useful for SBT unit testing where the JVM stays up a long time)
    private[this] val runner = new scala.ref.WeakReference(_runner)
    
    override def run: Unit = try {
      runner.get.foreach{ _.abort() }
    } catch {
      case ex: Throwable => logger.error(s"Caught Exception in TaskRunner ($name) Shutdown Hook: "+ ex)
    }
  }
  
  /**
   * Once the param f is run the reference to it is automatically cleared so that anything it
   * references can be garbage collected.
   */
  final class ClearingBlockRunnableWithResult[T](f: => T, promise: Promise[T]) extends Runnable {
    // I think it's okay that this is not marked as @volatile since we only care
    // that it eventually gets set to null so it can be garbage collected.
    private[this] var fun: () => T = () => f
    
    def run(): Unit = try {
      if (null == fun) throw new AssertionError("Callable has already been called and cannot be called again since it's reference was cleared")
      promise.success(fun())
    } catch {
      case NonFatal(ex) => promise.failure(ex)
    } finally {
      fun = null
    }
  }
  
  /**
   * Once the param f is run the reference to it is automatically cleared so that anything it
   * references can be garbage collected.
   */
  final class ClearingBlockRunnable(f: => Unit) extends Runnable {
    // I think it's okay that this is not marked as @volatile since we only care
    // that it eventually gets set to null so it can be garbage collected.
    private[this] var fun: () => Unit = () => f
    
    def run(): Unit = try {
      if (null == fun) throw new AssertionError("Runnable has already been run and cannot be run again since it's reference was cleared")
      fun()
    } catch {
      case NonFatal(ex) => handleUncaughtException(Thread.currentThread, ex)
    } finally {
      fun = null
    }
  }
  
  private def handleUncaughtException(t: Thread, e: Throwable): Nothing = {
    logger.error("Uncaught Exception in thread '"+t.getName+"'.  Exiting...", e)
    sys.exit(-1)
  }
  
  private def uncaughtExceptionHandler: Thread.UncaughtExceptionHandler = new Thread.UncaughtExceptionHandler {
    def uncaughtException(t: Thread, e: Throwable): Unit = handleUncaughtException(t, e)
  }
  
  final class TaskRunnerThreadFactory(name: String) extends ThreadFactory {
    private val threadCount: AtomicInteger = new AtomicInteger(0)
    val group: ThreadGroup = new ThreadGroup(name)
    def newThread(r: Runnable): Thread = {
      val count: Int = threadCount.incrementAndGet
      val t: Thread = new Thread(group, r, name+"-"+count)
      t.setDaemon(true) // Don't prevent JVM shutdown when main exits
      t.setUncaughtExceptionHandler(uncaughtExceptionHandler)
      t
    }
  }

  private[common] def newTaskRunnerThreadFactory(name: String): TaskRunnerThreadFactory = {
    new TaskRunnerThreadFactory(name)
  }
  
}

abstract class TaskRunnerBase(name: String) extends Closeable with Logging {
  import TaskRunnerBase.{ShutdownHookThread, ClearingBlockRunnableWithResult, ClearingBlockRunnable, TaskRunnerThreadFactory}
  
  private[this] val shutdownHookThread: Thread = new ShutdownHookThread(name, this)
  Runtime.getRuntime.addShutdownHook(shutdownHookThread)
  
  protected def executor: ThreadPoolExecutor
  
  final def scoped(f: this.type => Unit): Unit = {
    f(this)
    shutdown()
  }

  final def size: Int = executor.getQueue().size()

  protected lazy val shutdownWarning: Boolean = {
    logger.warn(s"$name - TaskRunner is shutting down, rejected task submission")
    true
  }

  /**
   * Attempt to submit this job to the queue.  Returns true if successful or false if the queue is full
   */
  final def tryExecute(f: => Unit): Boolean = try {
    execute(f)
    true
  } catch {
    case ex: RejectedExecutionException => false
  }
  
  final def execute(f: => Unit): Unit = {
    executor.execute(new ClearingBlockRunnable(f))
  }

  /**
   * Attempt to submit this job to the queue.  Returns Some(...) if successful or None if the queue is full
   */
  final def trySubmit[T](f: => T): Option[Future[T]] = try {
    Some(submit(f))
  } catch {
    case ex: RejectedExecutionException => None
  }
  
  final def submit[T](f: => T): Future[T] = {
    val promise = Promise[T]()
    executor.submit(new ClearingBlockRunnableWithResult(f, promise))
    promise.future
  }
  
  final def close(): Unit = {
    shutdown(silent = true)
  }
  
  /**
   * Perform a "clean" shutdown of the executor by waiting for all tasks to finish
   */
  final def shutdown(silent: Boolean = false, warnIntervalSeconds: Int = 30): Unit = {
    deregisterShutdownHook()
    
    if(!silent) logger.info("Shutting down TaskRunner: "+name)
    executor.shutdown()
    
    // Wait for the executor to terminate
    while(!executor.awaitTermination(warnIntervalSeconds, TimeUnit.SECONDS)) {
      if(!silent) logger.warn("Still waiting for TaskRunner to finish: "+name)
    }
  }
  
  /**
   * Perform an unclean shutdown of the executor only waiting up to maxWaitSeconds
   */
  final def abort(maxWaitSeconds: Int = 5): Unit = {
    deregisterShutdownHook()
    
    if(0 != size) logger.warn(s"Shutting down $name with $size items still in queue!")
    executor.shutdown()
    if(!executor.awaitTermination(maxWaitSeconds, TimeUnit.SECONDS)) {
      executor.shutdownNow()
    }
  }
  
  private def deregisterShutdownHook(): Unit = try {
    if(Thread.currentThread.getName != shutdownHookThread.getName) Runtime.getRuntime.removeShutdownHook(shutdownHookThread)
  } catch {
    case _: IllegalStateException =>
  }

  override def finalize: Unit = {
    abort()
  }
}
