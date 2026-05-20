# Utilities & Automation Toolbox

🚀 **Java Utilities**

[![Release](https://img.shields.io/github/v/release/aleon1220/utilities-automation-toolbox?display_name=tag&sort=semver&label=📦%20Latest%20Release&color=blue)](https://github.com/aleon1220/utilities-automation-toolbox/releases/latest)
[![CI/CD github actions Status](https://github.com/aleon1220/utilities-automation-toolbox/actions/workflows/ci-cd-java-utilities.yml/badge.svg?branch=main)](https://github.com/aleon1220/utilities-automation-toolbox/actions/workflows/ci-cd-java-utilities.yml)

A collection of Bash, Java, PowerShell, Python utilities

## GitHub Actions Orchestrator

This repo operates as the orchestrator repo for other git repos.

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

## Notes

- from windows 11 enterprise edition I have a WSL layer with Linux Ubuntu distro
- from windows 10 PC I have a WSL layer with Linux RHEL
- Lenovo Laptop with Ubuntu 24
