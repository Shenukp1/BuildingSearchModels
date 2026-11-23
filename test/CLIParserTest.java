import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CLIParserTest {

    @Test
    void parsesValidArgs() {
        String[] args = {"input.txt", "0", "1", "2", "3", "4", "5", "6", "7"};
        CLIParser parser = new CLIParser(args);
        assertEquals(Path.of("input.txt"), parser.getFile());
        assertEquals(0, parser.getWMinFilled());
        assertEquals(1, parser.getWPref());
        assertEquals(2, parser.getWPair());
        assertEquals(3, parser.getWSecDiff());
        assertEquals(4, parser.getPenLectureMin());
        assertEquals(5, parser.getPenTutorialMin());
        assertEquals(6, parser.getPenNotPaired());
        assertEquals(7, parser.getPenSection());
    }

    @Test
    void rejectsTooFewArgs() {
        // see <https://docs.junit.org/6.1.3/writing-tests/exception-handling.html> how to test exceptions
        String[] args = {"input.txt", "1", "2", "3"};
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> { new CLIParser(args); });
        assertTrue(e.getMessage().contains("Usage: java SetBasedSearch"));
    }

    @Test
    void rejectsTooManyArgs() {
        String[] args = {"input.txt", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new CLIParser(args));
        assertTrue(e.getMessage().contains("Usage: java SetBasedSearch"));
    }

    @Test
    void rejectsNegativeInt() {
        String[] args = {"input.txt", "1", "2", "-3", "4", "5", "6", "7", "8"};

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new CLIParser(args));
        assertTrue(e.getMessage().contains("Wpair"));
        assertTrue(e.getMessage().contains("non-negative"));
    }

    @Test
    void rejectsNonInteger() {
        String[] args = {"input.txt", "1", "abc", "3", "4", "5", "6", "7", "8"};

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new CLIParser(args));
        assertTrue(e.getMessage().contains("Wpref"));
        assertTrue(e.getMessage().contains("must be an integer"));
    }

    @Test
    void rejectsBlankFilename() {
        String[] args = {"", "1", "2", "3", "4", "5", "6", "7", "8"};
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new CLIParser(args));
        assertTrue(e.getMessage().contains("Filename/Path is either"));
    }
}
