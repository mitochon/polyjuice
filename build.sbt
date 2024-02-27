import Dependencies._
import ReleaseTransformations._

lazy val mavenReleaseSettings = Seq(
	publishMavenStyle := true,
	publishTo := sonatypePublishToBundle.value,
	licenses += "MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"),
	homepage := Some(url("https://github.com/mitochon/polyjuice")),
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
	))
)

lazy val sbtReleasePluginSettings = Seq(
	releasePublishArtifactsAction := PgpKeys.publishSigned.value,
	releaseIgnoreUntrackedFiles := true,
	releaseProcess := Seq[ReleaseStep](
		checkSnapshotDependencies,
		inquireVersions,
		runClean,
		setReleaseVersion,
		commitReleaseVersion,
		tagRelease,
		releaseStepCommandAndRemaining("publishSigned"),
		releaseStepCommand("sonatypeBundleRelease"),
		setNextVersion,
		commitNextVersion,
		pushChanges
	)
)

lazy val commonSettings = Seq(
	organization := "me.mitochon",
	scalaVersion := "2.12.19",
	libraryDependencies += scalaTest % Test
) ++ mavenReleaseSettings ++ sbtReleasePluginSettings

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
