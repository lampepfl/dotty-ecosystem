![Screenshot](/demo.png?raw=true "Screenshot")
<!-- <img src="/demo.png?raw=true" width="500"> -->

# Get started
To see the status of the community build:

1. Clone this repo.
2. Run `sbt run`. This will open a console.
3. Run `clone` to clone all the projects under the `/repos` dir. The list of the projects is in the `src/main/scala/ecosystem/data/projects.scala` file.
4. Run `check` to get the status of the build.

All the commands are available under `src/main/scala/ecosystem/executeCommand.scala`. Every command object's overridden `toString` method shows what to type in the console to run the command.

So far, `BuildCommand`s are unsupported for SBT projects.

# Community build policy [draft]
1. Each project consists of the origin and upstream branch.
2. The syncing is done via rebases to maintain linear history.
3. Each project has the capability to force the Scala version from the command line when working with its build tool.
4. Sanity rules: the following is the desired state of each project:
    1. Main branch name is standardized.
    2. Dotty CI points to the latest commit.
    3. Even with upstream.
    4. Staging area clear, no unstaged changes.
5. Commands are provided to help the user overview and address each of the sanity issues in a standardized way.
6. Commands are simple and do not push to the repos.

## Reasoning
**(2) Why not rebase?**
- To trivially keep the older versions of the library that are ported to Dotty available and referable to via a commit. This way, a project that depends on a version of the library will not find itself without that dependency because that library released a newer version.
- Second, to allow for long-living port branches. When these branches get large, it is much harder to rebase them because we need to resolve conflicts one commit at a time.

**(3) Why not just modify the build?**
This is only Mill-related since SBT has the capability in question. It is needed for easy integration with our CI and when testing locally against the Dotty snapshot version. If our branch is modified and doesn't track the upstream perfectly, this complicates syncing with upstream. If our branch tracks upstream perfectly, the sync is trivially done via first merging the upstream and then submitting the PR to upstream. If it doesn't track perfectly, we need to take care of removing and reintroducing the private changes before submitting the PR. Introducing the capability to specify an extra cross Scala version into the community mill projects is non-intrusive so seems to be an optimal solution.
