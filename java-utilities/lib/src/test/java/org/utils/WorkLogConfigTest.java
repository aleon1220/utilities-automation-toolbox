package org.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

class WorkLogConfigTest {

    @Test
    void methodRunLibraryReturnsTrue() {
        WorkLogConfig classUnderTest = new WorkLogConfig();
        classUnderTest.baseOutputDir = "mocked";
        assertDoesNotThrow(() -> classUnderTest.run(), "run() should execute without throwing an exception");
    }

    @Test
    void accessibleMethodExists() {
        assertDoesNotThrow(() -> {
            WorkLogConfig.class.getDeclaredMethod("isValidDateRange", LocalDate.class, LocalDate.class);
        });
    }

    @Test
    void testDryRunExecutionWithThisWeek() {
        WorkLogConfig classUnderTest = new WorkLogConfig();
        classUnderTest.dryrun = true;
        classUnderTest.thisWeek = true;
        classUnderTest.baseOutputDir = "mocked";
        assertDoesNotThrow(() -> classUnderTest.run());
    }

    @Test
    void testDryRunExecutionWithExplicitDates() {
        WorkLogConfig classUnderTest = new WorkLogConfig();
        classUnderTest.dryrun = true;
        classUnderTest.startDate = Optional.of(LocalDate.of(2026, 3, 1));
        classUnderTest.endDate = Optional.of(LocalDate.of(2026, 3, 31));
        classUnderTest.baseOutputDir = "mocked";
        assertDoesNotThrow(() -> classUnderTest.run());
    }

    @Test
    void testIsWeekend() {
        LocalDate monday = LocalDate.of(2026, 3, 2);
        LocalDate friday = LocalDate.of(2026, 3, 6);
        LocalDate saturday = LocalDate.of(2026, 3, 7);
        LocalDate sunday = LocalDate.of(2026, 3, 8);

        assertThat(WorkLogConfig.isWeekend(monday)).isFalse();
        assertThat(WorkLogConfig.isWeekend(friday)).isFalse();
        assertThat(WorkLogConfig.isWeekend(saturday)).isTrue();
        assertThat(WorkLogConfig.isWeekend(sunday)).isTrue();
    }

    @Test
    void testIsNZHoliday() {
        LocalDate waitangiDay = LocalDate.of(2026, 2, 6);
        LocalDate anzacMonday = LocalDate.of(2026, 4, 27);
        LocalDate matariki = LocalDate.of(2026, 7, 10);
        LocalDate boxingDay = LocalDate.of(2026, 12, 26);
        LocalDate normalDay = LocalDate.of(2026, 3, 15);

        assertThat(WorkLogConfig.isNZHoliday(waitangiDay)).isTrue();
        assertThat(WorkLogConfig.isNZHoliday(anzacMonday)).isTrue();
        assertThat(WorkLogConfig.isNZHoliday(matariki)).isTrue();
        assertThat(WorkLogConfig.isNZHoliday(boxingDay)).isTrue();
        assertThat(WorkLogConfig.isNZHoliday(normalDay)).isFalse();
    }

    @Test
    void testValidDateRange() {
        LocalDate start = LocalDate.of(2026, 3, 1);
        LocalDate end = LocalDate.of(2026, 3, 15);
        LocalDate tooLongEnd = LocalDate.of(2026, 4, 15);
        LocalDate beforeStart = LocalDate.of(2026, 2, 15);

        assertThat(WorkLogConfig.isValidDateRange(start, end)).isTrue();
        assertThat(WorkLogConfig.isValidDateRange(start, tooLongEnd)).isFalse();
        assertThat(WorkLogConfig.isValidDateRange(start, beforeStart)).isFalse();
    }

    @Test
    void testDateValidationResult() {
        WorkLogConfig.DateValidationResult result = WorkLogConfig.performDateValidation(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15));
        assertThat(result.valid()).isTrue();
        assertThat(result.hasError()).isFalse();

        WorkLogConfig.DateValidationResult resultError = WorkLogConfig.performDateValidation(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 15));
        assertThat(resultError.valid()).isFalse();
        assertThat(resultError.hasError()).isTrue();
    }

    @Test
    void testEndOfMonthBoundary() {
        LocalDate start = LocalDate.of(2026, 2, 28);
        LocalDate leapYearEnd = LocalDate.of(2024, 2, 29); // 2024 is leap

        assertThat(WorkLogConfig.isValidDateRange(LocalDate.of(2024, 2, 1), leapYearEnd)).isTrue();
        assertThat(WorkLogConfig.isValidDateRange(LocalDate.of(2026, 2, 1), start)).isTrue();
    }

    @Test
    void testCreateMarkdownFiles(@TempDir Path tempDir) throws IOException {
        WorkLogConfig config = new WorkLogConfig();
        config.baseOutputDir = tempDir.toString();
        config.startDate = Optional.of(LocalDate.of(2026, 3, 5)); // Thursday
        config.endDate = Optional.of(LocalDate.of(2026, 3, 6)); // Friday

        config.createMarkdownFiles();

        String todayStr = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        Path outputDir = tempDir.resolve(todayStr);

        assertThat(Files.exists(outputDir)).isTrue();

        String thursName = "2026-03-05-Thursday.md";
        String friName = "2026-03-06-Friday.md";

        Path thursFile = outputDir.resolve(thursName);
        Path friFile = outputDir.resolve(friName);

        assertThat(Files.exists(thursFile)).isTrue();
        assertThat(Files.exists(friFile)).isTrue();

        String thursContent = Files.readString(thursFile);
        String friContent = Files.readString(friFile);

        assertThat(thursContent).contains("2026-03-05-Thursday");
        assertThat(thursContent).doesNotContain("week_reflection");

        assertThat(friContent).contains("2026-03-06-Friday");
        assertThat(friContent).contains("week_reflection"); // Friday template appended
    }

    @Test
    void testCreateMarkdownFilesInvalidRange() {
        WorkLogConfig config = new WorkLogConfig();
        config.startDate = Optional.empty(); // missing
        config.createMarkdownFiles(); // Should just return early
    }

    @Test
    void testCreateMarkdownFilesInvalidDates() {
        WorkLogConfig config = new WorkLogConfig();
        config.startDate = Optional.of(LocalDate.of(2026, 4, 1)); 
        config.endDate = Optional.of(LocalDate.of(2026, 3, 1)); 
        config.createMarkdownFiles(); // Should just print error and return
    }

    @Test
    void testAddContentToMarkdownFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test.md");
        Files.writeString(file, "Initial content\n");

        WorkLogConfig.addContentToMarkdownFile(file.toString(), "extra data");

        String content = Files.readString(file);
        assertThat(content).contains("Initial content");
        assertThat(content).contains("extra data");
        assertThat(content).contains("appending");
    }
}
