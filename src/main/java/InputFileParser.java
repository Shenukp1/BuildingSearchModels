import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Parses the input file given to the program to start the set based search on.
 */
public class InputFileParser {
    public static final String NAME = "Name:";
    public static final String LECTURE_SLOTS = "Lecture slots:";
    public static final String TUTORIAL_SLOTS = "Tutorial slots:";
    public static final String LECTURES = "Lectures:";
    public static final String TUTORIALS = "Tutorials:";
    public static final String NOT_COMPATIBLE = "Not compatible:";
    public static final String UNWANTED = "Unwanted:";
    public static final String PREFERENCES = "Preferences:";
    public static final String PAIR = "Pair:";
    public static final String PARTIAL_ASSIGNMENTS = "Partial assignments:";

    private Path filePath;
    private String[] parsedData = new String[0];

    
    public InputFileParser(Path filepath){
        this.filePath = filepath;
    }
    /**
     * Function that looks for the header, when it see it,
     * it will record it and then assume everything before the next header
     * file is part of that header.
     * 
     */
    public void parseInputFile() throws IOException{
        String header = "";
        try (BufferedReader reader = Files.newBufferedReader(this.filePath)) {
            
            String rawInputFileLine;
            // simply read the file until we reach the end, which is null
            while ((rawInputFileLine = reader.readLine()) != null) {
                String line = rawInputFileLine.strip(); // remove whitespaces
                
                // if the string is empty, then we skip
                if(line.isEmpty()){
                    continue;
                }

                // check if it is a header and record it
                // then continue to the next line
                if(line.endsWith(":")){
                    header = line;
                    continue;
                }

                String[] commaSeparatedParts = parseCommaSeparatedLine(line);

                // TODO(shenukp1) Nov 23, 2026: now we want check which header we are at and then add that line
                // to the appropriate header
                switch (header) {
                    case NAME:
                        addHeader(commaSeparatedParts);
                        break;
                    case LECTURE_SLOTS:
                        addHeader(commaSeparatedParts);
                        break;
                    case TUTORIAL_SLOTS:
                        addHeader(commaSeparatedParts);
                        break;
                    case LECTURES:
                        addHeader(commaSeparatedParts);
                        break;
                    case TUTORIALS:
                        addHeader(commaSeparatedParts);
                        break;
                    case NOT_COMPATIBLE:
                        addHeader(commaSeparatedParts);
                        break;
                    case UNWANTED:
                        addHeader(commaSeparatedParts);
                        break;
                    case PREFERENCES:
                        addHeader(commaSeparatedParts);
                        break;
                    case PAIR:
                        addHeader(commaSeparatedParts);
                        break;
                    case PARTIAL_ASSIGNMENTS:
                        addHeader(commaSeparatedParts);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown section: " + header);
                }


            }
        }
        catch (IOException e) {
            System.err.println("File I/O Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unknown error: " + e.getMessage());
            e.printStackTrace(); // trace to see what when wrong
        }
    
    }

    /**
     * Function that takes a data line, splits it by commas, and cleans up
     * any extra spaces in each part so only single spaces remain, then returns
     * the array of cleaned parts.
     * 
     */
    private static String[] parseCommaSeparatedLine(String line) {
        // According to the spec, everything is separated by commas
        // for all the data under the header
        String[] commaSeparatedParts = line.split(",");
        // we iterate through all the parts do not have extra spaces
        // because the spec says that this is possible
        for(int i =0;i<commaSeparatedParts.length;i++ ){
            // thus, we can replace more than one occurrence of space with
            // just one occurrence. see<https://stackoverflow.com/questions/4329250/if-you-have-multiple-spaces-inside-a-string-in-java-how-do-you-condense-them-in>
            commaSeparatedParts[i] = commaSeparatedParts[i].strip().replaceAll("\\s+", " ");
        }
        return commaSeparatedParts;
    }
    private void addHeader(String[] parsedData){
        this.parsedData = parsedData;
    }

    public String[] getParsedData() {
        return this.parsedData;
    }

}

