
lazy val projectSettings = Seq(
  name := "spark-redis-connector",
  organization := "nl.anchormen",
  version := "0.0.2-SNAPSHOT",
  scalaVersion := "2.11.7",
  //autoScalaLibrary := false,
  crossPaths 	:= false)

lazy val sparkVersion = "2.2.0"

lazy val projectDependencies = Seq(
  libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion /*% "provided"*/,
  libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion /*%"provided"*/,
  libraryDependencies += "redis.clients" % "jedis" % "2.9.0",
  libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.6",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  libraryDependencies += "org.mockito" % "mockito-all" % "2.0.2-beta" % "test")

lazy val projectAggregatedSettings = projectSettings ++ projectDependencies

lazy val root = (project in file("."))
  .settings(projectAggregatedSettings: _*)
  .settings(assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
  })

//skip tests in assembly
test in assembly := {}
