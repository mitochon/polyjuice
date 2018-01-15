import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "io.mitochon",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Polyjuice",
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= Seq(
      "com.github.samtools" % "htsjdk" % "2.14.0",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
    )
  )
