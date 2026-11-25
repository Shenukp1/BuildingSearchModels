import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class InputFileParserTest {

    @TempDir
    Path tempDir;

    private Path createTempInputFile(String content) throws IOException {
        Path file = tempDir.resolve("input.txt");
        Files.writeString(file, content);
        return file;
    }

    @Test
    void parsesNameSection() throws IOException {
        Path file = createTempInputFile("""
                Name:
                Example-name
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(parser::parseInputFile); // because the default scenario throws in the switch statement
        assertTrue(parser.getParsedData().length >= 1); // at least one item is contained if it was parsed correctly
    }

    @Test
    void parsesLectureSlotsSection() throws IOException {
        Path file = createTempInputFile("""
                Lecture slots:
                MO, 8:00, 3, 2
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(parser::parseInputFile);
        assertTrue(parser.getParsedData().length >= 1);
    }

    @Test
    void parsesTutorialSlotsSection() throws IOException {
        Path file = createTempInputFile("""
                Tutorial slots:
                MO, 8:00, 2, 1
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(parser::parseInputFile);
        assertTrue(parser.getParsedData().length >= 1);
    }

    @Test
    void parsesLecturesSection() throws IOException {
        Path file = createTempInputFile("""
                Lectures:
                CPSC 433 LEC 01
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(parser::parseInputFile);
        assertTrue(parser.getParsedData().length >= 1);
    }

    @Test
    void parsesTutorialsSection() throws IOException {
        Path file = createTempInputFile("""
                Tutorials:
                CPSC 433 LEC 01 TUT 01
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(parser::parseInputFile);
        assertTrue(parser.getParsedData().length >= 1);
    }

    @Test
    void parsesNotCompatibleSection() throws IOException {
        Path file = createTempInputFile("""
                Not compatible:
                CPSC 433 LEC 01, CPSC 433 LEC 01 TUT 01
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(parser::parseInputFile);
        assertTrue(parser.getParsedData().length >= 1);
    }

    @Test
    void parsesUnwantedSection() throws IOException {
        Path file = createTempInputFile("""
                Unwanted:
                CPSC 433 LEC 01, MO, 8:00
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(parser::parseInputFile);
        assertTrue(parser.getParsedData().length >= 1);
    }

    @Test
    void parsesPreferencesSection() throws IOException {
        Path file = createTempInputFile("""
                Preferences:
                MO, 8:00, CPSC 433 LEC 01, 10
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(parser::parseInputFile);
        assertTrue(parser.getParsedData().length >= 1);
    }

    @Test
    void parsesPairSection() throws IOException {
        Path file = createTempInputFile("""
                Pair:
                CPSC 433 LEC 01, CPSC 433 LEC 02
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(parser::parseInputFile);
        assertTrue(parser.getParsedData().length >= 1);
    }

    @Test
    void parsesPartialAssignmentsSection() throws IOException {
        Path file = createTempInputFile("""
                Partial assignments:
                CPSC 433 LEC 01, MO, 8:00
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(parser::parseInputFile);
        assertTrue(parser.getParsedData().length >= 1);
    }

    @Test
    void parsesAllSections() throws IOException {
        Path file = createTempInputFile("""
                Name:
                Example-name
                Lecture slots:
                MO, 8:00, 3, 2
                Tutorial slots:
                MO, 8:00, 2, 1

                Lectures:
                CPSC 433 LEC 01

                Tutorials:
                CPSC 433 LEC 01 TUT 01

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
        assertDoesNotThrow(parser::parseInputFile);
        assertTrue(parser.getParsedData().length >= 1);
    }

    @Test
    void returnsEmptyArrayWhenNoDataAfterHeader() throws IOException {
        Path file = createTempInputFile("""
                Name:
                """);
        InputFileParser parser = new InputFileParser(file);
        assertDoesNotThrow(parser::parseInputFile);
        assertEquals(0, parser.getParsedData().length);
    }
}
