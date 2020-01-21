package ecosystem
package model

import better.files.File
import org.eclipse.jgit.api._
import org.eclipse.jgit.transport.URIish

import ecosystem.data.workdir
import ecosystem.impl

/** A Git project */
trait Project
  val name: String
  val origin: String

/**
 * A community project is the Dotty-ported project that
 * we keep in our community build, run tests against and
 * keep in sync with upstream.
 */
case class CommunityProject(
  name: String,
  origin: String,
  originBranch: String,
  upstream: String,
  upstreamBranch: String,
  compileCommand: String => String,  // Generate command from Scala version
  testCommand: String => String,
  publishLocalCommand: String => String,
  cleanCommand: String,
) extends Project

/**
 * Dotty repository. Exists solely to whether CI runs tests
 * against the most recent commits of the community projects.
 */
lazy val dotty = new Project {
  val name = "dotty"
  val origin = "https://github.com/lampepfl/dotty.git"
}

def (name: String) asProject (given e: Ecosystem) = e.project(name)

extension on (project: Project) with
  def dir: File = workdir/project.name
  def isCloned = project.dir.exists
  def withGit[T](f: Git => T): T =
    val git = Git.open(project.dir.toJava)
    val res =
      try f(git)
      finally git.close()
    res

extension on (project: CommunityProject)(given e: Ecosystem) with
  def dependencies: Set[CommunityProject] = e.dependenciesOf(project.name)
  inline def exec(cmd: String): Unit = impl.exec(cmd, project.dir)
