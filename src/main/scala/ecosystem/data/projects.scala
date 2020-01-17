package ecosystem.data

import ecosystem.model._

given projects: Ecosystem
  defineMill("utest")(
    origin = "https://github.com/dotty-staging/utest.git",
    upstream = "https://github.com/lihaoyi/utest.git",
    baseCommand = version => s"""./mill -D dottyVersion="$version" utest.jvm[$version]"""
  )
  defineMill("os-lib")(
    origin = "https://github.com/dotty-staging/os-lib.git",
    upstream = "https://github.com/lihaoyi/os-lib.git",
    baseCommand = version => s"""mill -i -D dottyVersion="$version" os[$version].test""",
    dependencies = List("utest", "sourcecode")
  )
  defineMill("sourcecode")(
    origin = "https://github.com/dotty-staging/sourcecode.git",
    upstream = "https://github.com/lihaoyi/sourcecode.git",
    baseCommand = version => s"""./mill -i -D dottyVersion="$version" sourcecode.jvm[$version]"""
  )
