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

import fm.common.TaskRunnerBase.{ClearingBlockRunnable, ClearingBlockRunnableWithResult}
import java.util.concurrent.RejectedExecutionException
import scala.concurrent.{Future, Promise}

abstract class TaskRunnerNonPriority(name: String) extends TaskRunnerBase(name) {
  /**
   * Attempt to submit this job to the queue.  Returns true if successful or false if the queue is full
   */
  final def tryExecute(f: => Unit): Boolean = try {
    execute(f)
    true
  } catch {
    case _: RejectedExecutionException => false
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
    case _: RejectedExecutionException => None
  }

  final def submit[T](f: => T): Future[T] = {
    val promise = Promise[T]()
    executor.submit(new ClearingBlockRunnableWithResult(f, promise))
    promise.future
  }
}
