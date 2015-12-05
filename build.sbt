name := """etcd-store-guice"""

organization := "com.lvxingpai"

version := "0.1.2-SNAPSHOT"

scalaVersion := "2.11.4"

crossScalaVersions := "2.10.4" :: "2.11.4" :: Nil

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "4.0",
  "com.lvxingpai" %% "etcd-store" % "0.5.0-SNAPSHOT",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.11"
