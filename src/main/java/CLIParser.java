import java.nio.file.Path;

public class CLIParser {

    private static final int EXPECTED_ARGS = 9;

    private final Path file;
    private final int wMinFilled;
    private final int wPref;
    private final int wPair;
    private final int wSecDiff;
    private final int penLectureMin;
    private final int penTutorialMin;
    private final int penNotPaired;
    private final int penSection;

    public CLIParser(String[] args) {
        // the spec says we need at most 9 arguments, which include the first
        // arg as the input text file and the rest being 8 other input integer parameters
        if (args == null || args.length != EXPECTED_ARGS) {
            throw new IllegalArgumentException(
                "Usage: java SetBasedSearch <filename> " +
                "<Wminfilled> <Wpref> <Wpair> <Wsecdiff> " +
                "<pen_lecturemin> <pen_tutorialmin> <pen_notpaired> <pen_section>"
            );
        }

        if (args[0] == null || args[0].isBlank() || args[0].isEmpty()) {
            throw new IllegalArgumentException("Filename/Path is either empty, does not exist, or just spaces");
        }

        this.file           = Path.of(args[0]);
        this.wMinFilled     = parseNonNegativeInt(args[1], "Wminfilled");
        this.wPref          = parseNonNegativeInt(args[2], "Wpref");
        this.wPair          = parseNonNegativeInt(args[3], "Wpair");
        this.wSecDiff       = parseNonNegativeInt(args[4], "Wsecdiff");
        this.penLectureMin  = parseNonNegativeInt(args[5], "pen_lecturemin");
        this.penTutorialMin = parseNonNegativeInt(args[6], "pen_tutorialmin");
        this.penNotPaired   = parseNonNegativeInt(args[7], "pen_notpaired");
        this.penSection     = parseNonNegativeInt(args[8], "pen_section");
    }

    /**
     * Function that checks if the value is an integer or not, if not, we convert it to that.
     * 
     * The function toss an error in the following cases:
     *  1. If it cannot be converted we toss an error.
     *  2. If the value is negative we toss an error 
     */
    private static int parseNonNegativeInt(String raw, String name) {
        int value;
        try {
            value = Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be an integer, got: " + raw, e);
        }

        if (value < 0) {
            throw new IllegalArgumentException(name + " must be non-negative, got: " + value);
        }

        return value;
    }

    /**
     * Contains functions to get the value held within this object.
     */

    public Path getFile() {
        return file;
    }

    public int getWMinFilled() {
        return wMinFilled;
    }

    public int getWPref() {
        return wPref;
    }

    public int getWPair() {
        return wPair;
    }

    public int getWSecDiff() {
        return wSecDiff;
    }

    public int getPenLectureMin() {
        return penLectureMin;
    }

    public int getPenTutorialMin() {
        return penTutorialMin;
    }

    public int getPenNotPaired() {
        return penNotPaired;
    }

    public int getPenSection() {
        return penSection;
    }
}
