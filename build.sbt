ThisBuild / scalaVersion := "3.2.2"

ThisBuild / crossScalaVersions := Seq("3.2.2", "2.13.10", "2.12.17", "2.11.12")

ThisBuild / versionScheme := Some("early-semver")

val fatalWarnings = Seq(
  // Enable -Xlint, but disable the default 'unused' so we can manually specify below
  "-Xlint:-unused",
  // Remove "params" since we often have method signatures that intentionally have the parameters, but may not be used in every implementation, also omit "patvars" since it isn't part of the default xlint:unused and isn't super helpful
  "-Ywarn-unused:imports,privates,locals",
  // Warnings become Errors
  //"-Xfatal-warnings", // NOT ENABLED YET: EnumMacros.scala:87:31: method enclosingClass in trait Enclosures is deprecated (since 2.11.0)
)

def sharedSettings(crossType: CrossType = CrossType.Full) = Seq(
  // Due to Scala Standard Library changes in 2.13 some code is specific to
  // Scala 2.12 and below (e.g. 2.11, 2.12) and some code is specific to 2.13
  // and higher (e.g. 2.13, 3.0).
  Compile / unmanagedSourceDirectories ++= {   
    val platformSrcDir: Option[sbt.File] = Some((Compile / sourceDirectory).value)
    
    val sharedSrcDir: Option[sbt.File] = crossType match {
      case CrossType.Full => CrossType.Full.sharedSrcDir(baseDirectory.value, "main").map{ _.getParentFile }
      case _ => None
    }
    
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n < 13 => List(platformSrcDir, sharedSrcDir).flatten.map{ _ / "scala-2.12-" }
      case _ => List(platformSrcDir, sharedSrcDir).flatten.map{ _ / "scala-2.13+" }
    }
  },
  scalacOptions += "-language:implicitConversions",
  libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.15" % "provided,test",
)

val publishSettings = Seq(
  publishTo := sonatypePublishToBundle.value
)

val noPublishSettings = Seq(
  publish / skip := true
)

lazy val `fm-common` = project.in(file("."))
  .aggregate(
    `fm-common-core-macros`.jvm, `fm-common-core-macros`.js,
    `fm-common-core`.jvm, `fm-common-core`.js,
    `fm-common-full`.jvm, `fm-common-full`.js,
  )
  .settings(noPublishSettings)

//lazy val `fm-common-core-macros` = project.in(file("fm-common-core-macros")).
lazy val `fm-common-core-macros` = crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure).in(file("fm-common-core-macros"))
  .settings(noPublishSettings ++ sharedSettings(CrossType.Pure) ++ Seq(
    libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
      // For Scala 2.x we need a dependency on the Scala Compiler for the Macros to work
      case Some((2, _)) => List("org.scala-lang" % "scala-compiler" % scalaVersion.value)
      case _            => Nil
    }
  )))

// In fm-common-core we will mark these all as "optional" but in fm-common-full we will leave as-is
def jvmDependenciesSetting(configurations: Option[String]) = libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.3.5",
  "com.github.ben-manes.caffeine" % "caffeine" % "3.1.4",
  "com.github.luben" % "zstd-jni" % "1.5.4-2",
  "com.google.guava" % "guava" % "31.1-jre",
  "com.googlecode.juniversalchardet" % "juniversalchardet" % "1.0.3",
  "com.fasterxml.woodstox" % "woodstox-core" % "6.5.0",
  "commons-io" % "commons-io" % "2.11.0",
  "it.unimi.dsi" % "fastutil" % "8.5.11",
  "org.apache.commons" % "commons-compress" % "1.22",
  "org.apache.commons" % "commons-text" % "1.10.0",
  "org.slf4j" % "slf4j-api" % "2.0.6",
  "org.tukaani" % "xz" % "1.9",  // Used by commons-compress and should be synced up with whatever version commons-compress requires
  "org.xerial.snappy" % "snappy-java" % "1.1.8.4",
).map{ _.withConfigurations(configurations) }

def jsDependenciesSetting(configurations: Option[String]) = libraryDependencies ++= Seq(
  "io.github.cquiroz" %%% "scala-java-time" % "2.3.0",
  "org.scala-js" %%% "scalajs-dom" % "2.1.0",
).map{ _.withConfigurations(configurations) }

// This contains all of the dependency-free (mostly) classes
lazy val `fm-common-core` = crossProject(JSPlatform, JVMPlatform).in(file("fm-common-core"))
  .dependsOn(`fm-common-core-macros` % "compile-internal, test-internal")
  .settings(publishSettings)
  .settings(sharedSettings())
  .settings(
    name := "fm-common-core",
    description := "Common Scala classes that we use at Frugal Mechanic / Eluvio",
  ).jvmSettings(
    jvmDependenciesSetting(Some("optional")),
    // include the macro classes and resources in the main jar
    Compile / packageBin / mappings ++= { `fm-common-core-macros`.jvm / Compile / packageBin / mappings }.value,
    // include the macro sources in the main source jar
    Compile / packageSrc / mappings ++= { `fm-common-core-macros`.jvm / Compile / packageSrc / mappings }.value,
  ).jsSettings(
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),
    jsDependenciesSetting(Some("optional")),
    // include the macro classes and resources in the main jar
    Compile / packageBin / mappings ++= { `fm-common-core-macros`.js / Compile / packageBin / mappings }.value,
    // include the macro sources in the main source jar
    Compile / packageSrc / mappings ++= { `fm-common-core-macros`.js / Compile / packageSrc / mappings }.value,
  )

lazy val `fm-common-full` = crossProject(JSPlatform, JVMPlatform).in(file("fm-common"))
  .dependsOn(`fm-common-core`)
  .settings(publishSettings)
  .settings(
    name := "fm-common",
    // Only publish the .pom file. Don't publish jar, sources or docs
    Compile / packageBin / publishArtifact := false,
    Compile / packageSrc / publishArtifact := false,
    Compile / packageDoc / publishArtifact := false,
  )
  .jvmSettings(jvmDependenciesSetting(None))
  .jsSettings(jsDependenciesSetting(None))
