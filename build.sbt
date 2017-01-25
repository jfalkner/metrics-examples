name := "Metrics Examples"

version in ThisBuild := "0.0.1"

organization in ThisBuild := "falkner.jayson"

scalaVersion in ThisBuild := "2.11.8"

scalacOptions in ThisBuild := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature", "-language:postfixOps")

libraryDependencies ++= Seq(
  // test API from https://etorreborre.github.io/specs2/
  "org.specs2" %% "specs2-core" % "3.8.5" % "test",
  // JSON serialization support from https://github.com/spray/spray-json
  "io.spray" %% "spray-json" % "1.3.2"
)

lazy val metrics = RootProject(uri("https://github.com/jfalkner/metrics.git#0.2.3"))

val main = Project(id = "metrics_examples", base = file(".")).dependsOn(metrics)

