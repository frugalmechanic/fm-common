/*
 * Copyright 2020 Frugal Mechanic (http://frugalmechanic.com)
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

import fm.common.TaskRunnerBase.{ClearingBlockRunnableWithPriority, ClearingBlockRunnableWithResultAndPriority, RunnableWithPriority}
import java.util.Comparator
import java.util.concurrent.{PriorityBlockingQueue, RejectedExecutionException, RejectedExecutionHandler, ThreadPoolExecutor, TimeUnit}
import scala.concurrent.{Future, Promise}

object PriorityTaskRunner extends Logging {
  val defaultThreads: Int = Runtime.getRuntime().availableProcessors()
  val defaultQueueSize: Int = Int.MinValue
  val defaultCoreThreads: Int = Int.MinValue
  val defaultMaxThreads: Int = Int.MinValue

  def apply(
    name: String,
    threads: Int = defaultThreads,
    queueSize: Int = defaultQueueSize,
    coreThreads: Int = defaultCoreThreads,
    maxThreads: Int = defaultMaxThreads
  ): PriorityTaskRunner = {
    val _coreThreads: Int = if (coreThreads == Int.MinValue) threads else coreThreads
    val _maxThreads:  Int = if (maxThreads == Int.MinValue) threads else maxThreads
    val _queueSize:   Int = if (queueSize == Int.MinValue) threads * 2 else queueSize
    new PriorityTaskRunner(name, _coreThreads, _maxThreads, _queueSize)
  }

  private object RunnableWithPriorityComparator extends Comparator[Runnable] {
    override def compare(a: Runnable, b: Runnable): Int = {
      java.lang.Long.compare(a.asInstanceOf[RunnableWithPriority].priority, b.asInstanceOf[RunnableWithPriority].priority)
    }
  }

  private class BoundedPriorityQueue[E](queueSize: Int, comparator: Comparator[E]) extends PriorityBlockingQueue[E](queueSize, comparator) {
    // Ideally the "ReentrantLock lock" from PriorityBlockingQueue would be used here but I don't have easy access to it
    override def add(e: E): Boolean = synchronized {
      if (size() >= queueSize) throw new IllegalStateException("Queue is Full")
      else super.add(e)
    }

    // Ideally the "ReentrantLock lock" from PriorityBlockingQueue would be used here but I don't have easy access to it
    override def offer(e: E): Boolean = synchronized {
      if (size() >= queueSize) false
      else super.offer(e)
    }

    // Note: The BlockingQueue Interface defines this as a blocking method (i.e. wait until space is available) but
    //       we are not supporting that since method should not be called by the ThreadPoolExecutor.
    override def put(e: E): Unit = {
      throw new NotImplementedError("BoundedPriorityQueue.put is not implemented")
    }
  }
}

/**
 * Similar to a TaskRunner but allows you to pass in a priority value such that lower priority tasks will execute before
 * higher priority tasks.
 */
final class PriorityTaskRunner(val name: String, val coreThreads: Int, val maxThreads: Int, val queueSize: Int) extends TaskRunnerBase(name) {
  import PriorityTaskRunner.{BoundedPriorityQueue, RunnableWithPriorityComparator}

  private[this] val queue: BoundedPriorityQueue[Runnable] = {
    // Any items without a priority will default to Long.MaxValue (i.e. the lowest priority)
    new BoundedPriorityQueue[Runnable](queueSize, RunnableWithPriorityComparator)
  }

  private class StandardRejectExecutionHandler() extends RejectedExecutionHandler {
    def rejectedExecution(r: Runnable, executor: ThreadPoolExecutor): Unit = {
      // If the executor is shutting down then display a warning and drop the task
      if(executor.isShutdown) {
        shutdownWarning
        return
      }

      throw new RejectedExecutionException(s"$name - Queue is full")
    }
  }

  protected val executor: ThreadPoolExecutor = {
    val rejectedHandler: RejectedExecutionHandler = new StandardRejectExecutionHandler()
    val exec: ThreadPoolExecutor = new ThreadPoolExecutor(coreThreads, maxThreads, 60, TimeUnit.SECONDS, queue, TaskRunnerBase.newTaskRunnerThreadFactory(name), rejectedHandler)
    exec.allowCoreThreadTimeOut(true)
    exec
  }

  /**
   * Attempt to submit this job to the queue.  Returns true if successful or false if the queue is full
   */
  final def tryExecute(priority: Long)(f: => Unit): Boolean = try {
    execute(priority)(f)
    true
  } catch {
    case _: RejectedExecutionException => false
  }

  final def execute(priority: Long)(f: => Unit): Unit = {
    executor.execute(new ClearingBlockRunnableWithPriority(f, priority))
  }

  /**
   * Attempt to submit this job to the queue.  Returns Some(...) if successful or None if the queue is full
   */
  final def trySubmit[T](priority: Long)(f: => T): Option[Future[T]] = try {
    Some(submit(priority)(f))
  } catch {
    case _: RejectedExecutionException => None
  }

  final def submit[T](priority: Long)(f: => T): Future[T] = {
    val promise = Promise[T]()
    // Note: We use executor.execute instead of executor.submit since we use our own Future/Promise
    executor.execute(new ClearingBlockRunnableWithResultAndPriority(f, promise, priority))
    promise.future
  }
}
