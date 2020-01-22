package ecosystem

import scala.language.implicitConversions
import collection.JavaConverters._

import better.files.File
import org.jline.reader.UserInterruptException

import org.eclipse.jgit.api._
import org.eclipse.jgit.lib._
import org.eclipse.jgit.api.errors.RefAlreadyExistsException
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.submodule.SubmoduleWalk

import ecosystem.impl._
import ecosystem.model.{ given, _ }
import ecosystem.data.{ given, _ }
import ecosystem.rendering._


inline def (cmd: Command) execute(): Unit = executeCommand(cmd)
def executeCommand(cmd: Command): Unit =
  println(s">>> ${cmd}")
  cmd match
    case Show => projects.all.map(_.name).foreach(println)
    case Exit => throw UserInterruptException("")
    case Clone =>
      for p <- projects.all if !p.isCloned do Clone(p.name).execute()
    case Update =>
      for p <- projects.all do Update(p.name).execute()

    case Check =>
      UpdateDotty.execute()
      val ciTrackingCache = buildCiTrackingCache()
      val reportTableHeader = "Project" :: "Branch" :: "Ahead" :: "Behind" :: "CI Tracking" :: Nil
      val reportTableValues =
        for
          project <- projects.all
          report = checkProject(project, ciTrackingCache)
        yield
          val mainBranch     = checkPredicate(report.mainBranch, _ == "dotty-community-build")
          val aheadUpstream  = checkPredicate(report.aheadUpstream, _ == 0)
          val behindUpstream = checkPredicate(report.behindUpstream, _ == 0)
          val ciTracking     = if report.ciHash == report.originHeadHash then green("√") else red("X")
          project.name :: mainBranch :: aheadUpstream :: behindUpstream :: ciTracking :: Nil
      val reportTable: List[List[String]] = reportTableHeader :: reportTableValues
      println(table(reportTable))

    case UpdateDotty =>
      val git =
        if !dotty.isCloned
          info("Dotty is not cloned yet, cloning. This might take 1-2 minutes.")
          val git0 = Git.cloneRepository()
            .setURI(dotty.origin)
            .setDirectory(dotty.dir.toJava)
            .call()
          info("Initializing submodules.")
          git0.submoduleInit.call()
          git0
        else Git.open(dotty.dir.toJava)
      if !git.getRepository.getRemoteNames.asScala("staginga")
        info("Adding staging remote to Dotty repo")
        git.remoteAdd
          .setName("staging")
          .setUri(URIish("https://github.com/dotty-staging/dotty.git"))
          .call()
      info("Pulling the latest Dotty changes")
      git.pull.call()
      info("Updating Dotty submodules. If working against a fresh clone, this might take a few minutes.")
      dotty.exec("git submodule update")  // JGit API fails in certain situations command line API knows how to handle
      git.close()

    case cmd: ProjectCommand =>
      val project =
        try cmd.projectName.asProject
        catch
          case _: NoSuchElementException => return error(s"Project not found: ${cmd.projectName}")
      if !project.isCloned && !cmd.isInstanceOf[Clone] then Clone(project.name).execute()

      cmd match
        case Show(name) =>
          def printCommand(commandName: String, versionToCommand: Option[String => String]) =
            val cmdString = versionToCommand.map(_(dottyVersion)).getOrElse(red("N/A"))
            println(s"${bold(commandName)}\n${cmdString}\n")

          val showTable = List(
            "Name" :: "Value" :: Nil,
            "Project" :: project.name :: Nil,
            "Our fork" :: url(project.origin) + " " :: Nil, // Whitespace to make it clickable in the terminal
            "Fork branch" :: project.originBranch.map(red).getOrElse("GitHub default") :: Nil,
            "Upstream" :: url(project.upstream) + " " :: Nil,
            "Upstream branch" :: s"upstream/${project.upstreamBranch}" :: Nil,
            "Dependencies" :: (
              if project.dependencies.nonEmpty
              then project.dependencies.map(_.name).mkString(", ")
              else "None") :: Nil,
          )
          println(table(showTable))
          List[(String, Option[String => String])](
            ("Compile", project.compileCommand),
            ("Test", project.testCommand),
            ("Publish local", project.publishLocalCommand),
            ("Clean", Some(_ => project.cleanCommand)),
          ).foreach(printCommand)

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
          checkoutBranchIfNotExists(project.originBranch, git)
          git.close()

        case Update(name) =>
          project.withGit { git =>
            checkoutBranchIfNotExists(project.originBranch, git)
            git.pull.call()
            git.fetch.setRemote("upstream").call()
          }

        case Clean(name) => project.exec(project.cleanCommand)

        case cmd: BuildCommand =>
          project.dependencies.foreach { dep => PublishLocal(dep.name, cmd.scalaVersion).execute() }

          def execBuild(shellCmd: Option[String => String], version: String) =
            shellCmd match
              case Some(cmd) => project.exec(cmd(version))
              case None => error(s"Project ${cmd.projectName} doesn't know the shell command to " +
                s"execute for `$cmd`")

          cmd match
            case Compile(name, version) => execBuild(project.compileCommand, version)
            case Test(name, version) => execBuild(project.testCommand, version)
            case PublishLocal(name, version) => execBuild(project.publishLocalCommand, version)

        case Check(name) =>
          UpdateDotty.execute()
          val report = checkProject(project)

          println(s"""
            |Main branch: ${checkPredicate(report.mainBranch, _ == "dotty-community-build")}
            |Ahead upstream: ${checkPredicate(report.aheadUpstream, _ == 0)}
            |Behind upstream: ${checkPredicate(report.behindUpstream, _ == 0)}
            |CI hash == Origin head hash: ${checkPredicate((report.ciHash, report.originHeadHash), t => t._1 == t._2, t => s"${t._1} == ${t._2}")}
          """.stripMargin)

        case UpdateCiTracking(name) =>
          UpdateDotty.execute()
          val report = checkProject(project)
          val originHead = report.originHeadHash

          project.withSubmoduleGit { git =>
            git.fetch.setRemote("origin").call()
            git.checkout.setName(report.originHeadHash).call()
          }

          dotty.withGit { git =>
            git.checkout
              .setName(s"update-ci-$name-to-${report.originHeadHash}")
              .setCreateBranch(true)
              .call()
            git.add
              .addFilepattern(s"community-build/community-projects/${project.submoduleName}")
              .call()
            git.commit
              .setMessage(s"Update CI tracking for $name")
              .call()
          }

def checkoutBranchIfNotExists(branchOpt: Option[String], git: Git): Unit =
  for branch <- branchOpt do
    val createBranch =
      !git.branchList.call().asScala.exists(_.getName == Constants.R_HEADS + branch)

    var cmd = git.checkout
      .setCreateBranch(createBranch)
      .setName(branch)

    if createBranch then
      info(s"Branch $branch does not exist and will now be created")
      cmd = cmd
        .setStartPoint(s"origin/$branch")
        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)

    cmd.call()
