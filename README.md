# Utilities & Automation Toolbox

🚀 **Java Utilities**

[![Release](https://img.shields.io/github/v/release/aleon1220/utilities-automation-toolbox?display_name=tag&sort=semver&label=📦%20Latest%20Release&color=blue)](https://github.com/aleon1220/utilities-automation-toolbox/releases/latest)
[![CI/CD Status](https://github.com/aleon1220/utilities-automation-toolbox/actions/workflows/ci-cd-java-utilities.yml/badge.svg?branch=main)](https://github.com/gh-aleon1220/utilities-automation-toolbox/actions/workflows/ci-cd-java-utilities.yml)

A collection of Bash, Java, PowerShell, Python utilities, GitHub Actions. This repo operates as the orchestrator repo for other git repos.

helpful if you add a WSL layer with Linux distro.

Some commands might have RHEL.

## Structure

where there is a native replacement e.g. `aws-cli` instead of python `boto3` it will be linked and notified.

- `java-utilities/`
- `powershell-utilities/`
- `python-utilities/`


## release new version java-utilities

1. Merge PR to main branch
2. check tasks `./gradlew tasks --all`
3. Run `./gradlew markNextVersion` locally
4. Run `./gradlew hybridRelease --no-configuration-cache` locally
5. Copy the git commands and run them.
6. GH Actions triggers and release happens automatically.