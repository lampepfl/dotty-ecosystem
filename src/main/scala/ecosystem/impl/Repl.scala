package ecosystem
package impl

import scala.annotation.tailrec

import org.jline.reader._
import org.jline.reader.impl.completer._
import ecosystem.model._
import ecosystem.data.{ given Ecosystem }


@main def Repl =
  def reader = LineReaderBuilder.builder()
    .variable(LineReader.HISTORY_FILE, "history")
    .variable(LineReader.HISTORY_FILE_SIZE, 100)
    .completer(ArgumentCompleter(
      commandCompleter,
      projectCompleter,
      scalaVersionCompleter
    ))
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
  executeCommand(cmd)

def commandCompleter = StringsCompleter(
  "show",
  "show",
  "exit",
  "clone",
  "clone",
  "update",
  "update",
  "updateDotty",
  "check",
  "check",
  "clean",
  "compile",
  "test",
  "publishLocal",
  "updateCiTracking",
  "publishLocalDeps"
)

def projectCompleter(using e: Ecosystem) =
  StringsCompleter(e.all.map(_.name): _*)

def scalaVersionCompleter = StringsCompleter(
  data.dottyVersion, "2.13.1", "2.12.8")
