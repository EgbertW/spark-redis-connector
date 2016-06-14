
lazy val projectSettings = Seq(
  name := "spark-redis-connector",
  organization := "nl.anchormen",
  version := "0.0.2-SNAPSHOT",
  scalaVersion := "2.11.7",
  crossPaths 	:= false)

lazy val projectDependencies = Seq(
  libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.1" /*% "provided"*/,
  libraryDependencies += "org.apache.spark" %% "spark-streaming" % "1.6.1" /*%"provided"*/,
  libraryDependencies += "redis.clients" % "jedis" % "2.8.0",
  libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  libraryDependencies += "org.mockito" % "mockito-all" % "2.0.2-beta" % "test")

lazy val projectAggregatedSettings = projectSettings ++ projectDependencies

lazy val root = (project in file("."))
  .settings(projectAggregatedSettings: _*)

//skip tests in assembly
test in assembly := {}