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

import java.util.concurrent.{ScheduledFuture => JavaScheduledFuture, ScheduledThreadPoolExecutor, TimeUnit}
import scala.annotation.implicitNotFound
import scala.concurrent.Promise
import scala.concurrent.duration.FiniteDuration

object ScheduledTaskRunner {
  final class RunnableWrapper[U](f: => U) extends Runnable {
    def run(): Unit = f
  }
  
  object Implicits {
    /**
     * The implicit global `ScheduledTaskRunner`. Import `global` when you want to provide the global
     * `ScheduledTaskRunner` implicitly.
     */
    implicit lazy val global: ScheduledTaskRunner = ScheduledTaskRunner("ScheduledTaskRunner.global")
  }
  
  /**
   * The explicit global `ExecutionContext`. Invoke `global` when you want to provide the global
   * `ExecutionContext` explicitly.
   */
  def global: ScheduledTaskRunner = Implicits.global

  def apply(name: String): ScheduledTaskRunner = apply(name, Runtime.getRuntime().availableProcessors())

  def apply(name: String, threads: Int): ScheduledTaskRunner = {
    val executor: ScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(threads,TaskRunnerBase. newTaskRunnerThreadFactory(name))
    ScheduledTaskRunner(name, executor)
  }
}

@implicitNotFound("""Cannot find an implicit ScheduledTaskRunner. You might pass
an (implicit st: ScheduledTaskRunner) parameter to your method
or import fm.common.ScheduledTaskRunner.Implicits.global.""")
final case class ScheduledTaskRunner(name: String, protected val executor: ScheduledThreadPoolExecutor) extends TaskRunnerBase(name) {
  import ScheduledTaskRunner.RunnableWrapper
  import TaskRunnerBase.ClearingBlockRunnableWithResult

  // We want tasks removed as soon as they are cancelled since the common case for this class
  // is handling http/service timeouts where we will normally cancel the task
  executor.setRemoveOnCancelPolicy(true)
  
  /**
   * Creates and executes a one-shot action that becomes enabled after the given delay.
   */
  def schedule(runnable: Runnable, duration: FiniteDuration): ScheduledTask = {
    val java: JavaScheduledFuture[_] = executor.schedule(runnable, duration.length, duration.unit)
    ScheduledTask.wrap(java)
  }
  
  /**
   * Creates and executes a one-shot action that becomes enabled after the given delay.
   */
  def schedule[T](duration: FiniteDuration)(f: => T): ScheduledFuture[T] = {
    val p: Promise[T] = Promise()
    val task: ScheduledTask = schedule(new ClearingBlockRunnableWithResult(f, p), duration)
    ScheduledFuture[T](p, task)
  }
  
  /**
   * Creates and executes a periodic action that becomes enabled first after the given initial delay, and subsequently with the given period; 
   * that is executions will commence after initialDelay then initialDelay+period, then initialDelay + 2 * period, and so on.
   */
  def scheduleAtFixedRate[U](initialDelay: FiniteDuration, delay: FiniteDuration)(f: => U): ScheduledTask = {
    val java: JavaScheduledFuture[_] = executor.scheduleAtFixedRate(new RunnableWrapper(f), initialDelay.toMillis, delay.toMillis, TimeUnit.MILLISECONDS)
    ScheduledTask.wrap(java)
  }
  
  /**
   * Creates and executes a periodic action that becomes enabled first after the given initial delay, and subsequently with the given 
   * delay between the termination of one execution and the commencement of the next.
   */
  def scheduleWithFixedDelay[U](initialDelay: FiniteDuration, delay: FiniteDuration)(f: => U): ScheduledTask = {
    val java: JavaScheduledFuture[_] = executor.scheduleWithFixedDelay(new RunnableWrapper(f), initialDelay.toMillis, delay.toMillis, TimeUnit.MILLISECONDS)
    ScheduledTask.wrap(java)
  }
}