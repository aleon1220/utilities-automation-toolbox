package org.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class WorkLogDisplay {

    void main() {
        System.out.println("Hello from instance JDK25 main!");
        printHolidays();
        printDaysUntilEndofMonth(LocalDate.now());
        printDataStructures();
    }

    static LocalDate dateToday = LocalDate.now();

    static LocalDate endOfMonth = LocalDate.now()
            .withDayOfMonth(LocalDate.now().getMonth().length(LocalDate.now().isLeapYear()));

    static final Map<LocalDate, String> nzHolidays2025 = Map.of(
            LocalDate.of(2025, 1, 1), "New Year's Day",
            LocalDate.of(2025, 1, 2), "Day after New Year's Day",
            LocalDate.of(2025, 2, 6), "Waitangi Day",
            LocalDate.of(2025, 4, 21), "Easter Monday",
            LocalDate.of(2025, 4, 25), "ANZAC Day",
            LocalDate.of(2025, 6, 2), "King's Birthday",
            LocalDate.of(2025, 6, 20), "Matariki",
            LocalDate.of(2025, 10, 27), "Labour Day",
            LocalDate.of(2025, 12, 25), "Christmas Day",
            LocalDate.of(2025, 12, 26), "Boxing Day");

    public static void printHolidays() {
        Map<LocalDate, String> holidayMap = nzHolidays2025;
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
        long daysUntilEndOfMonth = dateToday.lengthOfMonth() - dateToday.getDayOfMonth();
        int weekNumber = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        System.out.println("======= Printing info until End of Month =======");
        System.out.println("======= " + daysUntilEndOfMonth +
                " Days from today until end of Month from " + WorkLogConfig.formatDateForFileName(date));
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
            System.out.println(current);
        }

        System.out.printf("  %n%n%-40s | %s%n", WorkLogConfig.formatDateForFileName(date), date.getDayOfWeek());
        System.out.printf("----------------------------------------------%n");
    }

    public static void printDataStructures() {
        System.out.println("======= Print details using using Java 25!");
        System.out.println("======= Map Class Type " + nzHolidays2025.getClass());
        System.out.println("======= NZ Holidays 2025 - Contents");
        System.out.println(nzHolidays2025);
    }
} // end of Class
