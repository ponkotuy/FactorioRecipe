
name := "FactorioRecipe"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "org.luaj" % "luaj-jse" % "3.0.1",
  "org.skinny-framework" %% "skinny-orm" % "2.3.6",
  "com.github.tototoshi" %% "play-json4s-native" % "0.5.0",
  "com.github.tototoshi" %% "play-json4s-test-native" % "0.5.0" % "test",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.5.1",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions += "-feature"

// Docker
dockerRepository := Some("ponkotuy")
dockerUpdateLatest := true
