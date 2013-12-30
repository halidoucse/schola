// The Typesafe repository
//resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

organization := "schola.oadmin"

name := "oadmin"

version := "0.0.1"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javaOptions ++= Seq("-Dsun.net.inetaddr.ttl=30")

ideaExcludeFolders += ".idea"

ideaExcludeFolders += ".idea_modules"

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "2.0.0-RC1",
  "com.typesafe" % "config" % "1.0.2"
)

libraryDependencies ++= List(
  // "com.h2database" % "h2" % "1.3.166",
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc41",
  "c3p0" % "c3p0" % "0.9.1.2"
)
 
libraryDependencies ++= List(
  "net.databinder" %% "unfiltered-oauth2" % "0.7.1",
  //"net.databinder" %% "unfiltered-directives" % "0.7.1",
  "net.databinder" %% "unfiltered-spec" % "0.7.1" % "test",
  "net.databinder" %% "unfiltered-filter-async" % "0.7.1",
  "net.databinder" %% "unfiltered-filter-uploads" % "0.7.1",
  "net.databinder" %% "unfiltered-json4s" % "0.7.1"
)

libraryDependencies ++= List(
  "io.webcrank" %% "webcrank-password" % "0.3",
  "org.bouncycastle" % "bcprov-jdk14" % "1.49"
)

libraryDependencies ++= List(
  "org.mongodb" % "mongo-java-driver" % "1.3"
)

libraryDependencies ++= List(
  "com.typesafe.akka" %% "akka-actor" % "2.3-M1",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3-M1"
)

libraryDependencies ++= List(
  "org.fusesource.scalate" %% "scalate-core" % "1.6.1"
)

libraryDependencies ++= List(
 // "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.clapper" %% "avsl" % "1.0.1"
)

libraryDependencies ++= List(
  "commons-validator" % "commons-validator" % "1.4.0",
  "org.apache.commons" % "commons-lang3" % "3.1"
)

libraryDependencies ++= List(
  "net.jpountz.lz4" % "lz4" % "1.2.0",
  "net.spy" % "spymemcached" % "2.10.3"
)

libraryDependencies ++= List(
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  "net.databinder.dispatch" %% "dispatch-json4s-native" % "0.11.0"
)

libraryDependencies ++= List(
 // "org.mockito" % "mockito-core" % "1.9.5" % "test"
 // "com.typesafe.play" %% "templates" % "2.2.1"
)

libraryDependencies ++= List(
  "com.github.scala-incubator.io" %% "scala-io-file"  % "0.4.2"
)

