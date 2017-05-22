import sbt.Keys._

val projectScalaVersion = "2.11.8"

val scalaVersions = Seq("2.11.8", "2.12.2")

resolvers in Global += "RustyRaven" at "http://rustyraven.github.io"

resolvers in Global += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

val codebookLibrary = Seq("com.rustyraven" %% "codebook-runtime" % "1.2-SNAPSHOT")

val spannerClientLibraries = Seq(
  "com.google.cloud" % "google-cloud-spanner" % "0.17.2-beta",
  "com.google.auth" % "google-auth-library-oauth2-http" % "0.6.0",
  "com.google.guava" % "guava" % "21.0"
) 

val loggingLibraries = Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.7"
)

val scoptLibrary = Seq("com.github.scopt" %% "scopt" % "3.4.0")

val testLibraries = Seq(
  "org.specs2" %% "specs2-core" % "3.6.2" % "test",
  "org.specs2" %% "specs2-mock" % "3.6.2" % "test",
  "com.typesafe" % "config" % "1.3.1" % "test"
)

val commonLibraries = Seq(
  "joda-time" % "joda-time" % "2.9.6",
  "org.joda" % "joda-convert" % "1.8"
)

val jlineLibrary = Seq("jline" % "jline" % "2.14.3")

parallelExecution in ThisBuild := false

val projectVersion = "0.6-SNAPSHOT"

val noJavaDoc = Seq(
  publishArtifact in (Compile, packageDoc) := false,
  publishArtifact in packageDoc := false,
  publishArtifact in packageSrc := false,
  sources in (Compile,doc) := Seq.empty
)

lazy val core = (project in file("."))
  .settings(antlr4Settings : _*)
  .settings(
    scalaVersion := projectScalaVersion,
    crossScalaVersions := scalaVersions,
    name := "bolt",
    organization := "com.sopranoworks",
    version := projectVersion,
    publishTo := Some(Resolver.file("bolt",file("../RustyRaven.github.io"))(Patterns(true, Resolver.mavenStyleBasePattern))),
    antlr4PackageName in Antlr4 := Some("com.sopranoworks.bolt"),
    libraryDependencies ++=
      spannerClientLibraries ++
      codebookLibrary ++
      loggingLibraries ++
      testLibraries ++
      commonLibraries,
    dependencyOverrides += "io.netty" % "netty-tcnative-boringssl-static" % "1.1.33.Fork22"
  )
  .settings(noJavaDoc: _*)

lazy val client = (project in file("client"))
  .enablePlugins(JavaAppPackaging,UniversalPlugin)
  .settings(
    scalaVersion := projectScalaVersion,
    name := "spanner-cli",
    version := projectVersion,
    //mappings in Universal in packageBin += file("Readme.md") -> "Readme.md",
    bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/application.conf"""",
    batScriptExtraDefines += """set _JAVA_OPTS=%_JAVA_OPTS% -Dconfig.file=%EXAMPLE_CLI_HOME%\\conf\\application.conf""",
    libraryDependencies ++= scoptLibrary ++ jlineLibrary
  ).dependsOn(core)
  .settings(noJavaDoc: _*)
