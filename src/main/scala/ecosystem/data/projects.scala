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
    origin = "",
    upstream = "",
    baseCommand = null
  )

  defineSbt("intent")(
    origin = "",
    upstream = "",
  )

  defineSbt("algebra")(
    origin = "",
    upstream = "",
  )

  defineSbt("scalacheck")(
    origin = "",
    upstream = "",
  )

  defineSbt("scalatest")(
    origin = "",
    upstream = "",
  )

  // vvv dependencies = List(scalatest, scalacheck)
  defineSbt("scalatestplus-scalacheck")(
    origin = "",
    upstream = "",
  )

  defineSbt("scala-xml")(
    origin = "",
    upstream = "",
  )

  defineSbt("scopt")(
    origin = "",
    upstream = "",
  )

  defineSbt("scalap")(
    origin = "",
    upstream = "",
  )

  defineSbt("squants")(
    origin = "",
    upstream = "",
  )

  defineSbt("betterfiles")(
    origin = "",
    upstream = "",
  )

  defineSbt("ScalaPB")(
    origin = "",
    upstream = "",
  )

  defineSbt("minitest")(
    origin = "",
    upstream = "",
  )

  defineSbt("fastparse")(
    origin = "",
    upstream = "",
  )

  //vvv extraSbtArgs  = List("-Dscala.build.compileWithDotty=true")
  defineSbt("stdLib213")(
    origin = "",
    upstream = "",
  )

  defineSbt("shapeless")(
    origin = "",
    upstream = "",
  )

  defineSbt("xml-interpolator")(
    origin = "",
    upstream = "",
  )

  defineSbt("effpi")(
    origin = "",
    upstream = "",
  )

  defineSbt("sconfig")(
    origin = "",
    upstream = "",
  )
