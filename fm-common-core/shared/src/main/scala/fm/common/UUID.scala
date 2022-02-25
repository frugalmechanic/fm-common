/*
 * Copyright 2017 Frugal Mechanic (http://frugalmechanic.com)
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

import java.lang.StringBuilder
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.{Arrays, Date}
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicInteger
import scala.math.Ordering
import scala.util.Try

object UUID {
  private[this] val UnsignedSixByteMin: Long = 0L
  private[this] val UnsignedSixByteMax: Long = 0xffffffffffffL // We only use 6 bytes for the timestamp and random fields
  
  private[this] val UnsignedShortMin: Int = 0
  private[this] val UnsignedShortMax: Int = 65535 // Short.MaxValue - Short.MinValue
  
  private[this] val SignedShortMax: Int = 32768
  
  private[this] val counter = new AtomicInteger(ThreadLocalRandom.current().nextInt)
  
  private def nextCounter(epochMilli: Long): Int = counter.getAndIncrement() & 0xffff
  
  implicit object ordering extends Ordering[UUID] { def compare(a: UUID, b: UUID): Int = a.compare(b) }

  /** 000000000000-0000-0000-000000000000 */
  val Zero: UUID = UUID(0L, 0L)

  /** ffffffffffff-ffff-ffff-ffffffffffff */
  val UnsignedMaxValue: UUID = UUID(-1L, -1L)

  /** 7fffffffffff-ffff-7fff-ffffffffffff */
  val SignedMaxValue: UUID = UUID(Long.MaxValue, Long.MaxValue)

  /** 800000000000-0000-8000-000000000000 */
  val SignedMinValue: UUID = UUID(Long.MinValue, Long.MinValue)

  /**
   * Creates a completely random UUID
   */
  def random(): UUID = {
    val random: ThreadLocalRandom = ThreadLocalRandom.current()
    UUID(random.nextLong(), random.nextLong())
  }

  /**
   * Creates a new UUID based on the current time with a random node id
   */
  def apply(): UUID = {
    // No Node Id Specified so we use a random negative Short
    makeWithNodeId(makeRandomNodeId())
  }

  /**
   * Creates a new UUID based on the current time with the given node id
   */
  def apply(nodeId: Int): UUID = {
    if (nodeId < 0 || nodeId > 32767) throw new IllegalArgumentException("Invalid NodeId: '"+nodeId+"'. NodeId must be between 0 and 32767 (inclusive).")
    makeWithNodeId(nodeId)
  }

  def apply(date: Date): UUID = forEpochMilli(date.getTime)
  def apply(date: ImmutableDate): UUID = forEpochMilli(date.getTime)
  def apply(instant: Instant): UUID = forEpochMilli(InstantCompat.toEpochMilli(instant))

  def forEpochMilli(epochMilli: Long): UUID = makeWithNodeIdAndEpochMilli(makeRandomNodeId(), epochMilli)

  private def makeRandomNodeId(): Int = {
    // Get a random int between 0 (inclusive) and 32768 (exclusive)
    val randomInt: Int = ThreadLocalRandom.current().nextInt(SignedShortMax)

    // Add one (we don't want zero as a value) and make it negative.
    // This should give us a number between -32768 (inclusive) and -1 (inclusive)
    // which corresponds to the range of all negative Short values
    (randomInt + 1) * -1
  }

  private def makeWithNodeId(nodeId: Int): UUID = {
    makeWithNodeIdAndEpochMilli(nodeId, System.currentTimeMillis())
  }

  private def makeWithNodeIdAndEpochMilli(nodeId: Int, epochMilli: Long): UUID = {
    val counter: Int = nextCounter(epochMilli)
    val random: Long = ThreadLocalRandom.current().nextLong(UnsignedSixByteMax + 1)
    apply(epochMilli, counter, nodeId, random)
  }

  def apply(epochMilli: Long, counter: Int, nodeId: Int, random: Long): UUID = {
    checkUnsignedSixByteRange("epochMilli", epochMilli)
    checkUnsignedShortRange("counter", counter)
    checkSignedShortRange("nodeId", nodeId)
    checkUnsignedSixByteRange("random", random)
    
    val timeAndCounter: Long = (epochMilli << 16) | (counter & 0xffffL)
    val nodeIdAndRandom: Long = (nodeId.toLong << 48) | (random & 0xffffffffffffL)
    
    UUID(timeAndCounter, nodeIdAndRandom)
  }

  def apply(bytes: ImmutableArray[Byte]): UUID = apply(bytes.toArray)

  def apply(bytes: Array[Byte]): UUID = {
    require(bytes.length == 16, "Not a UUID - Invalid Byte Array Length")
    val buf: ByteBuffer = ByteBuffer.wrap(bytes)
    UUID(buf.getLong, buf.getLong)
  }
  
  def apply(uuid: java.util.UUID): UUID = apply(uuid.getMostSignificantBits, uuid.getLeastSignificantBits)

  def apply(uuid: BigInt): UUID = apply(uuid.bigInteger)

  def apply(uuid: BigInteger): UUID = {
    val bytes: Array[Byte] = uuid.toByteArray()
    require(bytes.length <= 16, "Not a UUID - Invalid Byte Array Length")

    // If we have less than 16 bytes then we need to extend the byte array to be 16 bytes
    val newBytes: Array[Byte] = if (bytes.length < 16) {
      val tmp: Array[Byte] = new Array(16)

      // If the BigInteger is negative then we need to fill in -1s in our array otherwise we use the default 0s
      if (uuid.isNegative) Arrays.fill(tmp, -1.toByte)

      System.arraycopy(bytes, 0, tmp, 16 - bytes.length, bytes.length)
      tmp
    } else {
      bytes
    }

    apply(newBytes)
  }
  
  def apply(uuid: String): UUID = {
    // Note: If the UUID looks like Base58 then we go with that.  In some cases this will conflict with Base64 encoded
    //       with no padding.  Specifically when length in 22 and all characters look like valid Base58 we will treat
    //       it as Base58 and not as Base64.  I'm tempted to remove any Base64 references in UUID to discourage use of
    //       it as a way to serialize the UUID.
    if (mightBeBase58(uuid)) return apply(Base58.decode(uuid))

    uuid.length match {
      // Base 64: AVJHfgdafGqJBjASSLG0GQ==, AVJHfgdafGqJBjASSLG0GQ=, AVJHfgdafGqJBjASSLG0GQ
      case 22 | 23 | 24 =>
        apply(Base64.decode(uuid))
      
      // Hex: 0152477e075a7c6a8906301248b1b419
      case 32 =>
        apply(Base16.decode(uuid))
      
      // "Pretty" Hex: 0152477e075a-7c6a-8906-301248b1b419
      case 35 =>
        Seq(12, 17, 22).foreach{ (idx: Int) => require(!Character.isLetterOrDigit(uuid(idx)), s"Not a valid UUID: $uuid") }
        
        val epochMillis: Long = java.lang.Long.parseLong(uuid.substring(0, 12), 16)
        val counter: Int = Integer.parseInt(uuid.substring(13, 17), 16)
        val nodeId: Int = Integer.parseInt(uuid.substring(18, 22), 16) << 16 >> 16 // Some shifting to restore the original sign
        val random: Long = java.lang.Long.parseLong(uuid.substring(23, 35), 16)
        apply(epochMillis, counter, nodeId, random)
      
      // "Standard" formatted UUID: 0152477e-075a-7c6a-8906-301248b1b419
      case 36 =>
        Seq(8, 13, 18, 23).foreach{ (idx: Int) => require(!Character.isLetterOrDigit(uuid(idx)), s"Not a valid UUID: $uuid") }
        
        val epochMillis: Long = java.lang.Long.parseLong(uuid.substring(0, 8)+uuid.substring(9, 13), 16)
        val counter: Int = Integer.parseInt(uuid.substring(14, 18), 16)
        val nodeId: Int = Integer.parseInt(uuid.substring(19, 23), 16) << 16 >> 16 // Some shifting to restore the original sign
        val random: Long = java.lang.Long.parseLong(uuid.substring(24, 36), 16)
        apply(epochMillis, counter, nodeId, random)
        
      case _ => throw new IllegalArgumentException("Invalid UUID")
    }
  }

  private def mightBeBase58(uuid: String): Boolean = !isNotBase58(uuid)

  private def isNotBase58(uuid: String): Boolean = {
    if (uuid.length < 11 || uuid.length > 22) return true

    var i: Int = 0

    while (i < uuid.length) {
      val hasIllegalChar: Boolean = uuid.charAt(i) match {
        case '0' | 'O' | 'I' | 'l' => true // Alpha Chars omitted from Base58
        case '/' | '+' | '_' | '-' => true // Special Chars omitted from Base58
        case '='                   => true // Padding char (not used in Base 58)
        case _                     => false
      }

      if (hasIllegalChar) return true

      i += 1
    }

    false
  }
  
  /**
   * Can use this in an extractor:
   * val Array(UUID.parse(first), UUID.parse(second)) = s.split(':')
   */
  object parse {
    def apply(uuid: String): Option[UUID] = get(uuid)
    def unapply(uuid: String): Option[UUID] = get(uuid)
  }
  
  def get(uuid: String): Option[UUID] = Try{ apply(uuid) }.toOption

  def get(bytes: ImmutableArray[Byte]): Option[UUID] = {
    if (bytes.length == 16) Some(apply(bytes.toArray)) else None
  }

  def get(bytes: Array[Byte]): Option[UUID] = {
    if (bytes.length == 16) Some(apply(bytes)) else None
  }
  
  def isValid(uuid: String): Boolean = get(uuid).isDefined
  
  private def checkUnsignedSixByteRange(name: String, value: Long): Unit = {
    if (value < UnsignedSixByteMin || value > UnsignedSixByteMax) throw new IllegalArgumentException(name+": '"+value+"' is outside of the valid range which should be between "+UnsignedSixByteMin+" and "+UnsignedSixByteMax)
  }
  
  private def checkUnsignedShortRange(name: String, value: Int): Unit = {
    if (value < UnsignedShortMin || value > UnsignedShortMax) throw new IllegalArgumentException(name+": '"+value+"' is outside of the valid range which should be between "+UnsignedShortMin+" and "+UnsignedShortMax)
  }
  
  private def checkSignedShortRange(name: String, value: Int): Unit = {
    if (value < Short.MinValue || value > Short.MaxValue) throw new IllegalArgumentException(name+": '"+value+"' is outside of the valid range which should be between "+Short.MinValue+" and "+Short.MaxValue)
  }
  
//  /**
//   * A non-scientific super simple performance tester
//   */
//  def main(args: Array[String]): Unit = Util.printAppStats{
//    val doPrettyString: Boolean = args.headOption.flatMap{ _.parseBoolean }.getOrElse{ false }
//
//    {
//      var i: Int = 0
//      var tmp: Int = 0
//      while (i < 1000000) {
//        val uuid: UUID = UUID()
//        tmp += uuid.counter
//        if (doPrettyString) tmp += uuid.toPrettyString().length
//        i += 1
//      }
//      println("Warming Complete: "+tmp)
//    }
//
//    import java.util.concurrent.CountDownLatch
//
//    val threads: Int = 8
//    val iterationsPerThread: Int = 10000000
//    val latch: CountDownLatch = new CountDownLatch(threads)
//
//    val runner: TaskRunner = TaskRunner("UUID Tester", threads = threads)
//
//    val millis: Long = Util.time{
//      (0 until threads).foreach{ i =>
//        runner.submit{
//          var sum: Int = 0
//          var i: Int = 0
//          while(i < iterationsPerThread) {
//            val uuid: UUID = UUID()
//            sum += uuid.counter
//            if (doPrettyString) sum += uuid.toPrettyString().length
//            i += 1
//          }
//          latch.countDown()
//          println(s"$i - Sum: $sum")
//        }
//      }
//
//      latch.await()
//    }
//
//    val totalUUIDs: Int = iterationsPerThread*threads
//    println(s"Total Time: ${millis}ms, total UUIDs Created: ${totalUUIDs}, per ms: ${totalUUIDs/millis}")
//
//  }
}

/**
 * A custom UUID implementation (not to be confused with java.util.UUID or RFC4122 implementations)
 * that allows natural sorting by timestamp based on the string or numeric representation.
 * 
 * The UUID consists of 16 bytes (128 bits) broken up into 2 longs:
 * 
 * timeAndCounter: {6-Byte Millis since epoch}{2-Byte Counter}
 * nodeIdAndRandom: {2-Byte Node ID}{6-Byte Random Number}
 * 
 * The "pretty" hex encoded representation is:
 * 	{6-byte millis since epoch}-{2-byte-counter}-{2-byte-optional-node-id}-{4-byte-random}
 * 
 * Example: 015247f01787-9740-85e0-3e9672a8dfa2
 */
final case class UUID(timeAndCounter: Long, nodeIdAndRandom: Long) extends Ordered[UUID] {
  /** Between 0 and 281474976710655 (both inclusive) which is a 6-byte unsigned int */
  def epochMilli: Long = timeAndCounter >>> 16
  
  /** Between 0 and 65535 (both inclusive) */
  def counter: Int = (timeAndCounter & 0xffffL).toInt
  
  /** Between Short.MinValue (-32768) and Short.MaxValue (32767) (both inclusive) */
  def nodeId: Int = (nodeIdAndRandom >> 48).toInt
  
  /** Between 0 and 281474976710655 (both inclusive) which is a 6-byte unsigned int */
  def random: Long = nodeIdAndRandom & 0xffffffffffffL
  
  /** The java.time.Instant represented by the epochMilli */
  def instant: Instant = InstantCompat.ofEpochMilli(epochMilli)

  /** The fm.common.ImmutableDate represented by the epochMilli */
  def date: ImmutableDate = new ImmutableDate(epochMilli)

  /** The java.util.Date represented by the epochMilli */
  def javaDate: Date = new Date(epochMilli)

  /** Is this UUID using a random node id? */
  def isRandomNodeId: Boolean = nodeId < 0

  def toImmutableByteArray(): ImmutableArray[Byte] = {
    ImmutableArray.unsafeWrapArray(toByteArray())
  }

  def toByteArray(): Array[Byte] = {
    val buf: ByteBuffer = ByteBuffer.allocate(16)
    buf.putLong(timeAndCounter)
    buf.putLong(nodeIdAndRandom)
    buf.array()
  }
  
  def toBigInteger: BigInteger = new BigInteger(toByteArray())
  def toBigInt: BigInt = new BigInt(toBigInteger)
  
  def toHex: String = Base16.encode(toByteArray())
  def toBase16: String = Base16.encode(toByteArray())

  def toBase58: String = Base58.encode(toByteArray())

  def toBase64: String = Base64Strict.encode(toByteArray())
  def toBase64NoPadding: String = Base64Strict.encodeNoPadding(toByteArray())
  def toBase64URL: String = Base64URL.encode(toByteArray())
  def toBase64URLNoPadding: String = Base64URL.encodeNoPadding(toByteArray())
  
  /** {6-byte millis since epoch}-{2-byte-counter}-{2-byte-optional-node-id}-{4-byte-random} */
  def toPrettyString(): String = toPrettyString('-')
  
  /** {6-byte millis since epoch}{sep}{2-byte-counter}{sep}{2-byte-optional-node-id}{sep}{4-byte-random} */
  def toPrettyString(sep: Char): String = {
    val bytes: Array[Byte] = toByteArray()
    
    val sb: StringBuilder = new StringBuilder(35)
    
    sb.append(Base16.encode(bytes, 0, 6))
    sb.append(sep)
    sb.append(Base16.encode(bytes, 6, 2))
    sb.append(sep)
    sb.append(Base16.encode(bytes, 8, 2))
    sb.append(sep)
    sb.append(Base16.encode(bytes, 10, 6))
    
    sb.toString()
  }
  
  /** {upper 4-bytes of 6-byte millis since epoch}-{lower 2-bytes of 6-byte millis since epoch}-{2-byte-counter}-{2-byte-optional-node-id}-{4-byte-random} */
  def toStandardString(): String = toStandardString('-')
  
  /** {upper 4-bytes of 6-byte millis since epoch}{sep}{lower 2-bytes of 6-byte millis since epoch}{sep}{2-byte-counter}{sep}{2-byte-optional-node-id}{sep}{4-byte-random} */
  def toStandardString(sep: Char): String = {
    val bytes: Array[Byte] = toByteArray()
    
    val sb: StringBuilder = new StringBuilder(36)
    
    sb.append(Base16.encode(bytes, 0, 4))
    sb.append(sep)
    sb.append(Base16.encode(bytes, 4, 2))
    sb.append(sep)
    sb.append(Base16.encode(bytes, 6, 2))
    sb.append(sep)
    sb.append(Base16.encode(bytes, 8, 2))
    sb.append(sep)
    sb.append(Base16.encode(bytes, 10, 6))
    
    sb.toString()
  }
  
  override def toString(): String = toPrettyString()
  
  def compare(that: UUID): Int = {
    val res: Int = java.lang.Long.compare(this.timeAndCounter, that.timeAndCounter)
    if (res == 0) java.lang.Long.compare(this.nodeIdAndRandom, that.nodeIdAndRandom) else res
  }
  
  def toJavaUUID: java.util.UUID = new java.util.UUID(timeAndCounter, nodeIdAndRandom)

  def isZero: Boolean = timeAndCounter === 0L && nodeIdAndRandom === 0L
  def isUnsignedMaxValue: Boolean = timeAndCounter === -1L && nodeIdAndRandom === -1L
  def isSignedMinValue: Boolean = timeAndCounter === Long.MinValue && nodeIdAndRandom === Long.MinValue
  def isSignedMaxValue: Boolean = timeAndCounter === Long.MaxValue && nodeIdAndRandom === Long.MaxValue
}