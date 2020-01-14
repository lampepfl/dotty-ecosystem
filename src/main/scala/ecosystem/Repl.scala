package ecosystem

import annotation.tailrec
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
  val chunks = line.split(" ").toList
  chunks match
    case "show" :: name :: Nil =>
      try
        val project = name.asProject
        out(s"""
          |Project: ${project.name}
          |Staging: ${project.staging}
          |Upstream: ${project.upstream}
          |Dependencies: ${project.dependencies.map(_.name).mkString(", ")}
        """)
      catch
        case _: NoSuchElementException => println(s"Project not found: ${name}")

    case "exit" :: Nil => throw UserInterruptException("")

    case _ => println(s"Command could not be interpreted: $line")

inline def out(str: String) = println(formatStr(str))

@tailrec def formatStr(str: String): String =
  var res = str
  res = str.stripMargin
  if res.startsWith("\n") then res = res.tail
  if res != str then formatStr(res) else res
