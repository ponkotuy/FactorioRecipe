
name := "FactorioRecipe"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  guice,
  "org.luaj" % "luaj-jse" % "3.0.1",
  "org.skinny-framework" %% "skinny-orm" % "2.3.6",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.6.0",
  "org.postgresql" % "postgresql" % "9.4.1212",
  "org.flywaydb" %% "flyway-play" % "5.0.0",
  "org.springframework.security" % "spring-security-web" % "4.2.2.RELEASE",
  "com.github.tototoshi" %% "play-json4s-native" % "0.8.0",
  "com.github.tototoshi" %% "play-json4s-test-native" % "0.8.0" % "test",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions += "-feature"

// Docker
dockerRepository := Some("ponkotuy")
dockerUpdateLatest := true
