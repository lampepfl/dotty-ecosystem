package ecosystem.impl

import scala.annotation.tailrec
import scala.sys.process._

import better.files.File

def exec(cmd: String, workdir: File) =
  println(s"$$ $cmd")
  Process(cmd, workdir.toJava).!

inline def out(str: String) = println(formatStr(str))

@tailrec def formatStr(str: String): String =
  var res = str
  res = str.stripMargin
  if res.startsWith("\n") then res = res.tail
  if res != str then formatStr(res) else res
