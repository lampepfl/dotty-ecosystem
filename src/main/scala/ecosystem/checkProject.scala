package ecosystem

import better.files.File

import org.eclipse.jgit.lib._
import org.eclipse.jgit.api._
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.submodule.SubmoduleWalk

import ecosystem.impl._
import ecosystem.model.{ given, _ }
import ecosystem.data.{ given, _ }
import ecosystem.rendering._

def checkProject(project: CommunityProject, ciTrackingCache: Map[String, String] = null): CheckReport = project.withGit { git =>
  val repo = git.getRepository
  val branch = s"${repo.getBranch}"
  val trackingStatus = doWhileTracking(repo, branch, "upstream", project.upstreamBranch) {
    BranchTrackingStatus.of(repo, branch)
  }
  val originHeadHash = git.getRepository.findRef("HEAD").getObjectId.getName
  val ciHash = Option(ciTrackingCache).getOrElse(buildCiTrackingCache())(project.submoduleName)

  CheckReport(
    mainBranch = branch,
    aheadUpstream = trackingStatus.getAheadCount,
    behindUpstream = trackingStatus.getBehindCount,
    ciHash = ciHash,
    originHeadHash = originHeadHash
  )
}

def buildCiTrackingCache() = dotty.withGit { dottyGit =>
  var cache = Map.empty[String, String]
  val walk = SubmoduleWalk.forIndex(dottyGit.getRepository)
  while walk.next() do
    val name = walk.getModuleName.split("/").last
    cache = cache.updated(name, walk.getHead.getName)
  cache
}

def doWhileTracking[T](repo: Repository, branch: String, remote: String, trackedBranch: String)(action: => T): T =
  val config = repo.getConfig
  def setRemote(value: String) =
    config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, branch, ConfigConstants.CONFIG_KEY_REMOTE, value)
  def setTrackedBranch(value: String) =
    config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, branch, ConfigConstants.CONFIG_KEY_MERGE, value)

  val previousRemote = config.getString(ConfigConstants.CONFIG_BRANCH_SECTION, branch, ConfigConstants.CONFIG_KEY_REMOTE)
  val previousTrackedBranch = config.getString(ConfigConstants.CONFIG_BRANCH_SECTION, branch, ConfigConstants.CONFIG_KEY_MERGE)

  setRemote(remote)
  setTrackedBranch(Constants.R_HEADS + trackedBranch)
  config.save()

  val result = action

  setRemote(previousRemote)
  setTrackedBranch(previousTrackedBranch)
  config.save()

  result

def checkPredicate[T](value: T, predicate: T => Boolean, show: T => String = { (t: T) => t.toString }) =
  if predicate(value) then green(show(value)) else red(show(value))