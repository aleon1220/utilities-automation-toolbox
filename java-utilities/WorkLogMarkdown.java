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

import static java.util.Map.entry;

class WorkLogMarkdown {

    static LocalDate startDate = LocalDate.of(2026, 1, 19);
    static LocalDate endDate = LocalDate.of(2026, 1, 31);

    static final Map<LocalDate, String> HOLIDAYS_2026 = Map.ofEntries(
            entry(LocalDate.of(2026, 1, 1), "New Year's Day"),
            entry(LocalDate.of(2026, JANUARY, 2), "Day after New Year's Day"),
            entry(LocalDate.of(2026, FEBRUARY, 6), "Waitangi Day"),
            entry(LocalDate.of(2026, APRIL, 3), "Good Friday"),
            entry(LocalDate.of(2026, APRIL, 6), "Easter Monday"),
            entry(LocalDate.of(2026, APRIL, 27), "Anzac Day (Observed)"), // Actual Saturday Apr 25
            entry(LocalDate.of(2026, JUNE, 1), "King's Birthday"),
            entry(LocalDate.of(2026, JULY, 10), "Matariki"),
            entry(LocalDate.of(2026, OCTOBER, 26), "Labour Day"),
            entry(LocalDate.of(2026, DECEMBER, 25), "Christmas Day"),
            entry(LocalDate.of(2026, DECEMBER, 28), "Boxing Day (Observed)") // Actual Sat Dec 26
    );

    static Map<LocalDate, String> nzHolidays2025 = Map.of(
            LocalDate.of(2025, 1, 1), "New Year's Day 2025",
            LocalDate.of(2025, 1, 2), "Day after New Year's Day Jan 2 2025",
            LocalDate.of(2025, 2, 6), "Waitangi Day",
            LocalDate.of(2025, 4, 21), "Easter Monday",
            LocalDate.of(2025, 4, 25), "ANZAC Day",
            LocalDate.of(2025, 6, 2), "King's Birthday",
            LocalDate.of(2025, 6, 20), "Matariki",
            LocalDate.of(2025, 10, 27), "Labour Day",
            LocalDate.of(2025, 12, 25), "Christmas Day",
            LocalDate.of(2025, 12, 26), "Boxing Day");

    // markdown templates
    public static String markdownWorkLogDayStructure = """
            ## GOALS
            1. [WaterDo tasks](https://waterdo.app/en/tasks/)
            2. goal_1
            3. goal_2

            ## QUESTIONS
            1. ?

            ## MORNING

            ### Daily Standup
            1. ✅ What was done yesterday?
            2. 🔄 What is planned for today?
            3. ❗ blockers & escalations

            ### TODO_replace_meeting_name
            1. todo_important_note

            ## AFTERNOON

            ## WRAP UP DAY
            ## Tasks for next business day
            1. todo
            2. todo

            ### Day Reflection & Learning
            1. todo

            ### Timesheet submission
            - NZ_Timesheet_Code

            """;

    static String textFridayTemplate = """
            ## Friday Reflection Time | Learning & Next Week Goals
            1. reflection
            2.
            3.

            """;

    void main() {
        createMarkdownFiles();
        printDataStructures();
        printHolidays();
        printDaysUntilEndofMonth(LocalDate.now());
        // addContent();
    }

    private static String formatDateForFileName(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-EEEE");
        return formatter.format(date);
    }

    private static String createTitleForWorkLog(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-EEEE");
        var titleWorkLogDayFormatted = String.format("# %s \n", formatter.format(date));
        return titleWorkLogDayFormatted;
    }

    static void createMarkdownFiles() {
        if (!isValidDateRange(startDate, endDate)) {
            return;
        }

        try {
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                // Logic fixed: Skip if it IS a weekend OR if it IS a holiday
                if (isWeekend(date)) {
                    System.out.println("Skipping Weekend for " + formatDateForFileName(date));
                    continue;
                }

                if (isNZHoliday(date)) {
                    System.out.println("Skipping Holiday: " + HOLIDAYS_2026.get(date));
                    continue;
                }

                String fileName = formatDateForFileName(date).concat(".md");
                String titleWorkLogDay = createTitleForWorkLog(date);
                Path filePath = Path.of(fileName);
                Files.writeString(filePath, titleWorkLogDay.concat(markdownWorkLogDayStructure));

                if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                        writer.write(textFridayTemplate);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error creating markdown files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void printDataStructures() {
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

        System.out.println("----------------------------------------------");
        System.out.println("Total: " + sortedHolidays.size() + " National Holidays");
    }

    static void printDaysUntilEndofMonth(LocalDate date) {
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());
        long daysUntilEndOfMonth = ChronoUnit.DAYS.between(date, endOfMonth);
        System.out.println("======= Days from today until end of Month from " + formatDateForFileName(date) );
        System.out.println(String.format("%s ", daysUntilEndOfMonth) + "Days till end of the Month ");
        System.out.println("======= Finishing month of " + date.getMonth() + " Total Days " + daysUntilEndOfMonth);

        for (LocalDate current = date; !current.isAfter(endOfMonth); current = current.plusDays(1)) {
            System.out.println(createTitleForWorkLog(current));
            // System.out.printf(" %n%n%-40s | %s%n", formatDateForFileName(current),
            // current.getDayOfWeek());
        }
        System.out.printf("  %n%n%-40s | %s%n", formatDateForFileName(date), date.getDayOfWeek());
        System.out.println("================================\n");
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
            return new DateValidationResult(false, "Date Error Validation: endDate cannot be before startDate");
        }

        long daysBetween = ChronoUnit.DAYS.between(start, end);
        if (daysBetween > 30) {
            return new DateValidationResult(false, "Error: Date range cannot span more than a Month");
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
}