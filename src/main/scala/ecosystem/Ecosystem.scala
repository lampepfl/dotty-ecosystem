package ecosystem

import collection.mutable
import java.net.URL

case class Project(
  name: String,
  staging: URL,
  upstream: URL
)

class Ecosystem
  private val projectsStore = mutable.Map.empty[String, Project]
  private val dependenciesStore = mutable.Map.empty[String, Set[String]]

  protected def define(name: String)(
      staging: String,
      upstream: String,
      dependencies: List[String] = Nil
    ): Project =
    val project = Project(name, URL(staging), URL(upstream))
    projectsStore.update(name, project)
    for depString <- dependencies do
      val old = dependenciesStore.getOrElseUpdate(name, Set.empty)
      dependenciesStore.update(name, old + depString)
    project

  def project(name: String) = projectsStore(name)
  def dependenciesOf(name: String) = dependenciesStore.getOrElseUpdate(name, Set.empty).map(project)

def (name: String) asProject (given e: Ecosystem) = e.project(name)
def (name: String) dependencies (given e: Ecosystem) = e.dependenciesOf(name)
def (project: Project) dependencies (given e: Ecosystem) = e.dependenciesOf(project.name)
