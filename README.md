# Community build policy [draft]
1. Each project consists of the origin and upstream branch.
2. The syncing is done via merges: first merge upstream into origin, then submit PR to upstream.
3. Each project has the capability to force the Scala version from the command line when working with its build tool.
4. Sanity rules: the following is the desired state of each project:
    1. Main branch name is standardized.
    2. Dotty CI points to the latest commit.
    3. Even with upstream.
    4. Staging area clear, no unstaged changes.
5. Commands are provided to help the user overview and address each of the sanity issues in a standardized way.
6. Commands are simple and do not push to the repos.
