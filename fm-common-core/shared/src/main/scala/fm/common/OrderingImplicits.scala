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

object OrderingImplicits extends OrderingImplicits

/**
 * scala.math.Ordering only goes up to Tuple9
 */
trait OrderingImplicits {
  implicit def Tuple10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10](implicit ord1: Ordering[T1], ord2: Ordering[T2], ord3: Ordering[T3], ord4: Ordering[T4], ord5: Ordering[T5], ord6: Ordering[T6], ord7: Ordering[T7], ord8 : Ordering[T8], ord9: Ordering[T9], ord10: Ordering[T10]): Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)] =
    new Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)]{
      def compare(x: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), y: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)): Int = {
        val compare1 = ord1.compare(x._1, y._1)
        if (compare1 != 0) return compare1
        val compare2 = ord2.compare(x._2, y._2)
        if (compare2 != 0) return compare2
        val compare3 = ord3.compare(x._3, y._3)
        if (compare3 != 0) return compare3
        val compare4 = ord4.compare(x._4, y._4)
        if (compare4 != 0) return compare4
        val compare5 = ord5.compare(x._5, y._5)
        if (compare5 != 0) return compare5
        val compare6 = ord6.compare(x._6, y._6)
        if (compare6 != 0) return compare6
        val compare7 = ord7.compare(x._7, y._7)
        if (compare7 != 0) return compare7
        val compare8 = ord8.compare(x._8, y._8)
        if (compare8 != 0) return compare8
        val compare9 = ord9.compare(x._9, y._9)
        if (compare9 != 0) return compare9
        val compare10 = ord10.compare(x._10, y._10)
        if (compare10 != 0) return compare10
        0
      }
    }
  
  implicit def Tuple11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11](implicit ord1: Ordering[T1], ord2: Ordering[T2], ord3: Ordering[T3], ord4: Ordering[T4], ord5: Ordering[T5], ord6: Ordering[T6], ord7: Ordering[T7], ord8 : Ordering[T8], ord9: Ordering[T9], ord10: Ordering[T10], ord11: Ordering[T11]): Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11)] =
    new Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11)]{
      def compare(x: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), y: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11)): Int = {
        val compare1 = ord1.compare(x._1, y._1)
        if (compare1 != 0) return compare1
        val compare2 = ord2.compare(x._2, y._2)
        if (compare2 != 0) return compare2
        val compare3 = ord3.compare(x._3, y._3)
        if (compare3 != 0) return compare3
        val compare4 = ord4.compare(x._4, y._4)
        if (compare4 != 0) return compare4
        val compare5 = ord5.compare(x._5, y._5)
        if (compare5 != 0) return compare5
        val compare6 = ord6.compare(x._6, y._6)
        if (compare6 != 0) return compare6
        val compare7 = ord7.compare(x._7, y._7)
        if (compare7 != 0) return compare7
        val compare8 = ord8.compare(x._8, y._8)
        if (compare8 != 0) return compare8
        val compare9 = ord9.compare(x._9, y._9)
        if (compare9 != 0) return compare9
        val compare10 = ord10.compare(x._10, y._10)
        if (compare10 != 0) return compare10
        val compare11 = ord11.compare(x._11, y._11)
        if (compare11 != 0) return compare11
        0
      }
    }
  
  implicit def Tuple12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12](implicit ord1: Ordering[T1], ord2: Ordering[T2], ord3: Ordering[T3], ord4: Ordering[T4], ord5: Ordering[T5], ord6: Ordering[T6], ord7: Ordering[T7], ord8 : Ordering[T8], ord9: Ordering[T9], ord10: Ordering[T10], ord11: Ordering[T11], ord12: Ordering[T12]): Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12)] =
    new Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12)]{
      def compare(x: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), y: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12)): Int = {
        val compare1 = ord1.compare(x._1, y._1)
        if (compare1 != 0) return compare1
        val compare2 = ord2.compare(x._2, y._2)
        if (compare2 != 0) return compare2
        val compare3 = ord3.compare(x._3, y._3)
        if (compare3 != 0) return compare3
        val compare4 = ord4.compare(x._4, y._4)
        if (compare4 != 0) return compare4
        val compare5 = ord5.compare(x._5, y._5)
        if (compare5 != 0) return compare5
        val compare6 = ord6.compare(x._6, y._6)
        if (compare6 != 0) return compare6
        val compare7 = ord7.compare(x._7, y._7)
        if (compare7 != 0) return compare7
        val compare8 = ord8.compare(x._8, y._8)
        if (compare8 != 0) return compare8
        val compare9 = ord9.compare(x._9, y._9)
        if (compare9 != 0) return compare9
        val compare10 = ord10.compare(x._10, y._10)
        if (compare10 != 0) return compare10
        val compare11 = ord11.compare(x._11, y._11)
        if (compare11 != 0) return compare11
        val compare12 = ord12.compare(x._12, y._12)
        if (compare12 != 0) return compare12
        0
      }
    }
  
  implicit def Tuple13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13](implicit ord1: Ordering[T1], ord2: Ordering[T2], ord3: Ordering[T3], ord4: Ordering[T4], ord5: Ordering[T5], ord6: Ordering[T6], ord7: Ordering[T7], ord8 : Ordering[T8], ord9: Ordering[T9], ord10: Ordering[T10], ord11: Ordering[T11], ord12: Ordering[T12], ord13: Ordering[T13]): Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13)] =
    new Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13)]{
      def compare(x: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), y: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13)): Int = {
        val compare1 = ord1.compare(x._1, y._1)
        if (compare1 != 0) return compare1
        val compare2 = ord2.compare(x._2, y._2)
        if (compare2 != 0) return compare2
        val compare3 = ord3.compare(x._3, y._3)
        if (compare3 != 0) return compare3
        val compare4 = ord4.compare(x._4, y._4)
        if (compare4 != 0) return compare4
        val compare5 = ord5.compare(x._5, y._5)
        if (compare5 != 0) return compare5
        val compare6 = ord6.compare(x._6, y._6)
        if (compare6 != 0) return compare6
        val compare7 = ord7.compare(x._7, y._7)
        if (compare7 != 0) return compare7
        val compare8 = ord8.compare(x._8, y._8)
        if (compare8 != 0) return compare8
        val compare9 = ord9.compare(x._9, y._9)
        if (compare9 != 0) return compare9
        val compare10 = ord10.compare(x._10, y._10)
        if (compare10 != 0) return compare10
        val compare11 = ord11.compare(x._11, y._11)
        if (compare11 != 0) return compare11
        val compare12 = ord12.compare(x._12, y._12)
        if (compare12 != 0) return compare12
        val compare13 = ord13.compare(x._13, y._13)
        if (compare13 != 0) return compare13
        0
      }
    }
  
  implicit def Tuple14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14](implicit ord1: Ordering[T1], ord2: Ordering[T2], ord3: Ordering[T3], ord4: Ordering[T4], ord5: Ordering[T5], ord6: Ordering[T6], ord7: Ordering[T7], ord8 : Ordering[T8], ord9: Ordering[T9], ord10: Ordering[T10], ord11: Ordering[T11], ord12: Ordering[T12], ord13: Ordering[T13], ord14: Ordering[T14]): Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14)] =
    new Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14)]{
      def compare(x: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), y: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14)): Int = {
        val compare1 = ord1.compare(x._1, y._1)
        if (compare1 != 0) return compare1
        val compare2 = ord2.compare(x._2, y._2)
        if (compare2 != 0) return compare2
        val compare3 = ord3.compare(x._3, y._3)
        if (compare3 != 0) return compare3
        val compare4 = ord4.compare(x._4, y._4)
        if (compare4 != 0) return compare4
        val compare5 = ord5.compare(x._5, y._5)
        if (compare5 != 0) return compare5
        val compare6 = ord6.compare(x._6, y._6)
        if (compare6 != 0) return compare6
        val compare7 = ord7.compare(x._7, y._7)
        if (compare7 != 0) return compare7
        val compare8 = ord8.compare(x._8, y._8)
        if (compare8 != 0) return compare8
        val compare9 = ord9.compare(x._9, y._9)
        if (compare9 != 0) return compare9
        val compare10 = ord10.compare(x._10, y._10)
        if (compare10 != 0) return compare10
        val compare11 = ord11.compare(x._11, y._11)
        if (compare11 != 0) return compare11
        val compare12 = ord12.compare(x._12, y._12)
        if (compare12 != 0) return compare12
        val compare13 = ord13.compare(x._13, y._13)
        if (compare13 != 0) return compare13
        val compare14 = ord14.compare(x._14, y._14)
        if (compare14 != 0) return compare14
        0
      }
    }
  
  implicit def Tuple15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15](implicit ord1: Ordering[T1], ord2: Ordering[T2], ord3: Ordering[T3], ord4: Ordering[T4], ord5: Ordering[T5], ord6: Ordering[T6], ord7: Ordering[T7], ord8 : Ordering[T8], ord9: Ordering[T9], ord10: Ordering[T10], ord11: Ordering[T11], ord12: Ordering[T12], ord13: Ordering[T13], ord14: Ordering[T14], ord15: Ordering[T15]): Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15)] =
    new Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15)]{
      def compare(x: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), y: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15)): Int = {
        val compare1 = ord1.compare(x._1, y._1)
        if (compare1 != 0) return compare1
        val compare2 = ord2.compare(x._2, y._2)
        if (compare2 != 0) return compare2
        val compare3 = ord3.compare(x._3, y._3)
        if (compare3 != 0) return compare3
        val compare4 = ord4.compare(x._4, y._4)
        if (compare4 != 0) return compare4
        val compare5 = ord5.compare(x._5, y._5)
        if (compare5 != 0) return compare5
        val compare6 = ord6.compare(x._6, y._6)
        if (compare6 != 0) return compare6
        val compare7 = ord7.compare(x._7, y._7)
        if (compare7 != 0) return compare7
        val compare8 = ord8.compare(x._8, y._8)
        if (compare8 != 0) return compare8
        val compare9 = ord9.compare(x._9, y._9)
        if (compare9 != 0) return compare9
        val compare10 = ord10.compare(x._10, y._10)
        if (compare10 != 0) return compare10
        val compare11 = ord11.compare(x._11, y._11)
        if (compare11 != 0) return compare11
        val compare12 = ord12.compare(x._12, y._12)
        if (compare12 != 0) return compare12
        val compare13 = ord13.compare(x._13, y._13)
        if (compare13 != 0) return compare13
        val compare14 = ord14.compare(x._14, y._14)
        if (compare14 != 0) return compare14
        val compare15 = ord15.compare(x._15, y._15)
        if (compare15 != 0) return compare15
        0
      }
    }
  
  implicit def Tuple16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16](implicit ord1: Ordering[T1], ord2: Ordering[T2], ord3: Ordering[T3], ord4: Ordering[T4], ord5: Ordering[T5], ord6: Ordering[T6], ord7: Ordering[T7], ord8 : Ordering[T8], ord9: Ordering[T9], ord10: Ordering[T10], ord11: Ordering[T11], ord12: Ordering[T12], ord13: Ordering[T13], ord14: Ordering[T14], ord15: Ordering[T15], ord16: Ordering[T16]): Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16)] =
    new Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16)]{
      def compare(x: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), y: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16)): Int = {
        val compare1 = ord1.compare(x._1, y._1)
        if (compare1 != 0) return compare1
        val compare2 = ord2.compare(x._2, y._2)
        if (compare2 != 0) return compare2
        val compare3 = ord3.compare(x._3, y._3)
        if (compare3 != 0) return compare3
        val compare4 = ord4.compare(x._4, y._4)
        if (compare4 != 0) return compare4
        val compare5 = ord5.compare(x._5, y._5)
        if (compare5 != 0) return compare5
        val compare6 = ord6.compare(x._6, y._6)
        if (compare6 != 0) return compare6
        val compare7 = ord7.compare(x._7, y._7)
        if (compare7 != 0) return compare7
        val compare8 = ord8.compare(x._8, y._8)
        if (compare8 != 0) return compare8
        val compare9 = ord9.compare(x._9, y._9)
        if (compare9 != 0) return compare9
        val compare10 = ord10.compare(x._10, y._10)
        if (compare10 != 0) return compare10
        val compare11 = ord11.compare(x._11, y._11)
        if (compare11 != 0) return compare11
        val compare12 = ord12.compare(x._12, y._12)
        if (compare12 != 0) return compare12
        val compare13 = ord13.compare(x._13, y._13)
        if (compare13 != 0) return compare13
        val compare14 = ord14.compare(x._14, y._14)
        if (compare14 != 0) return compare14
        val compare15 = ord15.compare(x._15, y._15)
        if (compare15 != 0) return compare15
        val compare16 = ord16.compare(x._16, y._16)
        if (compare16 != 0) return compare16
        0
      }
    }
  
  implicit def Tuple17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17](implicit ord1: Ordering[T1], ord2: Ordering[T2], ord3: Ordering[T3], ord4: Ordering[T4], ord5: Ordering[T5], ord6: Ordering[T6], ord7: Ordering[T7], ord8 : Ordering[T8], ord9: Ordering[T9], ord10: Ordering[T10], ord11: Ordering[T11], ord12: Ordering[T12], ord13: Ordering[T13], ord14: Ordering[T14], ord15: Ordering[T15], ord16: Ordering[T16], ord17: Ordering[T17]): Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17)] =
    new Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17)]{
      def compare(x: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), y: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17)): Int = {
        val compare1 = ord1.compare(x._1, y._1)
        if (compare1 != 0) return compare1
        val compare2 = ord2.compare(x._2, y._2)
        if (compare2 != 0) return compare2
        val compare3 = ord3.compare(x._3, y._3)
        if (compare3 != 0) return compare3
        val compare4 = ord4.compare(x._4, y._4)
        if (compare4 != 0) return compare4
        val compare5 = ord5.compare(x._5, y._5)
        if (compare5 != 0) return compare5
        val compare6 = ord6.compare(x._6, y._6)
        if (compare6 != 0) return compare6
        val compare7 = ord7.compare(x._7, y._7)
        if (compare7 != 0) return compare7
        val compare8 = ord8.compare(x._8, y._8)
        if (compare8 != 0) return compare8
        val compare9 = ord9.compare(x._9, y._9)
        if (compare9 != 0) return compare9
        val compare10 = ord10.compare(x._10, y._10)
        if (compare10 != 0) return compare10
        val compare11 = ord11.compare(x._11, y._11)
        if (compare11 != 0) return compare11
        val compare12 = ord12.compare(x._12, y._12)
        if (compare12 != 0) return compare12
        val compare13 = ord13.compare(x._13, y._13)
        if (compare13 != 0) return compare13
        val compare14 = ord14.compare(x._14, y._14)
        if (compare14 != 0) return compare14
        val compare15 = ord15.compare(x._15, y._15)
        if (compare15 != 0) return compare15
        val compare16 = ord16.compare(x._16, y._16)
        if (compare16 != 0) return compare16
        val compare17 = ord17.compare(x._17, y._17)
        if (compare17 != 0) return compare17
        0
      }
    }
  
  implicit def Tuple18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18](implicit ord1: Ordering[T1], ord2: Ordering[T2], ord3: Ordering[T3], ord4: Ordering[T4], ord5: Ordering[T5], ord6: Ordering[T6], ord7: Ordering[T7], ord8 : Ordering[T8], ord9: Ordering[T9], ord10: Ordering[T10], ord11: Ordering[T11], ord12: Ordering[T12], ord13: Ordering[T13], ord14: Ordering[T14], ord15: Ordering[T15], ord16: Ordering[T16], ord17: Ordering[T17], ord18: Ordering[T18]): Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18)] =
    new Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18)]{
      def compare(x: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), y: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18)): Int = {
        val compare1 = ord1.compare(x._1, y._1)
        if (compare1 != 0) return compare1
        val compare2 = ord2.compare(x._2, y._2)
        if (compare2 != 0) return compare2
        val compare3 = ord3.compare(x._3, y._3)
        if (compare3 != 0) return compare3
        val compare4 = ord4.compare(x._4, y._4)
        if (compare4 != 0) return compare4
        val compare5 = ord5.compare(x._5, y._5)
        if (compare5 != 0) return compare5
        val compare6 = ord6.compare(x._6, y._6)
        if (compare6 != 0) return compare6
        val compare7 = ord7.compare(x._7, y._7)
        if (compare7 != 0) return compare7
        val compare8 = ord8.compare(x._8, y._8)
        if (compare8 != 0) return compare8
        val compare9 = ord9.compare(x._9, y._9)
        if (compare9 != 0) return compare9
        val compare10 = ord10.compare(x._10, y._10)
        if (compare10 != 0) return compare10
        val compare11 = ord11.compare(x._11, y._11)
        if (compare11 != 0) return compare11
        val compare12 = ord12.compare(x._12, y._12)
        if (compare12 != 0) return compare12
        val compare13 = ord13.compare(x._13, y._13)
        if (compare13 != 0) return compare13
        val compare14 = ord14.compare(x._14, y._14)
        if (compare14 != 0) return compare14
        val compare15 = ord15.compare(x._15, y._15)
        if (compare15 != 0) return compare15
        val compare16 = ord16.compare(x._16, y._16)
        if (compare16 != 0) return compare16
        val compare17 = ord17.compare(x._17, y._17)
        if (compare17 != 0) return compare17
        val compare18 = ord18.compare(x._18, y._18)
        if (compare18 != 0) return compare18
        0
      }
    }
  
  implicit def Tuple19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19](implicit ord1: Ordering[T1], ord2: Ordering[T2], ord3: Ordering[T3], ord4: Ordering[T4], ord5: Ordering[T5], ord6: Ordering[T6], ord7: Ordering[T7], ord8 : Ordering[T8], ord9: Ordering[T9], ord10: Ordering[T10], ord11: Ordering[T11], ord12: Ordering[T12], ord13: Ordering[T13], ord14: Ordering[T14], ord15: Ordering[T15], ord16: Ordering[T16], ord17: Ordering[T17], ord18: Ordering[T18], ord19: Ordering[T19]): Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19)] =
    new Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19)]{
      def compare(x: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), y: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19)): Int = {
        val compare1 = ord1.compare(x._1, y._1)
        if (compare1 != 0) return compare1
        val compare2 = ord2.compare(x._2, y._2)
        if (compare2 != 0) return compare2
        val compare3 = ord3.compare(x._3, y._3)
        if (compare3 != 0) return compare3
        val compare4 = ord4.compare(x._4, y._4)
        if (compare4 != 0) return compare4
        val compare5 = ord5.compare(x._5, y._5)
        if (compare5 != 0) return compare5
        val compare6 = ord6.compare(x._6, y._6)
        if (compare6 != 0) return compare6
        val compare7 = ord7.compare(x._7, y._7)
        if (compare7 != 0) return compare7
        val compare8 = ord8.compare(x._8, y._8)
        if (compare8 != 0) return compare8
        val compare9 = ord9.compare(x._9, y._9)
        if (compare9 != 0) return compare9
        val compare10 = ord10.compare(x._10, y._10)
        if (compare10 != 0) return compare10
        val compare11 = ord11.compare(x._11, y._11)
        if (compare11 != 0) return compare11
        val compare12 = ord12.compare(x._12, y._12)
        if (compare12 != 0) return compare12
        val compare13 = ord13.compare(x._13, y._13)
        if (compare13 != 0) return compare13
        val compare14 = ord14.compare(x._14, y._14)
        if (compare14 != 0) return compare14
        val compare15 = ord15.compare(x._15, y._15)
        if (compare15 != 0) return compare15
        val compare16 = ord16.compare(x._16, y._16)
        if (compare16 != 0) return compare16
        val compare17 = ord17.compare(x._17, y._17)
        if (compare17 != 0) return compare17
        val compare18 = ord18.compare(x._18, y._18)
        if (compare18 != 0) return compare18
        val compare19 = ord19.compare(x._19, y._19)
        if (compare19 != 0) return compare19
        0
      }
    }
  
  implicit def Tuple20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20](implicit ord1: Ordering[T1], ord2: Ordering[T2], ord3: Ordering[T3], ord4: Ordering[T4], ord5: Ordering[T5], ord6: Ordering[T6], ord7: Ordering[T7], ord8 : Ordering[T8], ord9: Ordering[T9], ord10: Ordering[T10], ord11: Ordering[T11], ord12: Ordering[T12], ord13: Ordering[T13], ord14: Ordering[T14], ord15: Ordering[T15], ord16: Ordering[T16], ord17: Ordering[T17], ord18: Ordering[T18], ord19: Ordering[T19], ord20: Ordering[T20]): Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20)] =
    new Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20)]{
      def compare(x: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), y: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20)): Int = {
        val compare1 = ord1.compare(x._1, y._1)
        if (compare1 != 0) return compare1
        val compare2 = ord2.compare(x._2, y._2)
        if (compare2 != 0) return compare2
        val compare3 = ord3.compare(x._3, y._3)
        if (compare3 != 0) return compare3
        val compare4 = ord4.compare(x._4, y._4)
        if (compare4 != 0) return compare4
        val compare5 = ord5.compare(x._5, y._5)
        if (compare5 != 0) return compare5
        val compare6 = ord6.compare(x._6, y._6)
        if (compare6 != 0) return compare6
        val compare7 = ord7.compare(x._7, y._7)
        if (compare7 != 0) return compare7
        val compare8 = ord8.compare(x._8, y._8)
        if (compare8 != 0) return compare8
        val compare9 = ord9.compare(x._9, y._9)
        if (compare9 != 0) return compare9
        val compare10 = ord10.compare(x._10, y._10)
        if (compare10 != 0) return compare10
        val compare11 = ord11.compare(x._11, y._11)
        if (compare11 != 0) return compare11
        val compare12 = ord12.compare(x._12, y._12)
        if (compare12 != 0) return compare12
        val compare13 = ord13.compare(x._13, y._13)
        if (compare13 != 0) return compare13
        val compare14 = ord14.compare(x._14, y._14)
        if (compare14 != 0) return compare14
        val compare15 = ord15.compare(x._15, y._15)
        if (compare15 != 0) return compare15
        val compare16 = ord16.compare(x._16, y._16)
        if (compare16 != 0) return compare16
        val compare17 = ord17.compare(x._17, y._17)
        if (compare17 != 0) return compare17
        val compare18 = ord18.compare(x._18, y._18)
        if (compare18 != 0) return compare18
        val compare19 = ord19.compare(x._19, y._19)
        if (compare19 != 0) return compare19
        val compare20 = ord20.compare(x._20, y._20)
        if (compare20 != 0) return compare20
        0
      }
    }
}