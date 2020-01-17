package ecosystem.model

import collection.mutable

class Ecosystem
  private val projectsStore = mutable.Map.empty[String, Project]
  private val dependenciesStore = mutable.Map.empty[String, Set[String]]

  protected def defineMill(name: String)(
      staging: String,
      upstream: String,
      upstreamBranch: String = "master",
      baseCommand: String => String,
      dependencies: List[String] = Nil
    ): Project =
    val project = Project(
      name = name,
      staging = staging,
      upstream = upstream,
      upstreamBranch = upstreamBranch,
      compileCommand = version => s"${baseCommand(version)}.compile",
      testCommand = version => s"${baseCommand(version)}.test",
      publishLocalCommand = version => s"${baseCommand(version)}.publishLocal",
      cleanCommand = "rm -rf out/"
    )
    projectsStore.update(name, project)
    for depString <- dependencies do
      val old = dependenciesStore.getOrElseUpdate(name, Set.empty)
      dependenciesStore.update(name, old + depString)
    project

  def project(name: String) = projectsStore(name)
  def dependenciesOf(name: String) = dependenciesStore.getOrElseUpdate(name, Set.empty).map(project)
  def all = projectsStore.values.toList
