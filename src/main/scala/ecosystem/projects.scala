package ecosystem

given Ecosystem
  define("utest")(
    staging = "https://github.com/dotty-staging/utest.git",
    upstream = "https://github.com/lihaoyi/utest.git"
  )
  define("oslib")(
    staging = "https://github.com/dotty-staging/os-lib.git",
    upstream = "https://github.com/lihaoyi/os-lib.git",
    dependencies = List("utest", "sourcecode")
  )
  define("sourcecode")(
    staging = "https://github.com/dotty-staging/sourcecode.git",
    upstream = "https://github.com/lihaoyi/sourcecode.git"
  )
