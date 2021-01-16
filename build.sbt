//
// Note: fm-common is setup to cross build with Scala.js
//

scalaVersion in ThisBuild := "2.12.13"

crossScalaVersions in ThisBuild := Seq("2.11.12", "2.12.13")

val fatalWarnings = Seq(
  // Enable -Xlint, but disable the default 'unused' so we can manually specify below
  "-Xlint:-unused",
  // Remove "params" since we often have method signatures that intentionally have the parameters, but may not be used in every implementation, also omit "patvars" since it isn't part of the default xlint:unused and isn't super helpful
  "-Ywarn-unused:imports,privates,locals",
  // Warnings become Errors
  //"-Xfatal-warnings", // NOT ENABLED YET: EnumMacros.scala:87:31: method enclosingClass in trait Enclosures is deprecated (since 2.11.0)
)

lazy val `fm-common` = project.in(file(".")).
  aggregate(fmCommonJS, fmCommonJVM, `fm-common-bench`).
  settings(FMPublic ++ Seq(
    publish := {},
    publishLocal := {},
    publishArtifact := false,
    publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo"))), // http://stackoverflow.com/a/18522706
    releaseCrossBuild := true // Make sure sbt-release performs cross builds
  ))
  
lazy val `fm-common-macros` = project.in(file("macro")).settings(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo"))), // http://stackoverflow.com/a/18522706
  libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value
)

lazy val `fm-common-` = crossProject.in(file(".")).
  settings((FMPublic ++ Seq( // Note: FMPublic needs to be here for sbt-release to work
    name := "fm-common",
    description := "Common Scala classes that we use at Frugal Mechanic / Eluvio",
    scalacOptions := Seq(
      "-unchecked",
      "-deprecation",
      "-language:implicitConversions",
      "-feature",
      "-Xlint",
      "-Ywarn-unused-import"
    ) ++ (if (scalaVersion.value.startsWith("2.12")) Seq(
      // Scala 2.12 specific compiler flags
      "-opt:l:inline",
      "-opt-inline-from:<sources>"
    ) else Nil) ++ (if (scalaVersion.value.startsWith("2.12")) fatalWarnings else Nil),
    
    // -Ywarn-unused-import/-Xfatal-warnings casues issues in the REPL and also during doc generation
    scalacOptions in (Compile, console) --= fatalWarnings,
    scalacOptions in (Test, console) --= fatalWarnings,
    scalacOptions in (Compile, doc) --= fatalWarnings,
    
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    // include the macro classes and resources in the main jar
    mappings in (Compile, packageBin) ++= { mappings in (`fm-common-macros`, Compile, packageBin) }.value,
    // include the macro sources in the main source jar
    mappings in (Compile, packageSrc) ++= { mappings in (`fm-common-macros`, Compile, packageSrc) }.value,
    
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.5" % "provided,test"
  )):_*).
  jvmSettings(Seq(
    // Add JVM-specific settings here
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.google.guava" % "guava" % "28.0-jre",
      "com.googlecode.juniversalchardet" % "juniversalchardet" % "1.0.3",
      "com.sun.mail" % "javax.mail" % "1.5.2" % "provided",
      "com.fasterxml.woodstox" % "woodstox-core" % "5.1.0",
      "commons-io" % "commons-io" % "2.6",
      "it.unimi.dsi" % "fastutil" % "8.2.2",
      "org.apache.commons" % "commons-compress" % "1.18",
      "org.apache.commons" % "commons-lang3" % "3.8.1",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "org.tukaani" % "xz" % "1.8",  // Used by commons-compress and should be synced up with whatever version commons-compress requires
      "org.xerial.snappy" % "snappy-java" % "1.1.8.2"
    )
  ):_*).
  jsSettings(
    // Add JS-specific settings here
    libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.9.4",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.6",
    libraryDependencies += "org.scala-js" %%% "scalajs-java-time" % "0.2.5",
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),
  )
  
lazy val `fm-common-bench` = project.in(file("bench")).settings(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo"))), // http://stackoverflow.com/a/18522706  
).enablePlugins(JmhPlugin).dependsOn(fmCommonJVM, `fm-common-macros`)

lazy val fmCommonJVM = `fm-common-`.jvm.dependsOn(`fm-common-macros` % "compile-internal, test-internal")
lazy val fmCommonJS = `fm-common-`.js.dependsOn(`fm-common-macros` % "compile-internal, test-internal")
