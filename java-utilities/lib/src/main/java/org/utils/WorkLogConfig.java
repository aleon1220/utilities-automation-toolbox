package org.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static java.time.Month.JANUARY;
import static java.time.Month.FEBRUARY;
import static java.time.Month.APRIL;
import static java.time.Month.JUNE;
import static java.time.Month.OCTOBER;
import static java.time.Month.DECEMBER;
import static java.util.Map.entry;
import java.util.Optional;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

// The @CommandLineSchema annotation tells the JVM how to map args to this record
@Command(name = "worklog", description = "%nWork log markdown file creator tool%n", footer = {
        "%nExample: create logs March 2026",
        "   worklog --start 2026-03-01 --end 2026-03-31",
        "   worklog -s 2026-03-01 -e 2026-03-31"
}, sortOptions = false, requiredOptionMarker = '*', showDefaultValues = true)

public class WorkLogConfig implements Runnable {
    @Option(names = { "-s", "--start" }, description = "Start date YYYY-MM-DD", required = true)
    Optional<LocalDate> startDate = Optional.empty();

    @Option(names = { "-e", "--end" }, description = "End date", required = true)
    Optional<LocalDate> endDate = Optional.empty();

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "worklog Show this help message and exit")
    boolean help;

    // markdown templates
    // todo: move to separate class or file if they get more complex to simplify
    public static String markdownWorkLogDayStructure = """
            # {{title_date}}
            ## GOALS

            1. Main Planning System
            2. [Trello](https://trello.com/c/63qYHZ9V)
            3. Client planning system

            ## QUESTIONS
            1. todo_add_question?

            ## MORNING

            ### Daily Standup
            1. ✅ What was done yesterday
              - **todo_done**
            2. 🔄 What is planned for today
              - **todo_planned**
            3. ❗ blockers & escalations
              - **todo_add_blocker**

            ## AFTERNOON
            ### TODO_activity_name
            1. todo_important_note

            ## WRAP UP DAY
            ## Tasks for next business day
            1. todo
            2. todo

            ### Day Reflection & Learning
            1. todo_add_reflection
            2. todo_add_learning

            ### Timesheet submission
            - NZ_Timesheet_Code todo_add

            """;

    // todo: add more holidays, and move to separate class or file if they get more
    // complex to simplify
    static LocalDate endOfMonth = LocalDate.now()
            .withDayOfMonth(LocalDate.now().getMonth().length(LocalDate.now().isLeapYear()));

    static final Map<LocalDate, String> HOLIDAYS_2026 = Map.ofEntries(
            entry(LocalDate.of(2026, JANUARY, 1), "New Year's Day"),
            entry(LocalDate.of(2026, FEBRUARY, 6), "Waitangi Day"),
            entry(LocalDate.of(2026, APRIL, 3), "Holy Week - Friday"),
            entry(LocalDate.of(2026, APRIL, 6), "Holy Week - Easter Monday"),
            entry(LocalDate.of(2026, APRIL, 25), "ANZAC Day"),
            entry(LocalDate.of(2026, JUNE, 1), "Queen's Birthday"),
            entry(LocalDate.of(2026, OCTOBER, 26), "Labour Day"),
            entry(LocalDate.of(2026, DECEMBER, 25), "Christmas Day"),
            entry(LocalDate.of(2026, DECEMBER, 26), "Boxing Day"));

    static String textFridayTemplate = """

            ## End of week Reflection | Learning & Next Goals
            1. reflection
            2.
            3.
            """;

    // todo: the file name and title should be the same call
    static String formatDateForFileName(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-EEEE");
        return formatter.format(date);
    }

    void createMarkdownFiles() {
        if (startDate.isEmpty() || endDate.isEmpty()) {
            System.out.println("======= Start and end dates are required");
            return;
        }

        if (!isValidDateRange(startDate.get(), endDate.get())) {
            System.out.println("======= Invalid date range");
            return;
        }

        try {
            Path outputDir = resolveOutputDirectory();
            for (LocalDate date = startDate.get(); !date.isAfter(endDate.get()); date = date.plusDays(1)) {

                if (isWeekend(date)) {
                    System.out.println("======= Skipping Weekend for " + formatDateForFileName(date));
                    continue;
                }

                if (isNZHoliday(date)) {
                    System.out.println("======= Skipping Holiday " + HOLIDAYS_2026.get(date));
                    continue;
                }

                var standardizedDateName = formatDateForFileName(date);
                String fileName = standardizedDateName + ".md";
                Path filePath = outputDir.resolve(fileName);
                var fullMarkdownContent = markdownWorkLogDayStructure.replace("{{title_date}}", standardizedDateName);
                Files.writeString(filePath, fullMarkdownContent);
                System.out.printf("======= ✅ Created file %s at path %s %n", fileName, filePath);

                if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                    try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                            java.nio.file.StandardOpenOption.APPEND)) {
                        writer.write(textFridayTemplate);
                        System.out.printf("======= 🔀 Friday Reflection block added to file %s %n", fileName);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error creating markdown files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // todo: this method is an attempt to append content. Simplify and generalise
    public static void addContentToMarkdownFile(String overrideMarkdownFile) {
        // var overrideMarkdownFile = "C:\\ws\\04\\2025-04-30-Wednesday.md";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(overrideMarkdownFile, true))) {
            var xtraMarkdownContent = "extra markdown content to be added";
            var headerWorkLogDayFormatted = String.format("## %s appending %n", formatDateForFileName(LocalDate.now()));
            System.out.println("======= markdown content to add: " + headerWorkLogDayFormatted + xtraMarkdownContent);
            writer.write(headerWorkLogDayFormatted);
            writer.write(xtraMarkdownContent);
            writer.newLine();
            System.out.println("======= markdown override added to file " + overrideMarkdownFile);
        } catch (IOException e) {
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
        return HOLIDAYS_2026.containsKey(date);
    }

    static boolean isWeekend(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case SATURDAY, SUNDAY -> true;
            // Returns FALSE for Mon-Fri
            default -> false;
        };
    }

    // todo: understand this override
    @Override
    public void run() {
        if (startDate.isPresent() && endDate.isPresent()) {
            createMarkdownFiles();
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new WorkLogConfig()).execute(args);
        System.exit(exitCode);
    }

    // todo: add some prints that show the output directory and file names being
    // created for better visibility and debugging
    private static Path resolveOutputDirectory() {
        String base = "/mnt/c/workspace/TESTS";
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        Path dir = Path.of(base, today);

        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create output directory: " + dir, e);
        }
        return dir;
    }
} // end of Class
