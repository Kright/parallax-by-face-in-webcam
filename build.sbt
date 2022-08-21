ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.3"

resolvers += "jitpack" at "https://jitpack.io"
resolvers += "Maven" at "https://maven.openimaj.org/"

lazy val root = (project in file("."))
  .settings(
    name := "parallax",
    idePackagePrefix := Some("com.kright"),
    libraryDependencies += "com.github.sarxos" % "webcam-capture" % "0.3.12",
    libraryDependencies += "org.openimaj" % "faces" % "1.3.10",
    libraryDependencies += "com.github.Kright" % "ScalaGameMath" % "master-SNAPSHOT",
  )
