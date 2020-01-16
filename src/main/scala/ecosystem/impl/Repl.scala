package ecosystem
package impl

import scala.annotation.tailrec

import org.jline.reader._
import ecosystem.model._


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
  executeCommand(cmd)
