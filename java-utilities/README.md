# java-utilities

a java CLI app with some operations i run on-demand, weekly or randomly.

## WorkLog markdown

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

## 🚀  Executing the Java utilities

### Release Execution Fat Jar

simplified local build with the gradle wrapper to fix a specific gradle version

* build Fat jar

```bash
./gradlew clean build shadowJar
```

* output location `ls -lha lib/build/libs/`
* the java archie with all dependencies fat/uber JAR is `appJavaUtils-all.jar`
* copy file to execution sandbox if using WSL from Windows11

```bash
EXECUTION_SANDBOX="/mnt/c/workspace/TESTS/"
cp lib/build/libs/appJavaUtils-all.jar $EXECUTION_SANDBOX
pushd $EXECUTION_SANDBOX
```

* smoke test the execution

```bash
java -jar appJavaUtils-all.jar --this-week --dryrun
```

### 🧪 local Development Unit Test Build & Run

perform the steps locally for development purposes. I used WSL ubuntu 24 running from an enterprise windows11.

* navigate to gradle project directory

```bash
pushd ./java-utilities
```

### **Clean Build**

```bash
gradle clean build 2>&1
```

### **List Build Output**

```bash
ls -lh lib/build
```

### **Run Help Command**

```bash
gradle run --args="--help"
```

#### set start and end date

```bash
START_DATE="2026-03-10"
END_DATE="2026-03-21"
```

#### **Run With Dry Run (Mock Execution)**

calculates starting the first business day of the week.

```bash
gradle run --args="--start $START_DATE --end $END_DATE --dryrun"
```

***

## CI with github actions

### Testing suite

#### Smoke test

```bash
./gradlew test --tests "org.utils.WorkLogConfigTest" --info | grep "testDryRun"
```
