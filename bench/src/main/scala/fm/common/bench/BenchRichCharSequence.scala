package fm.common.bench

import fm.common.Implicits._
import org.openjdk.jmh.annotations.Benchmark

class BenchRichCharSequence {

  //
  // 1
  //

  private def exampleString1: String = " a B c D e F g H i J k L m N o P"
  private def exampleString1Target: String = "   defghij    "

  @Benchmark
  def containsNormalizedManual1: Boolean = {
    exampleString1.lowerAlphaNumeric.contains(exampleString1Target.lowerAlphaNumeric)
  }

  @Benchmark
  def containsNormalizedOptimized1: Boolean = {
    exampleString1.containsNormalized(exampleString1Target)
  }

  //
  // 2
  //

  private def exampleString2: String = "soMeStRiNgExAcTfOoBaR"
  private def exampleString2Target: String = "eXaCt"

  @Benchmark
  def containsNormalizedManual2: Boolean = {
    exampleString2.lowerAlphaNumeric.contains(exampleString2Target.lowerAlphaNumeric)
  }

  @Benchmark
  def containsNormalizedOptimized2: Boolean = {
    exampleString2.containsNormalized(exampleString2Target)
  }

  //
  // 3
  //

  private def exampleString3: String = "ExAcT"
  private def exampleString3Target: String = "eXaCt"

  @Benchmark
  def containsNormalizedManual3: Boolean = {
    exampleString3.lowerAlphaNumeric.contains(exampleString3Target.lowerAlphaNumeric)
  }

  @Benchmark
  def containsNormalizedOptimized3: Boolean = {
    exampleString3.containsNormalized(exampleString3Target)
  }
}