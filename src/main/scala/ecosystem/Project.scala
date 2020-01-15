package ecosystem

import better.files.File

case class Project(
  name: String,
  staging: String,
  upstream: String,
  compileCommand: String => String,  // Generate command from Scala version
  testCommand: String => String,
  publishLocalCommand: String => String,
)

def (name: String) asProject (given e: Ecosystem) = e.project(name)

extension on (project: Project)(given e: Ecosystem) with
  def dependencies: Set[Project] = e.dependenciesOf(project.name)
  def dir: File = workdir/project.name
  def isCloned = project.dir.exists
