package ecosystem
package model

import better.files.File
import org.eclipse.jgit.api._
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.submodule.SubmoduleWalk

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
  originBranch: Option[String],
  upstream: String,
  upstreamBranch: String,
  compileCommand: Option[String => String],  // Generate command from Scala version
  testCommand: Option[String => String],
  publishLocalCommand: Option[String => String],
  cleanCommand: String,
  submoduleName: String
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

private def doWithGit[T](git: Git, f: Git => T): T =
  val res =
    try f(git)
    finally git.close()
  res

extension on (project: Project) with
  def dir: File = workdir/project.name
  def isCloned = project.dir.exists
  def withGit[T](f: Git => T): T =
    doWithGit(Git.open(project.dir.toJava), f)
  inline def exec(cmd: String): Unit = impl.exec(cmd, project.dir)



extension on (project: CommunityProject)(given e: Ecosystem) with
  def dependencies: Set[CommunityProject] = e.dependenciesOf(project.name)
  def submoduleDir: File = dotty.dir/"community-build/community-projects"/project.submoduleName
  def withSubmoduleGit[T](f: Git => T): T = dotty.withGit { dottyGit =>
    val walk = SubmoduleWalk.forIndex(dottyGit.getRepository)
    while walk.next() && walk.getModuleName.split("/").last != project.submoduleName do ()
    val git = Git.wrap(walk.getRepository)
    doWithGit(git, f)
  }
