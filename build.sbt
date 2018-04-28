name := "http4s-test"

version := "0.1"

scalaVersion := "2.12.4"

scalacOptions += "-Ypartial-unification"

addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M11" cross CrossVersion.full)

libraryDependencies ++= Seq(
  "io.frees" %% "frees-http4s" % "0.8.0",
  "org.http4s" %% "http4s-core" % "0.18.9",
  "org.http4s" %% "http4s-dsl" % "0.18.9",
  "org.http4s" %% "http4s-blaze-server" % "0.18.9"
)