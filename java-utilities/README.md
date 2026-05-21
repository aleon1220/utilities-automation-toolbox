# java-utilities

A collection of Java CLI utilities designed to automate repetitive tasks. This project is part of the `utilities-automation-toolbox` and provides a set of operations that can be run on-demand, weekly, or randomly to streamline developer workflows.

Currently, the primary tool included is the **WorkLog markdown generator**, which automates the creation of daily work logs in markdown format.

## CLI utility WorkLog markdown

WorkLog is a Picocli-based CLI automation tool that generates daily markdown worklog files between a start and end date.

uses the format **File Name Format yyyy-MM-dd-EEEE.md** Example: `2026-03-17-Tuesday.md`

## 📘 WorkLogConfig – Core Responsibilities & Usage Guide

## Core Responsibilities

### **CLI Argument Parsing**

The tool uses **Picocli** to accept command‑line arguments:

| Option              | Description                                                                     |
| ------------------- | ------------------------------------------------------------------------------- |
| `--start`, `-s`     | Start date (YYYY-MM-DD format)                                                  |
| `--end`, `-e`       | End date (YYYY-MM-DD format)                                                    |
| `--this-week`, `-t` | Automatically creates logs from the start of this business week to the end      |
| `--dryrun`, `-d`    | Safely execute and mock the execution without creating any files                |
| `--help`, `-h`      | Displays the help message                                                       |

***

## **Markdown File Generation**

The utility generates **one `.md` file per business day** within the given date range.

### **Content Behavior**

* Inserts a **title header** for the date.
* Inserts the **daily worklog markdown template**.
* When date falls on a **Friday**, the tool appends an additional sections
  * **Reflection**
  * **Learning & Next Goals**
* skips Weekends (Saturday & Sunday)
* skips New Zealand Public Holidays (hardcoded for 2026)

***

## 📋 Prerequisites

Before building or running the utilities, ensure you have the following installed:

* **Java JDK 25**: The project uses Java 25 toolchain features.
* **Gradle**: While the project includes a Gradle wrapper (`gradlew`), having Gradle installed locally can be helpful.
* **WSL (Optional)**: Highly recommended if you are developing on Windows 11, as the scripts and commands are optimized for a Linux-like environment (e.g., Ubuntu 24).

***

## 🚀 Executing the Java utilities

### Local Release Execution Fat Jar

execute from main branch and root gradle project directory. Simplified local build with the gradle wrapper to fix a specific gradle version

* check current version
 
 ```bash
 git tag
 ./gradlew currentVersion
 ```

* build Fat jar

 ```bash
 ./gradlew clean build shadowJar
 ```

* gradle plugin help
  
 ```bash
 ./gradlew -q help --task shadowJar
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

### 🧪 local Development Unit Test Build & Run

perform the steps locally for development purposes. I used WSL ubuntu 24 running from an enterprise windows11.

* navigate to gradle project directory
* run a clean Build

 ```bash
 gradle clean build 2>&1
 ```

* List Build Output

 ```bash
 ls -lh lib/build
 ```

* Smoke test by running Help Command

 ```bash
 ./gradlew run --args="--help"
 ```

* set start and end date

 ```bash
 START_DATE="2026-03-10"
 END_DATE="2026-03-21"
 ```

* Run With Dry Run (Mock Execution)

calculates starting the first business day of the week.

 ```bash
 ./gradlew run --args="--start $START_DATE --end $END_DATE --dryrun"
 ```

* Run with flags long format and short format

./gradlew run --args="-s 2026-05-20 --end=2026-05-23"

***

## ⚙️ CI with GitHub Actions

The project includes a CI pipeline to ensure code quality and functionality. The workflow is designed to validate changes through automated testing.

### Testing Suite

The testing suite focuses on verifying the core logic of the utilities without side effects (using dry runs)

#### run the test suite

```bash
./gradlew :java-utilities:lib:test :java-utilities:lib:jacocoTestReport :java-utilities:lib:jacocoTestCoverageVerification
```

#### Smoke Test Execution

The smoke test validates the CLI configuration and basic execution flow:

```bash
./gradlew test --tests "org.utils.WorkLogConfigTest" --info | grep "testDryRun"
```

The CI pipeline typically performs the following steps:

1. **Checkout Code**: Pulls the latest version of the repository.
2. **Setup Java**: Configures the environment with JDK 25.
3. **Build**: Compiles the code and generates the Fat JAR.
4. **Test**: Runs the unit tests and reports results.
