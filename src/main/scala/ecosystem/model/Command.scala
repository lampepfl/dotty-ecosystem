package ecosystem
package model

import ecosystem.data.dottyVersion

sealed trait Command

/** A command to be executed in the context of a project */
sealed trait ProjectCommand extends Command:
  def projectName: String

/** A command that involves running a build tool on the project */
sealed trait BuildCommand extends ProjectCommand:
  def scalaVersion: String

case class Show            (projectName: String) extends ProjectCommand { override def toString = s"show $projectName" }
case class Clone           (projectName: String) extends ProjectCommand { override def toString = s"clone $projectName" }
case class Update          (projectName: String) extends ProjectCommand { override def toString = s"update $projectName" }
case class Check           (projectName: String) extends ProjectCommand { override def toString = s"check $projectName" }
case class Clean           (projectName: String) extends ProjectCommand { override def toString = s"clean $projectName" }
case class UpdateCiTracking(projectName: String) extends ProjectCommand { override def toString = s"updateCiTracking $projectName" }
case class PublishLocalDeps(projectName: String, scalaVersion: String) extends ProjectCommand { override def toString = s"publishLocalDeps $projectName $scalaVersion" }

case class Compile     (projectName: String, scalaVersion: String) extends BuildCommand   { override def toString = s"compile $projectName $scalaVersion" }
case class Test        (projectName: String, scalaVersion: String) extends BuildCommand   { override def toString = s"test $projectName $scalaVersion" }
case class PublishLocal(projectName: String, scalaVersion: String) extends BuildCommand   { override def toString = s"publishLocal $projectName $scalaVersion" }

case object Show        extends Command { override def toString = "show" }
case object Exit        extends Command { override def toString = "exit" }
case object Clone       extends Command { override def toString = "clone" }
case object Update      extends Command { override def toString = "update" }
case object UpdateDotty extends Command { override def toString = "updateDotty" }
case object Check       extends Command { override def toString = "check" }

case class ParseException(msg: String) extends Exception(msg)

def parseCommand(line: String): Command =
  def versionArg(args: List[String]) = args match
    case version :: Nil => version
    case Nil => dottyVersion

  line.split(" ").toList match
    case "show" :: Nil => Show
    case "show" :: project :: Nil => Show(project)
    case "exit" :: Nil => Exit
    case "clone" :: Nil => Clone
    case "clone" :: project :: Nil => Clone(project)
    case "update" :: Nil => Update
    case "update" :: project :: Nil => Update(project)
    case "updateDotty" :: Nil => UpdateDotty
    case "check" :: Nil => Check
    case "check" :: project :: Nil => Check(project)
    case "clean" :: project :: Nil => Clean(project)
    case "compile" :: project :: args => Compile(project, versionArg(args))
    case "test" :: project :: args => Test(project, versionArg(args))
    case "publishLocal" :: project :: args => PublishLocal(project, versionArg(args))
    case "updateCiTracking" :: project :: Nil => UpdateCiTracking(project)
    case "publishLocalDeps" :: project :: args => PublishLocalDeps(project, versionArg(args))

    case _ => throw ParseException(s"Unknown command: $line")
