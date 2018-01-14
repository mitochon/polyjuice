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
      "commons-io" % "commons-io" % "2.6"
    )
  )
