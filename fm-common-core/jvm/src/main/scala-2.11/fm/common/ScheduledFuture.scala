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

import java.util.concurrent.CancellationException
import scala.concurrent.{CanAwait, ExecutionContext, Future, Promise}
import scala.concurrent.duration.Duration
import scala.util.Try

object ScheduledFuture {
  def apply[T](promise: Promise[T], task: ScheduledTask): ScheduledFuture[T] = new ScheduledFuture(promise, task)
}

final class ScheduledFuture[T](promise: Promise[T], task: ScheduledTask) extends Future[T] with ScheduledTask {
  private def self: Future[T] = promise.future
  
  /**
   * Attempts to cancel execution of this task. This attempt will fail if the task has already 
   * completed, has already been cancelled, or could not be cancelled for some other reason. 
   * If successful, and this task has not started when cancel is called, this task should 
   * never run. If the task has already started, then the mayInterruptIfRunning parameter 
   * determines whether the thread executing this task should be interrupted in an attempt 
   * to stop the task. 
   */
  def cancel(): Boolean = {
    val res: Boolean = task.cancel()
    if (res) promise.tryFailure(new CancellationException("Scheduled Task has been canceled"))
    res
  }

  /**
   * Returns true if this task was cancelled before it completed normally.
   */
  def isCancelled(): Boolean = task.isCancelled()
  
  //
  // Scala Future implementation
  //
  def isCompleted: Boolean = self.isCompleted
  def onComplete[U](func: (Try[T]) ⇒ U)(implicit executor: ExecutionContext): Unit = self.onComplete(func)
  def ready(atMost: Duration)(implicit permit: CanAwait): this.type = { self.ready(atMost); this }
  def result(atMost: Duration)(implicit permit: CanAwait): T = self.result(atMost)
  def value: Option[Try[T]] = self.value
}