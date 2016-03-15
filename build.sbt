
lazy val projectSettings = Seq(
  name := "spark-redis-receiver",
  organization := "nl.anchormen",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.11.7",
  crossPaths 	:= false)

lazy val projectDependencies = Seq(
  libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "1.6.0" % "provided",
  libraryDependencies += "org.apache.spark" % "spark-streaming_2.11" % "1.6.0" %"provided",
  libraryDependencies += "redis.clients" % "jedis" % "2.8.0",
  libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  libraryDependencies += "org.mockito" % "mockito-all" % "2.0.2-beta" % "test")

lazy val projectAggregatedSettings = projectSettings ++ projectDependencies

lazy val root = (project in file("."))
  .settings(projectAggregatedSettings: _*)


/*Assembly Settings*/
//assemblyJarName in assembly := "spark-redis-receiver_2.11_1.6.0.jar"
