FMProguardSettings

ProguardKeys.options in Proguard ++= Seq(
  "-dontoptimize",
  "-dontusemixedcaseclassnames", // Don't write out i.class and I.class (which won't unjar properly on case-insensitive file systems like on OSX)
  "-keep class fm.** { *; }",
  "-repackageclasses 'fm.common.libs'",
  "-keepattributes",
  "-keepparameternames",
  "-dontnote org.apache.commons.lang3.ObjectUtils",
  "-dontnote org.apache.commons.io.LineIterator",
  "-dontwarn com.ctc.wstx.**"
)

ProguardKeys.defaultInputFilter in Proguard := Some("!META-INF/**,!org/apache/commons/codec/language/bm/*.txt,!javax/**")

// Some of the Apache libs need javax.crypto
ProguardKeys.libraries in Proguard += new File(System.getProperty("java.home"), "lib/jce.jar")
