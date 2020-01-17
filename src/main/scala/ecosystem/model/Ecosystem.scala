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
      upstream: String,
      upstreamBranch: String = "master",
      baseCommand: String => String,
      dependencies: List[String] = Nil
    ): CommunityProject =
    val project = CommunityProject(
      name = name,
      origin = origin,
      upstream = upstream,
      upstreamBranch = upstreamBranch,
      compileCommand = version => s"${baseCommand(version)}.compile",
      testCommand = version => s"${baseCommand(version)}.test",
      publishLocalCommand = version => s"${baseCommand(version)}.publishLocal",
      cleanCommand = "rm -rf out/"
    )
    register(project, dependencies)
    project

  protected def defineSbt(name: String)(
      origin: String,
      upstream: String,
      upstreamBranch: String = "master"
    ): CommunityProject =
    val project = CommunityProject(
      name = name,
      origin = origin,
      upstream = upstream,
      upstreamBranch = upstreamBranch,
      compileCommand = null,  // TODO
      testCommand = null,
      publishLocalCommand = null,
      cleanCommand = null,
    )
    register(project, Nil)
    project

  def project(name: String) = projectsStore(name)
  def dependenciesOf(name: String) = dependenciesStore.getOrElseUpdate(name, Set.empty).map(project)
  def all = projectsStore.values.toList
