package ecosystem

import java.io.{ File => JFile }
import scala.annotation.tailrec

import better.files.File

import org.eclipse.jgit.api._
import org.eclipse.jgit.transport.URIish

import org.jline.reader._


@main def Repl =
  val reader = LineReaderBuilder.builder()
    .variable(LineReader.HISTORY_FILE, "history")
    .variable(LineReader.HISTORY_FILE_SIZE, 100)
    .build()

  @tailrec def loop(): Unit =
    val line =
      try reader.readLine("> ")
      catch
        case _: UserInterruptException => return println("User interrupt")
        case _: EndOfFileException => return println("End of file")
    try handle(line)
    catch
      case _: UserInterruptException => return println("Bye!")
      case e: Exception => println(e.getMessage); e.printStackTrace()
    loop()
  loop()

def handle(line: String): Unit =
  val cmd =
    try parseCommand(line)
    catch
      case ParseException(msg) => return println(msg)
  cmd.execute()

def (cmd: Command) execute(): Unit =
  println(s"Executing command: ${cmd}")
  cmd match
    case Show => projects.all.map(_.name).foreach(println)
    case Clean => workdir.clear()
    case Exit => throw UserInterruptException("")

    case cmd: ProjectCommand =>
      val project =
        try cmd.projectName.asProject
        catch
          case _: NoSuchElementException => return println(s"Project not found: ${cmd.projectName}")

      cmd match
        case Show(name) =>
          out(s"""
            |Project: ${project.name}
            |Staging: ${project.staging}
            |Upstream: ${project.upstream}
            |Dependencies: ${project.dependencies.map(_.name).mkString(", ")}
          """)

        case Clone(name) =>
          val git = Git.cloneRepository()
            .setURI(project.staging)
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
