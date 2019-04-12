/*
 * Copyright 2019 Frugal Mechanic (http://frugalmechanic.com)
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

object TaskRunnerBuilder {
  private val defaultName: String = "TaskRunner"
}

final class TaskRunnerBuilder {
  var name: String              = TaskRunnerBuilder.defaultName
  var threads: Int              = TaskRunner.defaultThreads
  var queueSize: Int            = TaskRunner.defaultQueueSize
  var coreThreads: Int          = TaskRunner.defaultCoreThreads
  var maxThreads: Int           = TaskRunner.defaultMaxThreads
  var blockOnFullQueue: Boolean = TaskRunner.defaultBlockOnFullQueue

  def withName(name: String): this.type = {
    this.name = name
    this
  }

  def withThreads(threads: Int): this.type = {
    this.threads = threads
    this
  }

  def withQueueSize(queueSize: Int): this.type = {
    this.queueSize = queueSize
    this
  }

  def withCoreThreads(coreThreads: Int): this.type = {
    this.coreThreads = coreThreads
    this
  }

  def withMaxThreads(maxThreads: Int): this.type = {
    this.maxThreads = maxThreads
    this
  }

  def withBlockOnFullQueue(blockOnFullQueue: Boolean): this.type = {
    this.blockOnFullQueue = blockOnFullQueue
    this
  }

  def withBlockOnFullQueue: this.type = {
    this.blockOnFullQueue = true
    this
  }

  def withoutBlockOnFullQueue: this.type = {
    this.blockOnFullQueue = false
    this
  }

  def result(): TaskRunner = TaskRunner(
    name = name,
    threads = threads,
    queueSize = queueSize,
    coreThreads = coreThreads,
    maxThreads = maxThreads,
    blockOnFullQueue = blockOnFullQueue
  )
}

