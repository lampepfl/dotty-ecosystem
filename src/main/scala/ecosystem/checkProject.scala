package ecosystem

import better.files.File

import org.eclipse.jgit.lib._
import org.eclipse.jgit.api._
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.submodule.SubmoduleWalk

import ecosystem.impl._
import ecosystem.model.{ given, _ }
import ecosystem.data.{ given, _ }

def checkProject(project: CommunityProject): CheckReport = project.withGit { git =>
  val repo = git.getRepository
  val branch = s"${repo.getBranch}"
  val trackingStatus = doWhileTracking(repo, branch, "upstream", project.upstreamBranch) {
    BranchTrackingStatus.of(repo, branch)
  }
  val (ciHash, originHeadHash) = ciTracking(project, git)
  CheckReport(
    mainBranch = branch,
    aheadUpstream = trackingStatus.getAheadCount,
    behindUpstream = trackingStatus.getBehindCount,
    ciHash = ciHash,
    originHeadHash = originHeadHash
  )
}

/** Which commit does the Dotty CI run the tests against? */
def ciTracking(project: CommunityProject, projectGit: Git): (String, String) =
  dotty.withGit { dottyGit =>
    val walk = SubmoduleWalk.forIndex(dottyGit.getRepository)
    while
      walk.next() &&
      !walk.getModuleName.endsWith(s"/${project.name}")
    do ()

    val ciHash = walk.getHead.getName
    val originHeadHash = projectGit.getRepository.findRef("HEAD").getObjectId.getName
    (ciHash, originHeadHash)
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
