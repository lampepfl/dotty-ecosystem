package ecosystem

given projects: Ecosystem
  defineMill("utest")(
    staging = "https://github.com/dotty-staging/utest.git",
    upstream = "https://github.com/lihaoyi/utest.git",
    baseCommand = version => s"""./mill -D dottyVersion="$version" utest.jvm[$version]"""
  )
  defineMill("oslib")(
    staging = "https://github.com/dotty-staging/os-lib.git",
    upstream = "https://github.com/lihaoyi/os-lib.git",
    baseCommand = version => s"""mill -i -D dottyVersion="$version" os[$version].test""",
    dependencies = List("utest", "sourcecode")
  )
  defineMill("sourcecode")(
    staging = "https://github.com/dotty-staging/sourcecode.git",
    upstream = "https://github.com/lihaoyi/sourcecode.git",
    baseCommand = version => s"""./mill -i -D dottyVersion="$version" sourcecode.jvm[$version]"""
  )
