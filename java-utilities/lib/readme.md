# WorkLog Library (`java-utilities/lib`)

`org.utils` is a Java utility library designed for automated work log markdown creation, public holiday filtering, date range calculations, and markdown content append operations.

---

## Architecture & Code Structure

The library consists of three main components under `src/main/java/org/utils`:

- **`WorkLogConfig.java`**: Core CLI command and markdown file generator. Uses [Picocli](https://picocli.info/) for command-line parsing and implements `Runnable` to handle command execution (`run()` override).
- **`Holidays.java`**: Extracted reference dataset for New Zealand public holidays (`NZ_HOLIDAYS_2026`). Provides helper functions `isNZHoliday(LocalDate)` and `getNZHolidayName(LocalDate)`.
- **`WorkLogDisplay.java`**: Diagnostic display utility for rendering formatted holiday schedules, counting days remaining until the end of the month, and calculating ISO/NZ week numbers using `WeekFields`.

---

## Picocli CLI & Run Override (`WorkLogConfig`)

### Command Definition & CLI Options

`WorkLogConfig` is annotated with `@Command` to configure CLI execution metadata and usage examples:

| Option | Long Flag | Description | Default / Type |
|---|---|---|---|
| `-s` | `--start` | Start date (`YYYY-MM-DD`) | `Optional<LocalDate>` |
| `-e` | `--end` | End date (`YYYY-MM-DD`) | `Optional<LocalDate>` |
| `-t` | `--this-week` | Automatically creates WorkLogs for the current work week (Monday to Friday) | `boolean` |
| `-d` | `--dryrun` | Safely mocks file creation without writing to disk | `boolean` |
| `-a` | `--append` | Target markdown file path to append content to | `Optional<String>` |
| `-c` | `--content` | Markdown content to append | `Optional<String>` |
| `-o` | `--out` | Base output directory | `/mnt/c/workspace/TESTS` |
| `-h` | `--help` | Display usage help message | `boolean` |

### `run()` Method Override Logic

When executed via Picocli (`new CommandLine(new WorkLogConfig()).execute(args)`), the `run()` override follows a clear control flow:

1. **Append Mode**: If both `--append` (`-a`) and `--content` (`-c`) are provided:
   - In dry-run mode (`--dryrun`), outputs mock append logs without mutating files.
   - Otherwise, invokes `addContentToMarkdownFile(String, String)` to format a daily header (`## YYYY-MM-DD-Day appending`) and append the content.
   - If only one append flag is supplied, displays an error and usage help.
2. **Current Week Resolution**: If `--this-week` (`-t`) is set, automatically computes `startDate` as current week's Monday and `endDate` as current week's Friday.
3. **Date Range Validation & File Generation**:
   - Validates that `endDate` is not before `startDate` and the span does not exceed 30 days via `performDateValidation`.
   - Iterates through each date in range:
     - **Weekend Skip**: Skips Saturday and Sunday via `isWeekend(date)`.
     - **Holiday Skip**: Skips NZ public holidays via `Holidays.isNZHoliday(date)`.
     - **Friday Reflection Block**: Appends `textFridayTemplate` containing weekly reflection questions (`## End of week Reflection`).
     - **Output Directory Creation**: Ensures the target directory (`baseOutputDir/YYYY-MM-DD`) is created.

---

## Javadoc Generation Instructions

To generate complete HTML Javadoc documentation for the library, execute the Gradle task from the workspace root in WSL:

```bash
./gradlew :java-utilities:lib:javadoc
```

The generated Javadoc files will be located at:
```
java-utilities/lib/build/docs/javadoc/index.html
```

---

## Running Smoke Tests & Verification

### Unit Test Suite

The library includes automated JUnit 5 and AssertJ tests located in `src/test/java/org/utils/WorkLogConfigTest.java`.

To execute all tests via Gradle in WSL:

```bash
./gradlew :java-utilities:lib:test
```

### Interactive Smoke Testing

You can run smoke tests against the CLI directly using Gradle:

```bash
# Display help menu
./gradlew :java-utilities:lib:run --args="--help"

# Perform a dry-run for the current work week
./gradlew :java-utilities:lib:run --args="--this-week --dryrun"

# Perform a dry-run append operation
./gradlew :java-utilities:lib:run --args="-a /tmp/test.md -c 'Smoke test note' --dryrun"
```

---

## Future Work

- [ ] **Enhanced Logging**: Add detailed logger output showing output directories and exact generated file names upon execution completion.
- [ ] **Dynamic & Multi-Year Holiday Support**: Expand `Holidays.java` to dynamically calculate Easter/movable holidays or support multi-year / multi-region holiday sets.
- [ ] **Customizable Templates**: Allow users to specify external template files via CLI parameters instead of relying solely on built-in classpath resources.
- [ ] **Configurable Work Schedules**: Support custom work-week definitions (e.g. 4-day work weeks or custom weekend days).
- [ ] javadoc has 16 warnings
