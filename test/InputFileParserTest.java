import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import models.Preference;
import models.Slot;

class InputFileParserTest {

    @TempDir
    Path tempDir;

    private Path createTempInputFile(String content) throws IOException {
        Path file = tempDir.resolve("input.txt");
        Files.writeString(file, content);
        return file;
    }
    /**
     * For all the test below we are simply checking if the hashmaps have 
     * the values parsed into them.
     */
    @Test
    void parsesNameSection() throws IOException {
        Path file = createTempInputFile("""
                Name:
                Example-name
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(() -> parser.parseInputFile());
        assertEquals("Example-name", parser.getInputFileParsedData().name);
    }

    @Test
    void parsesLectureSlotsSection() throws IOException {
        Path file = createTempInputFile("""
                Lecture slots:
                MO, 8:00, 3, 2, 1
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(() -> parser.parseInputFile());

        List<Slot> lecSlots = parser.getInputFileParsedData().slots.get(InputFileParsedData.LEC);
        assertEquals(1, lecSlots.size());
        assertEquals("MO, 8:00", lecSlots.get(0).timeSlot);
        assertEquals(3, lecSlots.get(0).max);
        assertEquals(2, lecSlots.get(0).min);
        assertEquals(1, lecSlots.get(0).alMax);
    }

    @Test
    void parsesTutorialSlotsSection() throws IOException {
        Path file = createTempInputFile("""
                Tutorial slots:
                MO, 8:00, 2, 1, 0
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(() -> parser.parseInputFile());

        List<Slot> tutSlots = parser.getInputFileParsedData().slots.get(InputFileParsedData.TUT);
        assertEquals(1, tutSlots.size());
        assertEquals("MO, 8:00", tutSlots.get(0).timeSlot);
        assertEquals(2, tutSlots.get(0).max);
        assertEquals(1, tutSlots.get(0).min);
        assertEquals(0, tutSlots.get(0).alMax);
    }

    @Test
    void addTimeSlotThrowsForInvalidType() throws IOException {
        Path file = createTempInputFile("");
        InputFileParser parser = new InputFileParser(file);
        String[] parts = {"MO", "8:00", "3", "2", "1"};
        assertThrows(IllegalArgumentException.class, () -> parser.addTimeSlot(parts, "INVALID"));
    }

    @Test
    void parsesLecturesSection() throws IOException {
        Path file = createTempInputFile("""
                Lectures:
                CPSC 433 LEC 01, true
                CPSC 231 LEC 01, false
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(() -> parser.parseInputFile());

        assertEquals(2, parser.getInputFileParsedData().lectures.size());
        assertTrue(parser.getInputFileParsedData().lectures.get("CPSC 433 LEC 01"));
        assertFalse(parser.getInputFileParsedData().lectures.get("CPSC 231 LEC 01"));
    }

    @Test
    void parsesTutorialsSection() throws IOException {
        Path file = createTempInputFile("""
                Tutorials:
                CPSC 433 LEC 01 TUT 01, true
                CPSC 231 LEC 01 TUT 01, false
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(() -> parser.parseInputFile());

        assertEquals(2, parser.getInputFileParsedData().tutorials.size());
        assertTrue(parser.getInputFileParsedData().tutorials.get("CPSC 433 LEC 01 TUT 01"));
        assertFalse(parser.getInputFileParsedData().tutorials.get("CPSC 231 LEC 01 TUT 01"));
    }


    @Test
    void parsesNotCompatibleSection() throws IOException {
        Path file = createTempInputFile("""
                Not compatible:
                CPSC 433 LEC 01, CPSC 433 LEC 01 TUT 01
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(() -> parser.parseInputFile());

        // should be symmetric, so both directions should be in the map
        HashMap<String, Set<String>> notCompatible = parser.getInputFileParsedData().notCompatible;
        assertTrue(notCompatible.get("CPSC 433 LEC 01").contains("CPSC 433 LEC 01 TUT 01"));
        assertTrue(notCompatible.get("CPSC 433 LEC 01 TUT 01").contains("CPSC 433 LEC 01"));
    }

    @Test
    void parsesUnwantedSection() throws IOException {
        Path file = createTempInputFile("""
                Unwanted:
                CPSC 433 LEC 01, MO, 8:00
                CPSC 433 LEC 01, TU, 9:30
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(() -> parser.parseInputFile());

        Set<String> unwantedSlots = parser.getInputFileParsedData().unwanted.get("CPSC 433 LEC 01");
        assertEquals(2, unwantedSlots.size());
        assertTrue(unwantedSlots.contains("MO, 8:00"));
        assertTrue(unwantedSlots.contains("TU, 9:30"));
    }

    @Test
    void parsesPreferencesSection() throws IOException {
        // preferences checks that the course and its time slot exist first
        Path file = createTempInputFile("""
                Lecture slots:
                MO, 8:00, 3, 2, 1
                Tutorial slots:
                TU, 9:30, 2, 1, 0
                Lectures:
                CPSC 433 LEC 01, true
                Tutorials:
                CPSC 433 LEC 01 TUT 01, false
                Preferences:
                MO, 8:00, CPSC 433 LEC 01, 10
                TU, 9:30, CPSC 433 LEC 01 TUT 01, 5
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(() -> parser.parseInputFile());

        List<Preference> prefs = parser.getInputFileParsedData().preferences;
        assertEquals(2, prefs.size());

        assertEquals("CPSC 433 LEC 01", prefs.get(0).courseId);
        assertEquals("MO, 8:00", prefs.get(0).slotTime);
        assertEquals(10, prefs.get(0).preference);

        assertEquals("CPSC 433 LEC 01 TUT 01", prefs.get(1).courseId);
        assertEquals("TU, 9:30", prefs.get(1).slotTime);
        assertEquals(5, prefs.get(1).preference);
    }

    @Test
    void ignoresInvalidPreferences() throws IOException {
        // all of the preferences should be ignored because of the following:
        //  1. lecture does not have the right lecture slot(i.e. has 9am instead of 8am)
        //  2. tutorial does not have the correct tut slot(i.e., 12:30pm instead of 9:30am)
        //  3. simply cpsc 999 is an unknown course because it was not in the lecture heading

        Path file = createTempInputFile("""
                Lecture slots:
                MO, 8:00, 3, 2, 1
                Tutorial slots:
                TU, 9:30, 2, 1, 0
                Lectures:
                CPSC 433 LEC 01, true
                Tutorials:
                CPSC 433 LEC 01 TUT 01, false
                Preferences:
                MO, 9:00, CPSC 433 LEC 01, 10
                TU, 12:30, CPSC 433 LEC 01 TUT 01, 5
                MO, 8:00, CPSC 999 LEC 01, 7
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(() -> parser.parseInputFile());
        assertTrue(parser.getInputFileParsedData().preferences.isEmpty());
    }

    @Test
    void parsesPairSection() throws IOException {
        Path file = createTempInputFile("""
                Pair:
                CPSC 433 LEC 01, CPSC 433 LEC 02
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(() -> parser.parseInputFile());

        Set<String> pairedCourses = parser.getInputFileParsedData().pairs.get("CPSC 433 LEC 01");
        assertEquals(1, pairedCourses.size());
        assertTrue(pairedCourses.contains("CPSC 433 LEC 02"));
    }

    @Test
    void parsesPartialAssignmentsSection() throws IOException {
        // partial assignments checks that the course and its time slot exist first
        Path file = createTempInputFile("""
                Lecture slots:
                MO, 8:00, 3, 2, 1
                Tutorial slots:
                TU, 9:30, 2, 1, 0
                Lectures:
                CPSC 433 LEC 01, true
                Tutorials:
                CPSC 433 LEC 01 TUT 01, false
                Partial assignments:
                CPSC 433 LEC 01, MO, 8:00
                CPSC 433 LEC 01 TUT 01, TU, 9:30
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(() -> parser.parseInputFile());

        HashMap<String, Set<String>> partialAssignments = parser.getInputFileParsedData().partialAssignments;
        assertTrue(partialAssignments.get("CPSC 433 LEC 01").contains("MO, 8:00"));
        assertTrue(partialAssignments.get("CPSC 433 LEC 01 TUT 01").contains("TU, 9:30"));
    }

    @Test
    void addPartialAssignmentThrowsWhenLectureSlotMissing() throws IOException {
        // lecture slot is 9am but partial is 8am
        Path file = createTempInputFile("""
                Lecture slots:
                MO, 9:00, 3, 2, 1
                Tutorial slots:
                TU, 9:30, 2, 1, 0
                Lectures:
                CPSC 433 LEC 01, true
                Tutorials:
                CPSC 433 LEC 01 TUT 01, false
                Partial assignments:
                CPSC 433 LEC 01, MO, 8:00
                CPSC 433 LEC 01 TUT 01, TU, 9:30
                """);
        InputFileParser parser = new InputFileParser(file);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () ->  parser.parseInputFile()
        );
        assertTrue(ex.getMessage().contains("Lecture"));// since the message contains Lecture={}
    }

    @Test
    void addPartialAssignmentThrowsWhenTutorialSlotMissing() throws IOException {
        // partial assignment is at 9:30 but tut slot available is at 12:30
        Path file = createTempInputFile("""
                Tutorial slots:
                TU, 12:30, 2, 1, 0
                Tutorials:
                CPSC 433 LEC 01 TUT 01, false
                Partial assignments:
                CPSC 433 LEC 01 TUT 01, TU, 9:30
                """);
        InputFileParser parser = new InputFileParser(file);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () ->  parser.parseInputFile()
        );
        assertTrue(ex.getMessage().contains("Tutorial"));
    }

    @Test
    void addPartialAssignmentThrowsWhenCourseUnknown() throws IOException {
        // since CPSC 999 is not under lec, this will fail. assuming 
        // I am considering the WARNING comment to be true.
        Path file = createTempInputFile("""
                Lecture slots:
                MO, 8:00, 3, 2, 1
                Tutorial slots:
                TU, 9:30, 2, 1, 0
                Lectures:
                CPSC 433 LEC 01, true
                Tutorials:
                CPSC 433 LEC 01 TUT 01, false
                Partial assignments:
                CPSC 999 LEC 01, MO, 8:00
                CPSC 999 LEC 01 TUT 01, TU, 9:30
                """);
        InputFileParser parser = new InputFileParser(file);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () ->  parser.parseInputFile()
        );
        assertTrue(ex.getMessage().contains("Unknown course"));
    }

    @Test
    void addToSetMapCreatesAndAddsToSet() throws IOException {
        Path file = createTempInputFile("");
        InputFileParser parser = new InputFileParser(file);
        HashMap<String, Set<String>> map = new HashMap<>();

        parser.addToSetMap(map, "CPSC 433 LEC 01", "MO, 8:00");
        parser.addToSetMap(map, "CPSC 433 LEC 01", "TU, 9:30");

        assertEquals(2, map.get("CPSC 433 LEC 01").size());
        assertTrue(map.get("CPSC 433 LEC 01").contains("MO, 8:00"));
        assertTrue(map.get("CPSC 433 LEC 01").contains("TU, 9:30"));
    }

    @Test
    void parsesAllSections() throws IOException {
        Path file = createTempInputFile("""
                Name:
                Example-name
                Lecture slots:
                MO, 8:00, 3, 2, 1
                Tutorial slots:
                MO, 8:00, 2, 1, 0

                Lectures:
                CPSC 433 LEC 01, true

                Tutorials:
                CPSC 433 LEC 01 TUT 01, false

                Not compatible:
                CPSC 433 LEC 01, CPSC 433 LEC 01 TUT 01

                Unwanted:
                CPSC 433 LEC 01, MO, 8:00

                Preferences:
                MO, 8:00, CPSC 433 LEC 01, 10

                Pair:
                CPSC 433 LEC 01, CPSC 433 LEC 02

                Partial assignments:
                CPSC 433 LEC 01, MO, 8:00
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(() -> parser.parseInputFile());

        InputFileParsedData data = parser.getInputFileParsedData();
        assertEquals("Example-name", data.name);
        assertEquals(1, data.slots.get(InputFileParsedData.LEC).size());
        assertEquals(1, data.slots.get(InputFileParsedData.TUT).size());
        assertTrue(data.lectures.get("CPSC 433 LEC 01"));
        assertFalse(data.tutorials.get("CPSC 433 LEC 01 TUT 01"));
        assertTrue(data.notCompatible.get("CPSC 433 LEC 01").contains("CPSC 433 LEC 01 TUT 01"));
        assertTrue(data.notCompatible.get("CPSC 433 LEC 01 TUT 01").contains("CPSC 433 LEC 01"));
        assertTrue(data.unwanted.get("CPSC 433 LEC 01").contains("MO, 8:00"));
        assertTrue(data.pairs.get("CPSC 433 LEC 01").contains("CPSC 433 LEC 02"));
        assertTrue(data.partialAssignments.get("CPSC 433 LEC 01").contains("MO, 8:00"));
        assertEquals(1, data.preferences.size());
        assertEquals("CPSC 433 LEC 01", data.preferences.get(0).courseId);
        assertEquals("MO, 8:00", data.preferences.get(0).slotTime);
        assertEquals(10, data.preferences.get(0).preference);
    }

    @Test
    void returnsEmptyDataWhenNoDataAfterHeader() throws IOException {
        Path file = createTempInputFile("""
                Name:
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(() -> parser.parseInputFile());

        InputFileParsedData data = parser.getInputFileParsedData();
        assertEquals("", data.name);
        assertTrue(data.lectures.isEmpty());
        assertTrue(data.tutorials.isEmpty());
    }
}
