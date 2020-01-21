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
      originBranch = originBranch,
      upstream = upstream,
      upstreamBranch = upstreamBranch,
      compileCommand = version => s"${baseCommand(version)}.compile",
      testCommand = version => s"${baseCommand(version)}.test",
      publishLocalCommand = version => s"${baseCommand(version)}.publishLocal",
      cleanCommand = "rm -rf out/",
      submoduleName = if submoduleName ne null then submoduleName else name
    )
    register(project, dependencies)
    project

  protected def defineSbt(name: String)(
      origin: String,
      originBranch: String = null,
      upstream: String,
      upstreamBranch: String = "master",
      submoduleName: String = null
    ): CommunityProject =
    val project = CommunityProject(
      name = name,
      origin = origin,
      originBranch = originBranch,
      upstream = upstream,
      upstreamBranch = upstreamBranch,
      compileCommand = null,  // TODO
      testCommand = null,
      publishLocalCommand = null,
      cleanCommand = "rm -rf target/",
      submoduleName = if submoduleName ne null then submoduleName else name
    )
    register(project, Nil)
    project

  def project(name: String) = projectsStore(name)
  def dependenciesOf(name: String) = dependenciesStore.getOrElseUpdate(name, Set.empty).map(project)
  def all = projectsStore.values.toList
