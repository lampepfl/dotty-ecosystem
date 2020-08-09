val dottyVersion = "0.26.0-RC1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotty-ecosystem",
    version := "0.1.0",

    scalaVersion := dottyVersion,

    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
    libraryDependencies += "org.jline" % "jline" % "3.13.3",
    libraryDependencies += "org.eclipse.jgit" % "org.eclipse.jgit" % "5.6.0.201912101111-r",
    libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.30",

    libraryDependencies += ("com.github.pathikrit" %% "better-files" % "3.8.0").withDottyCompat(scalaVersion.value)
  )
