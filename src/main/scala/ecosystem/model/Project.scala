package ecosystem
package model

import better.files.File
import org.eclipse.jgit.api._
import org.eclipse.jgit.transport.URIish

import ecosystem.data.workdir


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
  def withGit[T](f: Git => T): T =
    val git = Git.open(project.dir.toJava)
    val res =
      try f(git)
      finally git.close()
    res


