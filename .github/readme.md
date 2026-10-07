# GitHub Automation

← Back to the [main README](../README.md)

This folder contains the GitHub Actions automation that lets this repo act as the
orchestrator for the other repos in the org. For `gh` CLI commands to trigger,
monitor and debug these workflows, see the [workflows guide](workflows/readme.md).

## Workflows

| Workflow | What it does |
| --- | --- |
| [Github Repos Reporter](workflows/orchestrator-reporter.yml) | Builds a step summary of improvement branches across the org's repos |
| [Create Monthly Improvement Branches](workflows/orchestrator-prep-monthly-cycle.yml) | Cuts the current month's `yyyy-mm-month-improvements` branch from `main` in recently active repos, then runs the reporter |
| [Monthly Branch Improvement Sync](workflows/orchestrator-monthly-pr-sync.yml) | Runs on the 1st of each month and opens PRs for the previous month's improvement branches |
| [Add Collaborators to Repositories](workflows/add-collaborators.yml) | Grants a user or team access to one or all repos |
| [Java Utilities CI/CD](workflows/ci-cd-java-utilities.yml) | Builds and tests `java-utilities`, and releases on `v*.*.*` tags |
| [Smoke Test](workflows/manual-smoke-test.yml) | Manual check that GitHub Actions runs on `main` |

## Composite actions

| Action | What it does |
| --- | --- |
| [report-repos](actions/report-repos/action.yml) | Collects branch, PR and comparison data and writes the reporter summary |
| [validate-gh-token](actions/validate-gh-token/action.yml) | Validates GitHub CLI authentication and token permissions |
| [java-build-test](actions/java-build-test/action.yml) | Sets up Java and Gradle, builds the project and creates the fat JAR |

Shared helpers live in [scripts/](scripts/). [logging.sh](scripts/logging.sh) provides the
`log` function used by the composite action steps.

## Repos Reporter

Every run covers all non-archived repos of one user or org and always includes:

- **Main branches across all repos**: how many repos are ✨ Active (commit on the default
  branch in the last 30 days), 🌙 Quiet (31–90 days) or 💤 Dormant (over 90 days), plus
  Mermaid charts of commits to default branches per month (last 6 months) and each repo's
  share. A table per repo shows a monthly trend sparkline, commit count, open PRs and the
  last merged PR. Dormant repos are folded away.
- **Report data artifact** (`repos-report-<owner>`, kept 90 days), linked at the bottom of the
  summary:

  | File | Contents |
  | --- | --- |
  | `summary.json` | Run metadata, options and aggregated totals (activity counts, commits per month, open PRs, branches at conflict risk) |
  | `main_branches.json` / `.csv` | One row per repo: default branch, last commit, activity, open PRs, last merged PR, commits per month |
  | `main_commits_monthly.csv` | One row per repo per month, stamped with the report date, ready for spreadsheets or BI trend charts |
  | `improvement_branches.json` / `.csv` | One row per improvement branch: ahead/behind, last commit, PR number and state, conflict risk |
  | `report.md` | A copy of the job summary |

### Options

Most options are boolean inputs on the reporter workflow. `full_report` (on by default)
turns on every boolean option except `report_only_open_prs`.

| Input | Adds to the report |
| --- | --- |
| `org_name` | User or org to report on. Blank means this repo's owner (`aleon1220`). See [Token and access](#token-and-access) |
| `report_only_open_prs` | Limits the report to branches with an open PR |
| `stale_branch_age` | Time since the last commit with a status: 🟢 Active (under 14 days), 🟡 Stale (14–30 days), 🔴 Abandoned (over 30 days) |
| `pr_status` | The branch's PR with a direct link and status: 🔀 Open (✅ approved, ❌ changes requested or 👀 awaiting review), 📝 Draft, 🟣 Merged, 🚫 Closed. An open PR takes priority over older PRs. Branches without a PR show ➕ with a link to create one |
| `diff_links` | A one-click `main...branch` compare link |
| `behind_main` | How far each branch is behind its default branch, rated Low, ⚠️ High (over the threshold) or 🚨 Very high (over 5× the threshold). Branches with no new commits have no conflict risk. Flagged branches get a ready-to-run sync command under Recommended Next Steps |
| `behind_threshold` | Commits behind the default branch before a branch counts as high risk (default `10`) |
| `cleanup_dispatch` | Ready-to-run `gh api` delete commands for merged or empty branches from previous months |

The cleanup and sync commands are only printed, not run. Run them as an account with
push access to the target repo.

### Token and access

The reporter reads repos with the `ORG_LEVEL_TOKEN` secret, and the token's account
decides what it can see. The "Validate GitHub Token" step logs the token user, its scopes,
and how many public and private repos it can see in the target, and warns about any gaps.

- **One token for a personal account and orgs:** use a classic PAT with `repo`, `read:org` and
  `workflow`, created by an account that is a member of every org you report on. A
  fine-grained PAT is limited to a single resource owner (one user or one org), so it can't
  cover `aleon1220` and an org at the same time.
- **Org repos need org membership:** the token's account must be a member of the org, with
  access to the repos. Otherwise only public repos are visible, and private org repos
  don't show up at all.
- **Org policies:** if the org enforces SAML SSO, authorize the PAT for that org under
  *Settings → Developer settings → Personal access tokens → Configure SSO*. If the org
  restricts personal access tokens, an org owner must allow classic tokens or approve the
  fine-grained token under *Org settings → Personal access tokens*.
- **Local `gh` CLI:** `gh auth login` uses an OAuth token. If the org restricts OAuth apps, an
  org owner must approve *GitHub CLI* under *Org settings → Third-party access*.

## Future Work

Roadmap for the Repos Reporter. ✅ items are done and can be turned on with the input
shown. ⬜ items are planned.

### Done

- ✅ **Stale Branch Age Tracking** (`stale_branch_age`): Show time since the last commit on each improvement branch to highlight abandoned branches.
- ✅ **Automated PR Status & Direct Links** (`pr_status`): Check if an open PR exists for each improvement branch, with direct links and status indicators.
- ✅ **Direct Diff & Comparison Deep-Links** (`diff_links`): Include one-click GitHub comparison links (`main...branch`) for quick diff inspection.
- ✅ **Behind-Main & Conflict Risk Indicators** (`behind_main`, `behind_threshold`): Flag branches that are significantly behind the default branch (`behind_by > 10` by default) to preempt merge conflicts.
- ✅ **Interactive Automated Cleanup Dispatch** (`cleanup_dispatch`): List merged or obsolete improvement branches from previous months with delete commands.
- ✅ **Report Artifact Export (JSON/CSV)** (always on): Export raw and aggregated metrics as downloadable workflow artifacts for historical tracking and trend analysis.
- ✅ **Main Branch Trend Across Repos** (always on): Chart monthly commits to every repo's default branch, with per-repo activity and trend.

### Planned

- ⬜ **Multi-Owner Reports**: Report on a personal account and several orgs in a single run.
- ⬜ **Cross-Run History**: Read previous runs' `summary.json` artifacts to chart improvement-branch and conflict-risk trends over time.
- ⬜ **Faster Data Collection with Batched GraphQL**: Fetch branches, comparisons, PRs and last commits in a few batched GraphQL queries instead of several REST calls per branch (the compare endpoint is currently called twice per branch), to cut run time and API rate-limit use.
- ⬜ **Reporter Test Suite in CI**: Run the report steps against fixture data with a stubbed `gh`, check the generated summary, CSV and JSON, and validate the Mermaid diagrams on every PR that touches `.github/`.
- ⬜ **One-Click Cleanup & Sync Workflow**: A dispatchable workflow that reads the cleanup and sync candidates from the report artifact and deletes or syncs those branches, with a dry run by default, instead of copying commands by hand.
- ⬜ **Dormant Repo Archive Suggestions**: List repos with no commits on the default branch for over a year, with a ready-to-run `gh repo archive` command, to keep the active set focused. Most repos are dormant today.
- ⬜ **Default Branch Protection & Repo Hygiene Audit**: Flag repos whose default branch isn't protected or doesn't require PRs, and repos missing a description, README, license or Dependabot.
- ⬜ **CI Health on Default Branches**: Show the latest workflow run result on each repo's default branch (✅ passing, ❌ failing or none) next to its activity.
- ⬜ **Improvement Cycle Metrics**: For each monthly cycle, track how many improvement branches got commits, how many were merged, and the median days from branch creation to merge.

### Experiment: GitHub automation with a typed SDK (.NET first, then Java)

Port part of the reporter to a typed SDK, so GitHub data is handled as objects
(repositories, branches, pull requests, workflow runs) instead of `gh api` text output.
GitHub publishes official SDKs for .NET but not for Java, so .NET comes first. Options at
the time of writing (October 2026):

#### .NET (official GitHub SDKs)

| Option | What it gives you |
| --- | --- |
| [Octokit.NET](https://github.com/octokit/octokit.net) ([`Octokit`](https://www.nuget.org/packages/Octokit) on NuGet) | GitHub's official, hand-written .NET client and the most mature choice: a typed, async REST client for repos, branches, PRs, workflow runs and GitHub App auth. Latest release v14.0.0 (January 2025); the repo still receives commits |
| [Octokit.GraphQL](https://github.com/octokit/octokit.graphql.net) ([`Octokit.GraphQL`](https://www.nuget.org/packages/Octokit.GraphQL) on NuGet) | GitHub's official GraphQL client for .NET: strongly typed, LINQ-style queries, a good fit for the reporter's GraphQL calls. Still in beta (v0.4.0-beta, April 2024) |
| [GitHub .NET SDK](https://github.com/octokit/dotnet-sdk) ([`GitHub.Octokit.SDK`](https://www.nuget.org/packages/GitHub.Octokit.SDK) on NuGet) | GitHub's official SDK generated with Kiota from the REST API's OpenAPI description, so it covers every endpoint. Pre-1.0 (v0.0.31, December 2024) with no commits since March 2025, so treat it as paused |
| [.NET 10 file-based apps](https://devblogs.microsoft.com/dotnet/announcing-dotnet-run-app/) | Run a single `.cs` file with `dotnet run report.cs` and pull in packages with `#:package Octokit@<version>`, with no project file. A lightweight way to use the SDKs from a workflow step |
| [Actions toolkit for C#](https://github.com/IEvangelist/actions-toolkit-csharp) ([`GitHub.Actions.Core`](https://www.nuget.org/packages/GitHub.Actions.Core) on NuGet) | Community port of GitHub's actions toolkit, for writing the action itself in C#: typed inputs, outputs, step summary and logging. Not an official GitHub or Microsoft product. Latest release 10.0 (December 2025), and its package IDs are moving to `ActionsTool` |

Official references:

- [GitHub Docs: Libraries for the REST API](https://docs.github.com/en/rest/using-the-rest-api/libraries-for-the-rest-api), which lists Octokit.NET as an official library
- [GitHub blog: Our move to generated SDKs](https://github.blog/news-insights/product-news/our-move-to-generated-sdks/), which introduced the Kiota-based .NET and Go SDKs
- [GitHub blog: Bringing GraphQL to Octokit.NET](https://github.blog/news-insights/the-library/graphql-for-octokit/)
- [Microsoft Learn: Tutorial: Create a GitHub Action with .NET](https://learn.microsoft.com/dotnet/devops/create-dotnet-github-action)

#### Java (community libraries)

| Option | What it gives you |
| --- | --- |
| [Quarkus GitHub Action](https://docs.quarkiverse.io/quarkus-github-action/dev/index.html) (`io.quarkiverse.githubaction:quarkus-github-action`, 2.x) | Write the action itself in Java. An `@Action` method gets injected GitHub REST and GraphQL clients, the run context, typed inputs, and commands for outputs and the step summary. It can also build a native executable |
| [GitHub API for Java](https://github.com/hub4j/github-api) (`org.kohsuke:github-api`) | Mature object model for the REST API: repos, branches, PRs, workflow runs and GitHub App auth. Stable 1.x line, with 2.0 in release candidates. Quarkus GitHub Action builds on it |
| Kiota-generated client | [Kiota](https://github.com/microsoft/kiota) can generate a Java client from GitHub's [OpenAPI description](https://github.com/github/rest-api-description). It covers every endpoint but is lower level |

Avoid `spotify/github-java-client`: its maintainers stopped support at the end of January 2026.

#### Suggested first step

Write a .NET 10 file-based app that uses Octokit.NET (plus Octokit.GraphQL for the monthly
commit counts) to rebuild `main_branches.json`. Run it in a workflow step after
`actions/setup-dotnet`, then compare its output with the reporter's artifact. For a Java
version, a picocli command in [java-utilities](../java-utilities/) using GitHub API for
Java fits the existing Java 25 build and CI.
