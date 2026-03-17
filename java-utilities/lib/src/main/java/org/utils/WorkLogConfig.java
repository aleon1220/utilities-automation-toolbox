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
import java.util.Set;
import java.util.TreeMap;

import static java.time.Month.JANUARY;
import static java.time.Month.FEBRUARY;
import static java.time.Month.APRIL;
import static java.time.Month.JUNE;
import static java.time.Month.JULY;
import static java.time.Month.OCTOBER;
import static java.time.Month.DECEMBER;
import java.time.temporal.IsoFields;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

import static java.util.Map.entry;
import java.util.Optional;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

// The @CommandLineSchema annotation tells the JVM how to map args to this record
@Command(name = "worklog", description = "Work log configuration tool")
public class WorkLogConfig implements Runnable {

    @Option(names = { "-s", "--start" }, description = "Start date")
    Optional<LocalDate> startDate = Optional.empty();

    @Option(names = { "-e", "--end" }, description = "End date")
    Optional<LocalDate> endDate = Optional.empty();

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Display this help message")
    boolean help;

    // markdown templates
    public static String markdownWorkLogDayStructure = """
            ## GOALS
            1. Main Planning System
            2. [Trello](https://trello.com/c/63qYHZ9V)
            3. Client planning system

            ## QUESTIONS
            1. ?

            ## MORNING

            ### Daily Standup
            1. ✅ What was done yesterday
              - **todo_done**
              - **todo_done**
            2. 🔄 What is planned for today
              - **todo_planned**
              - **todo_planned**
            3. ❗ blockers & escalations
              - **todo_blocker**

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

    static LocalDate endOfMonth = LocalDate.now()
            .withDayOfMonth(LocalDate.now().getMonth().length(LocalDate.now().isLeapYear()));

    static final Map<LocalDate, String> HOLIDAYS_2026 = Map.ofEntries(
            entry(LocalDate.of(2026, JANUARY, 1), "New Year's Day"),
            entry(LocalDate.of(2026, FEBRUARY, 6), "Waitangi Day"),
            entry(LocalDate.of(2026, APRIL, 13), "Easter Monday"),
            entry(LocalDate.of(2026, APRIL, 25), "ANZAC Day"),
            entry(LocalDate.of(2026, JUNE, 1), "Queen's Birthday"),
            entry(LocalDate.of(2026, OCTOBER, 26), "Labour Day"),
            entry(LocalDate.of(2026, DECEMBER, 25), "Christmas Day"),
            entry(LocalDate.of(2026, DECEMBER, 26), "Boxing Day"));

    static final Map<LocalDate, String> nzHolidays2025 = HOLIDAYS_2026;

    static String textFridayTemplate = """

            ## End of week Reflection | Learning & Next Goals
            1. reflection
            2.
            3.
            """;

    private static String formatDateForFileName(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-EEEE");
        return formatter.format(date);
    }

    private static String createTitleForWorkLog(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-EEEE");
        var titleWorkLogDayFormatted = String.format("# %s \n", formatter.format(date));
        return titleWorkLogDayFormatted;
    }

    void createMarkdownFiles() {
        if (startDate.isEmpty() || endDate.isEmpty()) {
            System.out.println("Start and end dates are required");
            return;
        }

        if (!isValidDateRange(startDate.get(), endDate.get())) {
            return;
        }

        Path outputDir = resolveOutputDirectory();

        try {
            for (LocalDate date = startDate.get(); !date.isAfter(endDate.get()); date = date.plusDays(1)) {

                if (isWeekend(date)) {
                    System.out.println("Skipping Weekend for " + formatDateForFileName(date));
                    continue;
                }

                if (isNZHoliday(date)) {
                    System.out.println("Skipping Holiday: " + HOLIDAYS_2026.get(date));
                    continue;
                }

                String fileName = formatDateForFileName(date) + ".md";
                Path filePath = outputDir.resolve(fileName);

                String title = createTitleForWorkLog(date);
                Files.writeString(filePath, title + markdownWorkLogDayStructure);

                if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                    try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                            java.nio.file.StandardOpenOption.APPEND)) {
                        writer.write(textFridayTemplate);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error creating markdown files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void printDataStructures() {
        System.out.println("======= Print details using using Java 25!");
        System.out.println("======= Map Class Type " + nzHolidays2025.getClass());
        System.out.println("======= NZ Holidays 2025 - Contents");
        System.out.println(nzHolidays2025);
    }

    public static void addContentToMarkdownFile() {
        var overrideMarkdownFile = "C:\\ws\\04\\2025-04-30-Wednesday.md";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(overrideMarkdownFile, true))) {
            var xtraMarkdownContent = "extra markdown content to be added";
            writer.write(xtraMarkdownContent);
            writer.newLine();
            System.out.println("markdown template added to file " + overrideMarkdownFile);
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

    public static void printHolidays() {
        Map<LocalDate, String> holidayMap = HOLIDAYS_2026;
        // Use TreeMap to sort by date automatically
        var sortedHolidays = new TreeMap<>(holidayMap);
        var formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy");

        System.out.println("==============================================");
        System.out.println("     NEW ZEALAND PUBLIC HOLIDAYS 2026         ");
        System.out.println("==============================================");
        System.out.printf("%-20s | %-20s%n", "DATE", "HOLIDAY NAME");
        System.out.println("----------------------------------------------");

        sortedHolidays.forEach((date, name) -> {
            System.out.printf("%-20s | %-20s%n", date.format(formatter), name);
        });

        System.out.println("Total: " + sortedHolidays.size() + " National Holidays");
        System.out.printf("----------------------------------------------%n");
    }

    static void printDaysUntilEndofMonth(LocalDate date) {

        long daysUntilEndOfMonth = ChronoUnit.DAYS.between(date, endOfMonth);
        int weekNumber = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        System.out.println("======= Printing info until End of Month =======");
        System.out.println("======= " + daysUntilEndOfMonth +
                " Days from today until end of Month from " + formatDateForFileName(date));
        System.out.println("======= Current Week Number " + weekNumber);
        System.out.println("======= " + String.format("%s ", daysUntilEndOfMonth) + " Days until end of Month");
        System.out.println("======= Finishing month of " + date.getMonth() + " Total remaining Days "
                + daysUntilEndOfMonth);

        System.out.println("======= Listing remaining days until end of month");
        int lastWeek = -1;
        // WeekFields weekFields = WeekFields.ISO; // ISO standard for week numbering
        WeekFields weekFields = WeekFields.of(Locale.of("en", "NZ"));
        // WeekFields weekFields = WeekFields.of(Locale.of("en", "UK"));

        for (LocalDate current = date; !current.isAfter(endOfMonth); current = current.plusDays(1)) {
            int currentWeek = current.get(weekFields.weekOfWeekBasedYear());

            if (currentWeek != lastWeek) {
                System.out.printf("%n======= Header for a new week =======");
                System.out.printf("%n Week Number %d%n", currentWeek);
                lastWeek = currentWeek;
            }
            System.out.println(createTitleForWorkLog(current));
        }

        System.out.printf("  %n%n%-40s | %s%n", formatDateForFileName(date), date.getDayOfWeek());
        System.out.printf("----------------------------------------------%n");
    }

    static boolean isValidDateRange(LocalDate start, LocalDate end) {
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

    @Override
    public void run() {
        printHolidays();
        printDaysUntilEndofMonth(LocalDate.now());
        if (startDate.isPresent() && endDate.isPresent()) {
            createMarkdownFiles();
        }
        printDataStructures();
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new WorkLogConfig()).execute(args);
        System.exit(exitCode);
    }

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
}
