package fm.common

import java.lang.StringBuilder
import java.math.BigInteger
import java.nio.ByteBuffer
import java.time.Instant
import java.util.Date
import java.util.concurrent.ThreadLocalRandom
import scala.math.Ordering
import scala.util.Try

object UUID {
  private[this] val UnsignedSixByteMin: Long = 0L
  private[this] val UnsignedSixByteMax: Long = 0xffffffffffffL // We only use 6 bytes for the timestamp and random fields
  
  private[this] val UnsignedShortMin: Int = 0
  private[this] val UnsignedShortMax: Int = 65535 // Short.MaxValue - Short.MinValue
  
  private[this] val SignedShortMax: Int = 32768
  
  private val Base16: BaseEncoding = BaseEncoding.Base16LowerCase
  private val Base32: BaseEncoding = BaseEncoding.Base32LowerCase
  private val Base64: BaseEncoding = BaseEncoding.Base64
  private val Base64URL: BaseEncoding = BaseEncoding.Base64Url
  
  private[this] val counter = new java.util.concurrent.atomic.AtomicInteger(ThreadLocalRandom.current().nextInt)
  
  private def nextCounter(epochMilli: Long): Int = counter.getAndIncrement() & 0xffff
  
  def apply(): UUID = {
    // No Node Id Specified so we use a random negative Short
    
    // Get a random int between 0 (inclusive) and 32768 (exclusive)
    val randomInt: Int = ThreadLocalRandom.current().nextInt(SignedShortMax)
    
    // Add one (we don't want zero as a value) and make it negative.
    // This should give us a number between -32768 (inclusive) and -1 (inclusive) 
    // which corresponds to the range of all negative Short values
    val randomNodeId: Int = (randomInt + 1) * -1
    
    make(randomNodeId)
  }
  
  def apply(nodeId: Int): UUID = {
    if (nodeId < 0 || nodeId > 32767) throw new IllegalArgumentException("Invalid NodeId: '"+nodeId+"'. NodeId must be between 0 and 32767 (inclusive).")
    make(nodeId)
  }
  
  private def make(nodeId: Int): UUID = {
    val epochMilli: Long = System.currentTimeMillis()
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
  
  def apply(bytes: Array[Byte]): UUID = {
    require(bytes.length == 16, "Not a UUID - Invalid Byte Array Length")
    val buf: ByteBuffer = ByteBuffer.wrap(bytes)
    UUID(buf.getLong, buf.getLong)
  }
  
  def apply(uuid: BigInteger): UUID = apply(uuid.toByteArray)
  
  def apply(uuid: String): UUID = {
    uuid.length match {
      // Base 64: AVJHfgdafGqJBjASSLG0GQ==, AVJHfgdafGqJBjASSLG0GQ=, AVJHfgdafGqJBjASSLG0GQ
      case 22 | 23 | 24 =>
        val urlSafe: Boolean = uuid.contains('-') || uuid.contains('_')
        apply(if (urlSafe) Base64URL.decode(uuid) else Base64.decode(uuid))
      
      // Hex: 0152477e075a7c6a8906301248b1b419
      case 32 =>
        apply(Base16.decode(uuid))
      
      // "Pretty" Hex: 0152477e075a-7c6a-8906-301248b1b419
      case 35 => 
        val epochMillis: Long = java.lang.Long.parseLong(uuid.substring(0, 12), 16)
        val counter: Int = Integer.parseInt(uuid.substring(13, 17), 16)
        val nodeId: Int = Integer.parseInt(uuid.substring(18, 22), 16) << 16 >> 16 // Some shifting to restore the original sign
        val random: Long = java.lang.Long.parseLong(uuid.substring(23, 35), 16)
        apply(epochMillis, counter, nodeId, random)
        
      case _ => throw new IllegalArgumentException("Invalid UUID")
    }
  }
  
  def get(uuid: String): Option[UUID] = Try{ apply(uuid) }.toOption
  
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
  
  /**
   * A non-scientific super simple performance tester
   */
  def main(args: Array[String]): Unit = Util.printAppStats{
    (0 until 1000000).foreach{ i => UUID() }
    
    import java.util.concurrent.CountDownLatch
    
    val threads: Int = 8
    val iterationsPerThread: Int = 10000000
    val latch: CountDownLatch = new CountDownLatch(threads)
    
    val runner: TaskRunner = TaskRunner("UUID Tester", threads = threads)
    
    val millis: Long = Util.time{
      (0 until threads).foreach{ i =>
        runner.submit{
          var sum: Int = 0
          var i: Int = 0
          while(i < iterationsPerThread) {
            sum += UUID().counter
            i += 1
          }
          latch.countDown()
          println(s"$i - Sum: $sum")
        }
      }
      
      latch.await()
    }
    
    val totalUUIDs: Int = iterationsPerThread*threads
    println(s"Total Time: ${millis}ms, total UUIDs Created: ${totalUUIDs}, per ms: ${totalUUIDs/millis}")
    
  }
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
  import UUID.{Base16, Base32, Base64, Base64URL}
  
  /** Between 0 and 281474976710655 (both inclusive) which is a 6-byte unsigned int */
  def epochMilli: Long = timeAndCounter >>> 16
  
  /** Between 0 and 65535 (both inclusive) */
  def counter: Int = (timeAndCounter & 0xffffL).toInt
  
  /** Between Short.MinValue (-32768) and Short.MaxValue (32767) (both inclusive) */
  def nodeId: Int = (nodeIdAndRandom >> 48).toInt
  
  /** Between 0 and 281474976710655 (both inclusive) which is a 6-byte unsigned int */
  def random: Long = nodeIdAndRandom & 0xffffffffffffL
  
  /** The java.time.Instant represented by the epochMilli */
  def instant: Instant = Instant.ofEpochMilli(epochMilli)
  
  /** The java.util.Date represented by the epochMilli */
  def date: Date = new Date(epochMilli)
  
  /** Is this UUID using a random node id? */
  def isRandomNodeId: Boolean = nodeId < 0
  
  def toByteArray(): Array[Byte] = {
    val buf: ByteBuffer = ByteBuffer.allocate(16)
    buf.putLong(timeAndCounter)
    buf.putLong(nodeIdAndRandom)
    buf.array()
  }
  
  def toBigInteger: BigInteger = new BigInteger(toByteArray)
  def toBigInt: BigInt = new BigInt(toBigInteger)
  
  def toHex(): String = Base16.encode(toByteArray)
  
  def toBase64(): String = toBase64(false)
  def toBase64URLSafe(): String = toBase64(true)
  def toBase64(urlSafe: Boolean): String = if (urlSafe) Base64URL.encode(toByteArray) else Base64.encode(toByteArray) //Base64.encode(toByteArray, if (urlSafe) Base64.URL_SAFE else 0)
  
  /** {6-byte millis since epoch}-{2-byte-counter}-{2-byte-optional-node-id}-{4-byte-random} */
  def toPrettyString(): String = {
    val bytes: Array[Byte] = toByteArray()
    
    val sb: StringBuilder = new StringBuilder(35)
    
    sb.append(Base16.encode(bytes, 0, 6))
    sb.append("-")
    sb.append(Base16.encode(bytes, 6, 2))
    sb.append("-")
    sb.append(Base16.encode(bytes, 8, 2))
    sb.append("-")
    sb.append(Base16.encode(bytes, 10, 6))
    
    sb.toString()
  }
  
  override def toString(): String = toPrettyString()
  
  def compare(that: UUID): Int = {
    val res: Int = java.lang.Long.compare(this.timeAndCounter, that.timeAndCounter)
    if (res == 0) java.lang.Long.compare(this.nodeIdAndRandom, that.nodeIdAndRandom) else res
  }
}