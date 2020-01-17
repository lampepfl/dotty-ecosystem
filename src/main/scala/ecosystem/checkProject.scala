package ecosystem

import better.files.File

import org.eclipse.jgit.lib._
import org.eclipse.jgit.api._
import org.eclipse.jgit.transport.URIish

import ecosystem.impl._
import ecosystem.model.{ given, _ }
import ecosystem.data.{ given, _ }

def checkProject(project: CommunityProject): CheckReport = project.withGit { git =>
  val repo = git.getRepository
  val branch = s"${repo.getBranch}"
  doWhileTracking(repo, branch, "upstream", project.upstreamBranch) {
    val trackingStatus = BranchTrackingStatus.of(repo, branch)
    CheckReport(branch, trackingStatus.getAheadCount, trackingStatus.getBehindCount)
  }
}

/** Which commit does the Dotty CI run the tests against? */
def ciTracking(project: CommunityProject) =
  dotty.withGit { dottyGit => project.withGit { projectGit =>

  }}

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
