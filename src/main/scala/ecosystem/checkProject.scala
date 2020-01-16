package ecosystem

import better.files.File

import org.eclipse.jgit.api._
import org.eclipse.jgit.transport.URIish

import ecosystem.impl._
import ecosystem.model.{ given, _ }
import ecosystem.data.{ given, _ }

def checkProject(project: Project): CheckReport = project.withGit { git =>
  val branch = git.getRepository.getBranch
  CheckReport(branch, 1, 2)
}
