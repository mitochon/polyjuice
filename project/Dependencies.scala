import sbt._

object Dependencies {

  lazy val circeVersion = "0.12.1"
  lazy val http4sVersion = "0.18.24"
  lazy val logbackVersion = "1.2.13"
  lazy val scalaTestVersion = "3.2.0"
  lazy val typesafeConfigVersion = "1.4.0"
  lazy val typesafeLoggingVersion = "3.9.2"

  // web services, including json
  lazy val http4s = Seq(
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-circe" % http4sVersion,
    "io.circe" %% "circe-generic" % circeVersion
  )

  // utilities
  lazy val logback = "ch.qos.logback" % "logback-classic" % logbackVersion
  lazy val typesafeConfig = "com.typesafe" % "config" % typesafeConfigVersion
  lazy val typesafeLogging = "com.typesafe.scala-logging" %% "scala-logging" % typesafeLoggingVersion

  // test
  lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion
}