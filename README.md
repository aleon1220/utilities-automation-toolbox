# Utilities & Automation Toolbox

🚀 **Java Utilities**

[![Release](https://img.shields.io/github/v/release/aleon1220/utilities-automation-toolbox?display_name=tag&sort=semver&label=📦%20Latest%20Release&color=blue)](https://github.com/aleon1220/utilities-automation-toolbox/releases/latest)
[![CI/CD github actions Status](https://github.com/aleon1220/utilities-automation-toolbox/actions/workflows/ci-cd-java-utilities.yml/badge.svg?branch=main)](https://github.com/aleon1220/utilities-automation-toolbox/actions/workflows/ci-cd-java-utilities.yml)

A collection of Bash, Java, PowerShell, Python utilities

## GitHub Actions Orchestrator

This repo operates as the orchestrator repo for other git repos.

## Structure



- [java-utilities](java-utilities)
- [powershell-utilities](powershell-utilities/)
- [python-utilities](python-utilities/)

### java-utilities release new version

1. Merge PR to main branch
2. check tasks `./gradlew tasks --all`
3. From a linux machine or WSL with a GPG key create the new tag
4. from Github validate tag
5. GH Actions trigger the workflow CI/CD and release happens automatically

## Notes

- from windows 11 enterprise edition I have a WSL layer with Linux Ubuntu distro
- from windows 10 PC I have a WSL layer with Linux RHEL
- Lenovo Laptop with Ubuntu 24
- Fixed `WorkLogConfig` tests by adding method overload for backward compatibility.
- Implemented CLI footer examples for `--this-week` in `WorkLogConfig`.
