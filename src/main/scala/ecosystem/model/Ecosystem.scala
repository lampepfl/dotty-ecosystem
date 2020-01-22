package ecosystem.model

import collection.mutable

class Ecosystem
  private val projectsStore = mutable.Map.empty[String, CommunityProject]
  private val dependenciesStore = mutable.Map.empty[String, Set[String]]

  private def register(project: CommunityProject, dependencies: List[String]): Unit =
    projectsStore.update(project.name, project)
    for depString <- dependencies do
      val old = dependenciesStore.getOrElseUpdate(project.name, Set.empty)
      dependenciesStore.update(project.name, old + depString)

  protected def defineMill(name: String)(
      origin: String,
      originBranch: String = null,
      upstream: String,
      upstreamBranch: String = "master",
      baseCommand: String => String,
      dependencies: List[String] = Nil,
      submoduleName: String = null
    ): CommunityProject =
    val project = CommunityProject(
      name = name,
      origin = origin,
      originBranch = Option(originBranch),
      upstream = upstream,
      upstreamBranch = upstreamBranch,
      compileCommand = Some((version: String) => s"${baseCommand(version)}.compile"),
      testCommand = Some((version: String) => s"${baseCommand(version)}.test"),
      publishLocalCommand = Some((version: String) => s"${baseCommand(version)}.publishLocal"),
      cleanCommand = "rm -rf out/",
      submoduleName = Option(submoduleName).getOrElse(name)
    )
    register(project, dependencies)
    project

  protected def defineSbt(name: String)(
      origin: String,
      originBranch: String = null,
      upstream: String,
      upstreamBranch: String = "master",
      submoduleName: String = null,
      sbtCompileCommand: String = null,
      sbtTestCommand: String = null,
      sbtPublishLocalCommand: String = null,
    ): CommunityProject =
    def sbtCommand(version: String, sbtSuffix: String) =
      s"""sbt ";set updateOptions in Global ~= (_.withLatestSnapshots(false)) ;++$version! ;$sbtSuffix" """
    val project = CommunityProject(
      name = name,
      origin = origin,
      originBranch = Option(originBranch),
      upstream = upstream,
      upstreamBranch = upstreamBranch,
      compileCommand = Option(sbtCompileCommand).map(suffix => (version: String) => sbtCommand(version, suffix)),
      testCommand = Option(sbtTestCommand).map(suffix => (version: String) => sbtCommand(version, suffix)),
      publishLocalCommand = Option(sbtPublishLocalCommand).map(suffix => (version: String) => sbtCommand(version, suffix)),
      cleanCommand = "rm -rf target/",
      submoduleName = Option(submoduleName).getOrElse(name)
    )
    register(project, Nil)
    project

  def project(name: String) = projectsStore(name)
  def dependenciesOf(name: String) = dependenciesStore.getOrElseUpdate(name, Set.empty).map(project)
  def all = projectsStore.values.toList
