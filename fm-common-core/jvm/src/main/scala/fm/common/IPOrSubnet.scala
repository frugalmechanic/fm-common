/*
 * Copyright 2015 Frugal Mechanic (http://frugalmechanic.com)
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

object IPOrSubnet {
  def get(ip: String): Option[IPOrSubnet] = IP.get(ip) orElse IPSubnet.get(ip)
  
  def apply(ip: String): IPOrSubnet = get(ip).getOrElse{ throw new InvalidIPException(ip) }

  def apply(start: IP, end: IP): IPOrSubnet = if (start === end) start else IPSubnet.forRange(start, end)
}

trait IPOrSubnet extends Any {
  def start: IP
  def end: IP
  def mask: Int
  
  def toIPSubnet: IPSubnet
  
  def contains(other: IP): Boolean
}