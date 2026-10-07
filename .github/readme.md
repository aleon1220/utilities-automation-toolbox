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

Roadmap for the Repos Reporter. Checked items are done and can be turned on with
the input shown.

- [x] **Stale Branch Age Tracking** (`stale_branch_age`): Show time since the last commit on each improvement branch to highlight abandoned branches.
- [x] **Automated PR Status & Direct Links** (`pr_status`): Check if an open PR exists for each improvement branch, with direct links and status indicators.
- [x] **Direct Diff & Comparison Deep-Links** (`diff_links`): Include one-click GitHub comparison links (`main...branch`) for quick diff inspection.
- [x] **Behind-Main & Conflict Risk Indicators** (`behind_main`, `behind_threshold`): Flag branches that are significantly behind the default branch (`behind_by > 10` by default) to preempt merge conflicts.
- [x] **Interactive Automated Cleanup Dispatch** (`cleanup_dispatch`): List merged or obsolete improvement branches from previous months with delete commands.
- [x] **Report Artifact Export (JSON/CSV)** (always on): Export raw and aggregated metrics as downloadable workflow artifacts for historical tracking and trend analysis.
- [x] **Main Branch Trend Across Repos** (always on): Chart monthly commits to every repo's default branch, with per-repo activity and trend.
- [ ] **Multi-Channel Notifications**: Send the generated report via webhook to Slack, Microsoft Teams or Discord.
- [ ] **Multi-Owner Reports**: Report on a personal account and several orgs in a single run.
- [ ] **Cross-Run History**: Read previous runs' `summary.json` artifacts to chart improvement-branch and conflict-risk trends over time.
