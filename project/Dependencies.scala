import sbt._

object Dependencies {

  // bioinformatics
  lazy val htsjdk = "com.github.samtools" % "htsjdk" % "2.14.0"

  // test
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"
}