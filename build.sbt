name := "kafka-demo"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-stream-kafka" % "0.14",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0")
