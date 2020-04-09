package ecosystem

import better.files.File

import org.eclipse.jgit.lib._
import org.eclipse.jgit.api._
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.submodule.SubmoduleWalk
import org.eclipse.jgit.revwalk.RevWalk

import ecosystem.impl._
import ecosystem.model.{ given _, _ }
import ecosystem.data.{ given _, _ }
import ecosystem.rendering._

def checkProject(project: CommunityProject, ciTrackingCache: Map[String, String] = null): CheckReport = project.withGit { git =>
  val repo = git.getRepository
  val branch = s"${repo.getBranch}"
  val trackingStatus = doWhileTracking(repo, branch, "upstream", project.upstreamBranch) {
    BranchTrackingStatus.of(repo, branch)
  }
  if trackingStatus eq null then
    error(s"Upstream not specified or not synced for ${project.name}")

  val originHeadHash = git.getRepository.findRef("HEAD").getObjectId.getName
  val ciHash = Option(ciTrackingCache).getOrElse(buildCiTrackingCache())(project.submoduleName)

  val revWalk = RevWalk(git.getRepository)
  val originHeadCommit = revWalk.parseCommit(ObjectId.fromString(originHeadHash))
  val ciCommit = revWalk.parseCommit(ObjectId.fromString(ciHash))

  val isCiAhead  = revWalk.isMergedInto(originHeadCommit, ciCommit)
  val isCiBehind = revWalk.isMergedInto(ciCommit, originHeadCommit)

  CheckReport(
    mainBranch = branch,
    aheadUpstream = trackingStatus.getAheadCount,
    behindUpstream = trackingStatus.getBehindCount,
    ciHash = ciHash,
    originHeadHash = originHeadHash,
    isCiAhead = isCiAhead,
    isCiBehind = isCiBehind
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