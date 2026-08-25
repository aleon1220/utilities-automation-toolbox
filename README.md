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
3. From a linux machine or WSL with a GPG key create the new tag: use gradle function `hybridRelease`
4. from Github validate tag
5. GH Actions trigger the workflow CI/CD and release happens automatically

## Notes

- from windows 11 enterprise edition I have a WSL layer with Linux Ubuntu distro
- from windows 10 PC I have a WSL layer with Linux RHEL
- Lenovo Laptop with Ubuntu 24
- Fixed `WorkLogConfig` tests by adding method overload for backward compatibility.
- Implemented CLI footer examples for `--this-week` in `WorkLogConfig`.
- Extracted holidays logic to a separate `Holidays` class for modularity.
- Simplified and generalized `addContentToMarkdownFile` with CLI options (`-a`/`--append` and `-c`/`--content`).

## Future Work

1. **Automated CI/CD Workflows for Python and PowerShell**: Expand GitHub Actions to include linting, static analysis, and automated unit testing for Python (using `pytest`/`ruff`) and PowerShell scripts (using `Pester`/`PSScriptAnalyzer`).
2. **Containerized Execution & Executable Packaging**: Package Java and Python CLI tools as standalone binaries or lightweight Docker containers (e.g., GraalVM Native Image, PyInstaller) for seamless execution across Windows, WSL, and Linux environments.
3. **Cross-Language Code Coverage & Quality Badges**: Integrate coverage reporting (e.g., JaCoCo, Codecov) and automated code quality checks into CI/CD pipelines to display status badges on the main README.
4. **Enhanced WorkLog & External API Integrations**: Extend `java-utilities` (`WorkLogConfig`) to support export options (JSON, CSV, iCal) and bi-directional integration with issue trackers such as Azure DevOps or Jira.
5. **Unified Toolbox CLI Gateway**: Create a central wrapper entry-point script (e.g., `toolbox` CLI in Bash and PowerShell) to standardize command execution across Java, Python, and PowerShell scripts.
6. **Automated Dependency Updates & Security Scanning**: Set up Dependabot or Renovate alongside GitHub Security Scanning to maintain Gradle plugins, Java libraries, and Python package dependencies automatically.
7. **Interactive Shell Auto-Completion & Documentation**: Generate auto-completion scripts for Bash, Zsh, and PowerShell CLI invocations, along with updated interactive usage guides in the module READMEs.

