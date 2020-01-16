package ecosystem

import better.files.File

import org.eclipse.jgit.lib._
import org.eclipse.jgit.api._
import org.eclipse.jgit.transport.URIish

import ecosystem.impl._
import ecosystem.model.{ given, _ }
import ecosystem.data.{ given, _ }

def checkProject(project: Project): CheckReport = project.withGit { git =>
  val repo = git.getRepository
  val branch = s"${repo.getBranch}"
  // println("Tracking " + BranchConfig(repo.getConfig, branch).getRemoteTrackingBranch)
  // val trackingStatus = BranchTrackingStatus.of(repo, branch)
  CheckReport(branch, 1, 2)
}
