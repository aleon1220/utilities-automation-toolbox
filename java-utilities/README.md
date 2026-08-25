# java-utilities

A collection of Java CLI utilities designed to automate repetitive tasks.

This project is part of the `utilities-automation-toolbox` and provides a set of operations that can be run on-demand, weekly, or randomly to streamline developer workflows.

Currently, the primary tool included is the **WorkLog markdown generator**, which automates the creation of daily work logs in markdown format.

## CLI utility WorkLog markdown

WorkLog Markdown is a Picocli-based CLI automation tool used to generate markdown worklog files on demand using New Zealand Holidays.

uses the format **File Name Format yyyy-MM-dd-EEEE.md** Example: `2026-03-17-Tuesday.md`

## 📘 WorkLogConfig – Core & Usage Guide

## Core Responsibilities

### **CLI Argument Parsing**

The tool uses **Picocli** to accept command‑line arguments:

| Option                 | Description                                                                     |
| ---------------------- | ------------------------------------------------------------------------------- |
| `-s`, `--start`        | Start date `YYYY-MM-DD`                                                         |
| `-e`, `--end`          | End date `YYYY-MM-DD`                                                           |
| `-t`, `--this-week`    | Creates WorkLogs for this work week (Monday to Friday)                          |
| `-d`, `--dryrun`       | Safely execute and mock the execution                                           |
| `-o`, `--out`          | Base output directory (defaults to `/mnt/c/workspace/TESTS`)                   |
| `-a`, `--append`       | Target markdown file path to append content to                                  |
| `-c`, `--content`      | Markdown content to append                                                      |
| `-h`, `--help`         | Displays the help message and execution examples                                |

***

## **Markdown File Generation & Content Appending**

The utility generates 1 **markdown `.md` file per business day** within the given date range, or appends notes to an existing markdown file.

### **Content Behavior**

* Inserts a **title header** for the date.
* Inserts the **daily worklog markdown template**.
* When date falls on a **Friday**, the tool appends additional sections:
  * **Reflection**
  * **Learning & Next Goals**
* Skips Weekends (Saturday & Sunday)
* Skips New Zealand Public Holidays (hardcoded for 2026)
* Supports **appending markdown content** with a timestamp header using `--append` (`-a`) and `--content` (`-c`) flags.

***

## 📋 Prerequisites

Before building or running the utilities, ensure you have the following installed

* **Java JDK 25**: The project uses Java 25 toolchain features.
* **Gradle**: While the project includes a Gradle wrapper (`gradlew`), having Gradle installed locally can be helpful.
* **WSL (Optional)**: Highly recommended if you are developing on Windows 11, as the scripts and commands are optimized for a Linux-like environment (e.g., Ubuntu 24).

***

## Signed release process 📟📦

the process is best executed in an environment that supports gpg for signing with encryption keys the tag/commit

* check current version

 ```bash
 git tag
 ```

* run the gradle function prepared to handle the hybrid release

 ```bash
 ./gradlew hybridRelease
 ```

* the CI workflow ⚙️ [Java Utilities CI/CD](https://github.com/aleon1220/utilities-automation-toolbox/actions/workflows/ci-cd-java-utilities.yml) runs automatically as soon as the git tag is pushed
* [Github release](https://github.com/aleon1220/utilities-automation-toolbox/releases) is automatically created with the jar library

## 🚀 Executing the Java utilities

### Local Execution & Release Fat Jar

execute from main branch and root gradle project directory. Simplified local build with the gradle wrapper to fix a specific gradle version

* check current version

 ```bash
 ./gradlew currentVersion
 ```

* gradle plugin help
  
 ```bash
 ./gradlew -q help --task shadowJar
 ```

* build Fat jar

 ```bash
 ./gradlew clean build shadowJar
 ```

* output location `ls -lha ./java-utilities/lib/build/libs/`
* the java archive with all dependencies fat/uber JAR is versioned e.g. `lib-0.1.1-all.jar`
* obtain the current version

  ```bash
  UTIL_APP_VERSION=$(./gradlew getVersion --quiet)
  UTIL_JAR_NAME="lib-$UTIL_APP_VERSION-all.jar"
  ```

* copy file to execution sandbox if using WSL from Windows11

  ```bash
  EXECUTION_SANDBOX="/mnt/c/workspace/TESTS/"
  cp ./java-utilities/lib/build/libs/$UTIL_JAR_NAME $EXECUTION_SANDBOX
  ```

* navigate to sandbox

  ```bash
  pushd $EXECUTION_SANDBOX
  ```

* smoke test the execution

  ```bash
  java -jar $UTIL_JAR_NAME --this-week --dryrun
  ```

* copy or move the file to your target workspace directory

todo: implement a dynamic way to identify whether windows or linux

### Fetch and Run Latest Java archive from GitHub Release

You can download and execute the latest release directly by fetching the version via the GitHub API and running it in the standardized sandbox pattern:


* Set up the execution sandbox

```bash
EXECUTION_SANDBOX="/mnt/c/workspace/TESTS/"
mkdir -pv "$EXECUTION_SANDBOX"
pushd "$EXECUTION_SANDBOX" || exit
```


* Fetch the latest release version tag from GitHub API

```bash
LATEST_VERSION=$(curl -s https://api.github.com/repos/aleon1220/utilities-automation-toolbox/releases/latest | grep '"tag_name":' | sed -E 's/.*"([^"]+)".*/\1/')

UTIL_JAR_NAME="lib-${LATEST_VERSION#v}-all.jar"
```

* Download the latest release JAR

```bash
wget --verbose "https://github.com/aleon1220/utilities-automation-toolbox/releases/download/${LATEST_VERSION}/${UTIL_JAR_NAME}"
```

* use `curl` instead of `wget`

```bash
curl --verbose --location --remote-name "https://github.com/aleon1220/utilities-automation-toolbox/releases/download/${LATEST_VERSION}/${UTIL_JAR_NAME}"
```

* Smoke test the execution

```bash
java -jar "$UTIL_JAR_NAME" --this-week --dryrun
```

### 🧪 local Development Build Test & Run

#### Build stage

perform the steps locally for development purposes from the project directory. I used WSL ubuntu 24 running from an enterprise windows11.

* run a clean Build

  ```bash
  ./gradlew clean build 2>&1
  ```

* List Build Output

  ```bash
  ls -lh java-utilities/lib/build
  ```

* Smoke test by running Help Command

  ```bash
  ./gradlew run --args="--help"
  ```

### Testing Suite

#### Test stage

The testing suite focuses on verifying the core logic of the utilities without side effects (using dry runs)

#### run the test suite

  ```bash
  ./gradlew test jacocoTestReport jacocoTestCoverageVerification
  ```

#### run the test suite only for java-utilities sub-project

  ```bash
  ./gradlew :java-utilities:lib:test :java-utilities:lib:jacocoTestReport :java-utilities:lib:jacocoTestCoverageVerification
  ```

#### Smoke Test a single test

The smoke test validates the CLI configuration and basic execution flow:

  ```bash
  ./gradlew test --tests "org.utils.WorkLogConfigTest" --info | grep "testDryRun"
  ```

#### Execution stage

* set start date from today and end date

  ```bash
  START_DATE="$(date +%F)"
  END_DATE="2026-06-25"
  ```

* use today and tomorrow bash style

  ```bash
  START_DATE="$(date +%F)"
  END_DATE="$(date --date="tomorrow" +%F)"
  ```

* Run With Dry Run (Mock Execution)

  ```bash
  ./gradlew run --args="--start $START_DATE --end $END_DATE --dryrun"
  ```

* Run with flags long format and short format

  ```bash
  ./gradlew run --args="-s 2026-05-20 --end=2026-05-23"
  ```

***

## ⚙️ CI with GitHub Actions

The project includes a CI pipeline to ensure code quality and functionality. The workflow is designed to validate changes through automated testing.

The CI pipeline typically performs the following steps:

1. **Checkout Code**: Pulls the latest version of the repository.
2. **Setup Java**: Configures the environment with JDK 25.
3. **Build**: Compiles the code and generates the Fat JAR.
4. **Test**: Runs the unit tests and reports results.
