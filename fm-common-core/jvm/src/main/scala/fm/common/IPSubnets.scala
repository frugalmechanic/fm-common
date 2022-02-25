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

object IPSubnets {
  val Private: IPSubnets = new IPSubnets(Vector(
    "10.0.0.0/8",     // private
    "172.16.0.0/12",  // private
    "192.168.0.0/16", // private
    "127.0.0.0/8",    // localhost
    "169.254.0.0/16"  // link-local
  ))
}

final case class IPSubnets(subnets: Vector[IPSubnet]) {
  def this(nets: Seq[String]) = this(nets.map{IPSubnet.parse(_)}.toVector)
  
  def contains(ip: IP): Boolean = subnets.exists{ _.contains(ip) }
  
  def hasDefaultRoute: Boolean = subnets.exists{ _.isDefaultRoute }
  def hasQuadZero: Boolean = subnets.exists{ _.isQuadZero }
}