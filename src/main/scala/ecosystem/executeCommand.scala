package ecosystem

import better.files.File
import org.jline.reader.UserInterruptException

import org.eclipse.jgit.api._
import org.eclipse.jgit.transport.URIish

import ecosystem.impl._
import ecosystem.model.{ given, _ }
import ecosystem.data.{ given, _ }


inline def (cmd: Command) execute(): Unit = executeCommand(cmd)
def executeCommand(cmd: Command): Unit =
  println(s">>> ${cmd}")
  cmd match
    case Show => projects.all.map(_.name).foreach(println)
    case Exit => throw UserInterruptException("")
    case Clone =>
      if workdir.nonEmpty then println(s"Workdir $workdir is not empty")
      else for p <- projects.all do Clone(p.name).execute()
    case Update =>
      for p <- projects.all do Update(p.name).execute()

    case UpdateDotty =>
      val git =
        if !dotty.isCloned
          out("Dotty is not cloned yet, cloning")
          Git.cloneRepository()
            .setURI(dotty.origin)
            .setDirectory(dotty.dir.toJava)
            .call()
        else Git.open(dotty.dir.toJava)
      println("Pulling the latest Dotty changes")
      git.pull.call()
      println("Updating Dotty submodules")
      git.submoduleUpdate.call()
      git.close()

    case cmd: ProjectCommand =>
      val project =
        try cmd.projectName.asProject
        catch
          case _: NoSuchElementException => return println(s"Project not found: ${cmd.projectName}")

      cmd match
        case Show(name) =>
          out(s"""
            |Project: ${project.name}
            |Staging: ${project.origin}
            |Upstream: ${project.upstream}
            |Dependencies: ${project.dependencies.map(_.name).mkString(", ")}
          """)

        case Clone(name) =>
          val git = Git.cloneRepository()
            .setURI(project.origin)
            .setDirectory(project.dir.toJava)
            .call()
          git.remoteAdd
            .setName("upstream")
            .setUri(URIish(project.upstream))
            .call()
          git.fetch
            .setRemote("upstream")
            .call()
          git.close()

        case Update(name) =>
          project.withGit { git =>
            git.pull.call()
            git.fetch.setRemote("upstream").call()
          }

        case Clean(name) => exec(project.cleanCommand, project.dir)

        case cmd: BuildCommand =>
          if !project.isCloned then Clone(project.name).execute()
          project.dependencies.foreach { dep => PublishLocal(dep.name, cmd.scalaVersion).execute() }

          cmd match
            case Compile(name, version) =>
              exec(project.compileCommand(version), project.dir)

            case Test(name, version) =>
              exec(project.testCommand(version), project.dir)

            case PublishLocal(name, version) =>
              exec(project.publishLocalCommand(version), project.dir)


        case Check(name) =>
          UpdateDotty.execute()
          val report = checkProject(project)
          out(s"""
            |Project: $name
            |Main branch: ${report.mainBranch}
            |Upstream branch: upstream/${project.upstreamBranch}
            |Ahead upstream: ${report.aheadUpstream}
            |Behind upstream: ${report.behindUpstream}
          """)
