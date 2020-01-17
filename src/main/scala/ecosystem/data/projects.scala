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


  // TODO: below projects don't have commands
  defineMill("upickle")(
    origin = "https://github.com/dotty-staging/upickle.git",
    upstream = "https://github.com/lihaoyi/upickle.git",
    baseCommand = null
  )

  defineSbt("algebra")(
    origin = "https://github.com/dotty-staging/algebra.git",
    upstream = "https://github.com/typelevel/algebra.git"
  )

  // defineSbt("better-files")(
  //   origin = "https://github.com/dotty-staging/better-files.git",
  //   upstream = "https://github.com/pathikrit/better-files.git"
  // )

  defineSbt("scalacheck")(
    origin = "https://github.com/dotty-staging/scalacheck.git",
    upstream = "https://github.com/typelevel/scalacheck.git"
  )

  defineSbt("ScalaPB")(
    origin = "https://github.com/dotty-staging/ScalaPB.git",
    upstream = "https://github.com/scalapb/ScalaPB.git"
  )

  defineSbt("scalatest")(
    origin = "https://github.com/dotty-staging/scalatest.git",
    upstream = "https://github.com/scalatest/scalatest.git"
  )

  defineSbt("scopt")(
    origin = "https://github.com/dotty-staging/scopt.git",
    upstream = "https://github.com/scopt/scopt.git",
    upstreamBranch = "scopt4"
  )

  defineSbt("squants")(
    origin = "https://github.com/dotty-staging/squants.git",
    upstream = "https://github.com/typelevel/squants.git"
  )

  // defineSbt("scala")(
  //   origin = "https://github.com/dotty-staging/scala.git",
  //   upstream = "https://github.com/scala/scala.git",
  //   upstreamBranch = "2.13.x"
  // )

  defineSbt("minitest")(
    origin = "https://github.com/dotty-staging/minitest.git",
    upstream = "https://github.com/monix/minitest.git"
  )

  defineSbt("fastparse")(
    origin = "https://github.com/dotty-staging/fastparse.git",
    upstream = "https://github.com/lihaoyi/fastparse.git"
  )

  defineSbt("stdLib213")(
    origin = "https://github.com/dotty-staging/scala.git",
    upstream = "https://github.com/scala/scala.git",
    upstreamBranch = "2.13.x"
  )

  // TODO no upstream?
  // defineSbt("pdbp")(
  //   origin = "https://github.com/dotty-staging/pdbp.git",
  //   upstream = ""
  // )

  defineSbt("scala-xml")(
    origin = "https://github.com/dotty-staging/scala-xml.git",
    upstream = "https://github.com/scala/scala-xml.git"
  )

  defineSbt("shapeless")(
    origin = "https://github.com/dotty-staging/shapeless.git",
    upstream = "https://github.com/milessabin/shapeless.git",
    upstreamBranch = "shapeless-3-staging"
  )

  defineSbt("effpi")(
    origin = "https://github.com/dotty-staging/effpi.git",
    upstream = "https://github.com/alcestes/effpi.git"
  )

  defineSbt("intent")(
    origin = "https://github.com/dotty-staging/intent.git",
    upstream = "https://github.com/factor10/intent.git"
  )

  defineSbt("scalatestplus-scalacheck")(
    origin = "https://github.com/dotty-staging/scalatestplus-scalacheck.git",
    upstream = "https://github.com/scalatest/scalatestplus-scalacheck.git"
  )
