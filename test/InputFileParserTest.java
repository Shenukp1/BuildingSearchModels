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
        Path file = createTempInputFile("""
                Preferences:
                MO, 8:00, CPSC 433 LEC 01, 10
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(() -> parser.parseInputFile());
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
        Path file = createTempInputFile("""
                Partial assignments:
                CPSC 433 LEC 01, MO, 8:00
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(() -> parser.parseInputFile());
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

        // TODO(shenukp1) Nov 25, 2026: need to add in preferences and partial assignments after those functions are finished
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
