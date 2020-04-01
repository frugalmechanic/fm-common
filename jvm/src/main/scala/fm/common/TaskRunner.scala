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

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue, LinkedBlockingQueue, SynchronousQueue}
import java.util.concurrent.{RejectedExecutionException, RejectedExecutionHandler, ThreadPoolExecutor, TimeUnit}

object TaskRunner extends Logging {
  val defaultThreads: Int = Runtime.getRuntime().availableProcessors()
  val defaultQueueSize: Int = Int.MinValue
  val defaultCoreThreads: Int = Int.MinValue
  val defaultMaxThreads: Int = Int.MinValue
  val defaultBlockOnFullQueue: Boolean = true

  def apply(
    name: String,
    threads: Int = defaultThreads,
    queueSize: Int = defaultQueueSize,
    coreThreads: Int = defaultCoreThreads,
    maxThreads: Int = defaultMaxThreads,
    blockOnFullQueue: Boolean = defaultBlockOnFullQueue
  ): TaskRunner = {
    val _coreThreads: Int = if (coreThreads == Int.MinValue) threads else coreThreads
    val _maxThreads:  Int = if (maxThreads == Int.MinValue) threads else maxThreads
    val _queueSize:   Int = if (queueSize == Int.MinValue) threads * 2 else queueSize
    new TaskRunner(name, _coreThreads, _maxThreads, _queueSize, blockOnFullQueue)
  }

  def newBuilder: TaskRunnerBuilder = new TaskRunnerBuilder
}

final class TaskRunner(val name: String, val coreThreads: Int, val maxThreads: Int, val queueSize: Int, val blockOnFullQueue: Boolean = true) extends TaskRunnerBase(name) {
  
  private[this] val queue: BlockingQueue[Runnable] = {
    if(queueSize > 0) new ArrayBlockingQueue[Runnable](queueSize)
    else if (queueSize == 0) new SynchronousQueue[Runnable]()
    else new LinkedBlockingQueue[Runnable]()
  }
  
  private class BlockRejectExecutionHandler() extends RejectedExecutionHandler {
    def rejectedExecution(r: Runnable, executor: ThreadPoolExecutor) {
      // If the executor is shutting down then display a warning and drop the task
      if(executor.isShutdown) {
        shutdownWarning
        return
      }
      
      // Block on adding the task to the queue
      queue.put(r)
    }
  }
  
  private class StandardRejectExecutionHandler() extends RejectedExecutionHandler {
    def rejectedExecution(r: Runnable, executor: ThreadPoolExecutor) {
      // If the executor is shutting down then display a warning and drop the task
      if(executor.isShutdown) {
        shutdownWarning
        return
      }
      
      throw new RejectedExecutionException(s"$name - Queue is full")
    }
  }

  protected val executor: ThreadPoolExecutor = {
    val rejectedHandler: RejectedExecutionHandler = if (blockOnFullQueue) new BlockRejectExecutionHandler() else new StandardRejectExecutionHandler()
    val exec: ThreadPoolExecutor = new ThreadPoolExecutor(coreThreads, maxThreads, 60, TimeUnit.SECONDS, queue, TaskRunnerBase.newTaskRunnerThreadFactory(name), rejectedHandler)
    exec.allowCoreThreadTimeOut(true)
    exec
  }
}
