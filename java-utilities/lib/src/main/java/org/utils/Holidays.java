package org.utils;

import java.time.LocalDate;
import java.util.Map;
import static java.time.Month.*;
import static java.util.Map.entry;

public class Holidays {
    private static final Map<LocalDate, String> NZ_HOLIDAYS_2026 = Map.ofEntries(
            entry(LocalDate.of(2026, JANUARY, 1), "New Year's Day"),
            entry(LocalDate.of(2026, FEBRUARY, 6), "Waitangi Day"),
            entry(LocalDate.of(2026, APRIL, 3), "Holy Week - Friday"),
            entry(LocalDate.of(2026, APRIL, 6), "Holy Week - Easter Monday"),
            entry(LocalDate.of(2026, APRIL, 25), "ANZAC official Day"),
            entry(LocalDate.of(2026, APRIL, 27), "ANZAC Monday Holiday"),
            entry(LocalDate.of(2026, JUNE, 1), "UK Royalty Birthday"),
            entry(LocalDate.of(2026, JULY, 10), "Matariki"),
            entry(LocalDate.of(2026, OCTOBER, 26), "Labour Day"),
            entry(LocalDate.of(2026, DECEMBER, 25), "Christmas Day"),
            entry(LocalDate.of(2026, DECEMBER, 26), "Christmas Boxing Day"));

    public static boolean isNZHoliday(LocalDate date) {
        return NZ_HOLIDAYS_2026.containsKey(date);
    }

    public static String getNZHolidayName(LocalDate date) {
        return NZ_HOLIDAYS_2026.get(date);
    }
}
