package ecosystem

sealed trait Command

/** A command to be executed in the context of a project */
sealed trait ProjectCommand extends Command
  def projectName: String

/** A command that involves running a build tool on the project */
sealed trait BuildCommand extends ProjectCommand
  def scalaVersion: String

case class Show(projectName: String) extends ProjectCommand
case class Clone(projectName: String) extends ProjectCommand
case class Compile(projectName: String, scalaVersion: String) extends BuildCommand
case class Test(projectName: String, scalaVersion: String) extends BuildCommand
case class PublishLocal(projectName: String, scalaVersion: String) extends BuildCommand

case object Show extends Command
case object Exit extends Command
case object Clean extends Command

case class ParseException(msg: String) extends Exception(msg)

def parseCommand(line: String): Command =
  def versionArg(args: List[String]) = args match
    case version :: Nil => version
    case Nil => dottyVersion

  line.split(" ").toList match
    case "show" :: Nil => Show
    case "show" :: project :: Nil => Show(project)
    case "exit" :: Nil => Exit
    case "clone" :: project :: Nil => Clone(project)
    case "clean" :: Nil => Clean
    case "compile" :: project :: args =>
      Compile(project, versionArg(args))
    case "test" :: project :: args =>
      Test(project, versionArg(args))
    case "publishLocal" :: project :: args =>
      PublishLocal(project, versionArg(args))

    case _ => throw ParseException(s"Unknown command: $line")
