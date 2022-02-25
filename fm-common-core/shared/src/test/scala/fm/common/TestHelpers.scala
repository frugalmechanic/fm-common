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

import org.scalatest.exceptions.ModifiableMessage

object TestHelpers {
  final def withCallerInfo[T](fun: => T): T = try {
    fun
  } catch {
    case ex: ModifiableMessage[_] => throw ex.modifyMessage(modifyMsg(getStackInfo()))
  }

  private def getStackInfo(): String = {
    val elements: Array[StackTraceElement] = Thread.currentThread().getStackTrace()

    val idx: Int = elements.lastIndexWhere{ _.getMethodName() == "withCallerInfo" }

    if (-1 == idx) return "<unknown>"

    // java.lang.Thread.getStackTrace(Thread.java:1567)
    // fm.money.TestMoneyParser.getStackInfo(TestMoneyParser.scala:141)
    // fm.money.TestMoneyParser.withCallerInfo(TestMoneyParser.scala:135)                     <----- idx
    // fm.money.TestMoneyParser.fm$money$TestMoneyParser$$none(TestMoneyParser.scala:112)     <----- method containing withCallerInfo call (idx + 1)
    // fm.money.TestMoneyParser$$anonfun$1.apply$mcV$sp(TestMoneyParser.scala:17)             <----- Caller (idx + 2)
    // fm.money.TestMoneyParser$$anonfun$1.apply(TestMoneyParser.scala:7)
    // fm.money.TestMoneyParser$$anonfun$1.apply(TestMoneyParser.scala:7)

    val e: StackTraceElement = elements(idx + 2)
    val file = e.getFileName()
    val line = e.getLineNumber()

    s"$file:$line"
  }

  private def modifyMsg(stackInfo: String)(currentMessage: Option[String]): Option[String] = currentMessage.map { (msg: String) =>
    val toAdd = " (Caller: "+stackInfo+")"
    if (msg.contains(toAdd)) msg else msg+toAdd
  }.orElse{ Some(stackInfo) }
}
