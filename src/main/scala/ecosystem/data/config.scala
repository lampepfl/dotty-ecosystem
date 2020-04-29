package ecosystem.data

import better.files.File

import ecosystem.model.{ given _, _ }

val workdir = File("repos")
val dottyVersion = "0.25.0-bin-SNAPSHOT"

lazy val sbtPluginFile =
  dotty.dir/"community-build/sbt-dotty-sbt"
