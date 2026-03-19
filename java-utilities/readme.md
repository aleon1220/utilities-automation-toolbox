# java-utilities

## WorkLog markdown

WorkLogConfig is a Picocli-based CLI automation tool that generates daily markdown worklog files between a start and end date.

# 📘 WorkLogConfig – Core Responsibilities & Usage Guide

## Core Responsibilities

### **CLI Argument Parsing**

The tool uses **Picocli** to accept command‑line arguments:

| Option          | Description                   |
| --------------- | ----------------------------- |
| `--start`, `-s` | Start date (LocalDate format) |
| `--end`, `-e`   | End date (LocalDate format)   |
| `--help`, `-h`  | Displays the help message     |

***

## **Markdown File Generation**

The utility generates **one `.md` file per business day** within the given date range.

### **File Name Format**

    yyyy-MM-dd-EEEE.md

Example:

    2026-03-17-Tuesday.md

### **Content Behavior**

* Inserts a **title header** for the date.
* Inserts the **daily worklog markdown template**.
* When date falls on a **Friday**, the tool appends an additional: section *Reflection, Learning & Next Goals*
* skips Weekends (Saturday & Sunday)
* skips New Zealand Public Holidays (hardcoded for 2026)

***

## 🧪 Unit Test Build & Run

#### **Clean Build**

```bash
gradle clean build 2>&1
```

#### **List Build Output**

```bash
ls -lh lib/build
```

#### **Run Help Command**
```bash
gradle run --args="--help"
```

#### set start and end date
```bash
START_DATE="2026-03-10"
END_DATE="2026-03-21"
```

#### **Run With Dates**

```bash
gradle run --args="--start $START_DATE --end $END_DATE"
```

***

## 🚀 Release Execution
- build Fat jar
```bash
./gradlew clean build shadowJar
```

- After building your fat/uber JAR (e.g., `appJavaUtils-all.jar`):

```bash
java -jar appJavaUtils-all.jar --start $START_DATE --end $END_DATE
```
