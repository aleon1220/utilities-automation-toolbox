import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class WorkLogMarkdown {
    // data structure with NZ holidays 2025
    static LocalDate startDate = LocalDate.of(2025, 9, 16);
    static LocalDate endDate = LocalDate.of(2025, 9, 25);
    // Removed nzHolidays set to avoid redundancy; use nzHolidays2025.keySet()

    static Map<LocalDate, String> nzHolidays2025 = Map.of(
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

    // markdown templates
    public static String markdownWorkLogDayStructure = """
    
    ## GOALS
    1. 
    2.

    ## QUESTIONS
    1. ?
    2. ?

    ## MORNING
    ### TODO_replace_meeting_name
    1. todo_important_note

    ## AFTERNOON
            
    ### SRE Chapter standup
    - todo_important_note
            
    ## WRAP UP DAY 
    ### Day Reflection & Learning
    1. 
    
    ## Tasks for next business day
    1. 
    
    ### Timesheet submission
    - NZ_Timesheet_code
    """;

    static String textFridayTemplate = """
    ## Friday End of Week Reflection, Learning & Next Week Goals
    1.
    2.
    3.
    """;

    static void createMarkdownFiles() {
        try {
            // Loop through each day range
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                // Date formatter for file names
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-EEEE");
                String fileName = formatter.format(date) + ".md";
                String markdownDateWithDayOfWeek = formatter.format(date);
                String titleWorkLogDay = "# " + markdownDateWithDayOfWeek;

                if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    System.out.println("skip NZ Weekends " + date.getDayOfWeek() );
                    continue;
                }

                if (nzHolidays2025.containsKey(date)) {
                    System.out.println("skip NZ holidays " + nzHolidays2025.keySet().getClass());
                    continue;
                }

                Path filePath = Path.of(fileName);
                Files.writeString(filePath, titleWorkLogDay.concat(markdownWorkLogDayStructure));
                
                // if friday append textFriday to file
                if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                        writer.write(textFridayTemplate);
                        System.out.println("Friday reflection block added " + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("Created markdown file: " + filePath);

            } // end of Loop through each day range
        } catch (IOException e) {
            System.err.println("Error creating file: " + e.getStackTrace());
            e.printStackTrace();
        }
    }

    static void printDataStructures() {
        // Print the data structures for debugging
        System.out.println(nzHolidays2025.getClass() + " NZ Holidays 2025 ");
        System.out.println(nzHolidays2025);
    }

    public static void addContent() {
        var markdownFilePath = "C:\\Users\\LeonRA\\ws\\04\\2025-04-30-Wednesday.md";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(markdownFilePath, true))) {
            var xtraMarkdownContent = "extra markdown content to be added";
            writer.write(xtraMarkdownContent);
            writer.newLine();
            System.out.println("markdown template added to file " + markdownFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // addContent();
        printDataStructures();
        createMarkdownFiles();
    }
}
