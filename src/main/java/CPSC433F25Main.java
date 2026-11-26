import java.io.IOException;

/**
 * How to run:
 * java CPSC433F25Main path Wminfilled Wpref Wpair Wsecdiff pen_lecturemin pen_tutorialmin pen_notpaired pen_section
 *
 * Usage:
 *  ./run.sh run CPSC433F25Main input.txt 1 1 1 1 1 1 1 1
 */
public class CPSC433F25Main {
    public static void main(String[] args) throws IOException {
        CLIParser cliParser = new CLIParser(args);
        InputFileParser inputFileParser = new InputFileParser(cliParser.getFile());
        inputFileParser.parseInputFile();
        inputFileParser.getInputFileParsedData().printAll();
    }
}
