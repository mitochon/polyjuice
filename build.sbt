import Dependencies._

lazy val commonSettings = Seq(
	organization := "me.mitochon",
	scalaVersion := "2.12.6",
	version := "0.1.0-SNAPSHOT",
	publishMavenStyle := true,
	publishTo := Some(sonatypeDefaultResolver.value),
	licenses += "MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"),
	developers := List(
		Developer(
			id="ico",
			name="Francisco Tanudjaja",
			email="fcstanud@gmail.com",
			url=url("http://github.com/mitochon")
		)
	),
	scmInfo := Some(ScmInfo(
		url("https://github.com/mitochon/polyjuice"),
		"scm:git:https://github.com/mitochon/polyjuice.git"
	)),
	libraryDependencies += scalaTest % Test
)

lazy val root = project
	.in(file("."))
	.aggregate(potion, phial)
	.settings(
		commonSettings,
		name := "polyjuice",
		publishArtifact := false,
		publishTo := Some(
			Resolver.file("Unused transient repository", file("target/unusedrepo"))
		)
	)

lazy val potion = project
	.in(file("potion"))
	.settings(
		commonSettings,
		name := "polyjuice-potion",
		description := "Library for exploring genomic polymorphism."
	)

lazy val phial = project
	.in(file("phial"))
	.dependsOn(potion)
	.settings(
		commonSettings,
		name := "polyjuice-phial",
		description := "Web service for running polyjuice-potion.",
		libraryDependencies ++= Seq(logback, typesafeConfig, typesafeLogging),
		libraryDependencies ++= http4s
	)
