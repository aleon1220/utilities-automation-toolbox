package org.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

// The @CommandLineSchema annotation tells the JVM how to map args to this record
@Command(name = "worklog", description = "%nWork log markdown file creator%n", footer = {
        "%nExamples:",
        "  Create logs for March 2026:",
        "    worklog --start 2026-03-01 --end 2026-03-31",
        "    worklog -s 2026-03-01 -e 2026-03-31",
        "  Create logs for the current work week:",
        "    worklog --this-week",
        "    worklog -t",
        "  Append content to a markdown file:",
        "    worklog --append /path/to/file.md --content \"Completed ticket PROJ-123\"",
        "    worklog -a /path/to/file.md -c \"Daily notes\""
}, sortOptions = false, requiredOptionMarker = '*', showDefaultValues = true)

public class WorkLogConfig implements Runnable {
    @Option(names = { "-s", "--start" }, description = "Start date YYYY-MM-DD")
    Optional<LocalDate> startDate = Optional.empty();

    @Option(names = { "-e", "--end" }, description = "End date")
    Optional<LocalDate> endDate = Optional.empty();

    @Option(names = { "-t", "--this-week" }, description = "creates WorkLogs for this work week")
    boolean thisWeek;

    @Option(names = { "-d", "--dryrun" }, description = "safely execute and mock the execution")
    boolean dryrun;

    @Option(names = { "-a", "--append" }, description = "Target markdown file path to append content to")
    Optional<String> appendFile = Optional.empty();

    @Option(names = { "-c", "--content" }, description = "Markdown content to append")
    Optional<String> appendContent = Optional.empty();

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "worklog Show this help message and exit")
    boolean help;

    @Option(names = { "-o", "--out" }, description = "Base output directory", defaultValue = "/mnt/c/workspace/TESTS")
    String baseOutputDir;

    static String textFridayTemplate = """

            ## End of week Reflection 
            
            1. week_reflection
            2. week_learning
            3. next_week_goal

            """;

    static String loadResource(String resourcePath) {
        try (var in = WorkLogConfig.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null)
                throw new IllegalStateException("Template not found on classpath: " + resourcePath);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read resource: " + resourcePath, e);
        } // end of catch
    } // end of loadResource()

    static String formatDateForFileName(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-EEEE");
        return formatter.format(date);
    }

    public void createMarkdownFiles() {
        if (startDate.isEmpty() || endDate.isEmpty()) {
            System.out.println("======= Start and end dates are required");
            return;
        }
        createMarkdownFiles(startDate.get());
    }

    public void createMarkdownFiles(LocalDate startDate) {
        if (!isValidDateRange(startDate, endDate.get())) {
            System.out.println("======= Invalid date range");
            return;
        }

        try {
            if (dryrun) {
                System.out.println("🧪 ======= DRY RUN MODE ENABLED =======");
            }

            Path outputDir = resolveOutputDirectory();

            for (LocalDate date = startDate; !date.isAfter(endDate.get()); date = date.plusDays(1)) {

                if (isWeekend(date)) {
                    System.out.println("======= Skipping Weekend for " + formatDateForFileName(date));
                    continue;
                }

                if (isNZHoliday(date)) {
                    System.out.println("======= Skipping new Zealand Holiday " + Holidays.getNZHolidayName(date));
                    continue;
                }

                var standardizedDateName = formatDateForFileName(date);
                String fileName = standardizedDateName + ".md";
                Path filePath = outputDir.resolve(fileName);

                if (dryrun) {
                    System.out.printf("======= 📝 [DRY RUN] Would create file %s at path %s %n", fileName,
                            filePath);
                    if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                        System.out.printf("======= 🛠️ [DRY RUN] Would add Friday Reflection block to file %s %n",
                                fileName);
                    }
                    continue;
                } // end of dryrun check // end of dryrun check

                var template = loadResource("templates/worklog-day.md");
                var fullMarkdownContent = template.replace("{{title_date}}", standardizedDateName);

                Files.writeString(filePath, fullMarkdownContent);

                if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                    System.out.printf("======= Friday includes extra Reflection section");
                    try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                            java.nio.file.StandardOpenOption.APPEND)) {
                        System.out.printf("======= Friday includes a Reflection section");
                        writer.write(textFridayTemplate);
                        System.out.printf("======= 🔀 Friday Reflection block added to file %s %n", fileName);
                    }
                } // end of if block checking for Friday to add reflection template

                System.out.printf("======= ✅ Created file %s at path %s %n", fileName, filePath);
            } // end of for loop iterating over dates
        } catch (IOException e) {
            System.err.println("Error creating markdown files: " + e.getMessage());
            e.printStackTrace();
        } // end of catch block
    } // end of createMarkdownFiles()

    public static void addContentToMarkdownFile(String overrideMarkdownFilePath, String xtraMarkdownContent) {
        if (overrideMarkdownFilePath == null || overrideMarkdownFilePath.isBlank()) {
            System.err.println("======= Error: File path cannot be null or empty");
            return;
        }
        addContentToMarkdownFile(Path.of(overrideMarkdownFilePath), xtraMarkdownContent);
    }

    public static void addContentToMarkdownFile(Path filePath, String xtraMarkdownContent) {
        if (filePath == null) {
            System.err.println("======= Error: File path cannot be null");
            return;
        }
        try {
            var headerWorkLogDayFormatted = String.format("## %s appending %n", formatDateForFileName(LocalDate.now()));
            String contentToAppend = headerWorkLogDayFormatted + (xtraMarkdownContent != null ? xtraMarkdownContent : "") + System.lineSeparator();
            System.out.println("======= ⏭️ markdown content to add: " + headerWorkLogDayFormatted + (xtraMarkdownContent != null ? xtraMarkdownContent : ""));

            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            Files.writeString(filePath, contentToAppend, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("======= 📝 markdown override added to file " + filePath);
        } catch (IOException e) {
            System.err.println("======= Error appending to file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    static boolean validateDateRange(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            System.out.println("======= Date Error Validation: endDate cannot be before startDate");
            return false;
        }

        long daysBetween = ChronoUnit.DAYS.between(start, end);
        if (daysBetween > 30) {
            System.out.println("======= Error: Date range cannot span more than a Month");
            return false;
        }
        return true;
    } // end of validateDateRange()

    public static boolean isValidDateRange(LocalDate start, LocalDate end) {
        var result = performDateValidation(start, end);
        if (result.hasError()) {
            System.out.println("======= " + result.errorMessage());
        }
        return !result.hasError();
    }

    record DateValidationResult(boolean valid, String errorMessage) {
        boolean hasError() {
            return !valid;
        }
    }

    static DateValidationResult performDateValidation(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            return new DateValidationResult(false,
                    "Date Error Validation: endDate cannot be before startDate");
        }

        long daysBetween = ChronoUnit.DAYS.between(start, end);
        if (daysBetween > 30) {
            return new DateValidationResult(false,
                    "Error: Date range cannot span more than a Month");
        }

        return new DateValidationResult(true, "");
    }

    static boolean isNZHoliday(LocalDate date) {
        // Returns TRUE if it IS a holiday (Simple check)
        return Holidays.isNZHoliday(date);
    }

    static boolean isWeekend(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case SATURDAY, SUNDAY -> true;
            // Returns FALSE for Mon-Fri
            default -> false;
        };
    }

    @Override
    public void run() {
        if (appendFile.isPresent() && appendContent.isPresent()) {
            if (dryrun) {
                System.out.println("🧪 ======= DRY RUN MODE ENABLED =======");
                System.out.printf("======= 📝 [DRY RUN] Would append content to %s%n", appendFile.get());
                System.out.printf("======= ⏭️ [DRY RUN] Content: %s%n", appendContent.get());
                return;
            }
            addContentToMarkdownFile(appendFile.get(), appendContent.get());
            return;
        }

        if (appendFile.isPresent() || appendContent.isPresent()) {
            System.out.println("======= Error: Both --append (-a) and --content (-c) are required to append content.");
            new CommandLine(this).usage(System.out);
            return;
        }

        if (thisWeek) {
            LocalDate now = LocalDate.now();
            startDate = Optional.of(now.with(DayOfWeek.MONDAY));
            endDate = Optional.of(now.with(DayOfWeek.FRIDAY));
        }

        if (startDate.isPresent() && endDate.isPresent()) {
            createMarkdownFiles(startDate.get());
        } else {
            System.out.println("======= Start - end dates are require. Use --this-week");
            new CommandLine(this).usage(System.out);
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new WorkLogConfig()).execute(args);
        // todo: add logs that show the output directory and file names being
        System.exit(exitCode);
    }

    private Path resolveOutputDirectory() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        Path dir = Path.of(baseOutputDir, today);

        if (dryrun) {
            System.out.println("======= 🛠️ [DRY RUN] for better visibility and debugging OutputDirectory");
            System.out.println("======= 🛠️ [DRY RUN] Would ensure output directory exists: " + dir);
            return dir;
        }

        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create output directory: " + dir, e);
        }
        return dir;
    } // end of resolveOutputDirectory()
} // end of Class
