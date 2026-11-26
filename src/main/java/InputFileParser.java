import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import models.Slot;

/**
 * Parses the input file given to the program to start the set based search on.
 * 
 * IMPORTANT(shenukp1) Nov 25, 2026: so technically we can handle some hard constraints here
 * but maybe that is not the move. as in:
 *  - if we see that lectures starting with CPSC 351 and/or CPSC 413, we have to schedule CPSC 851 and/or CPSC 913 into tut
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
    private InputFileParsedData inputFileParsedData; // contains all the parsed data


    
    public InputFileParser(Path filepath){
        this.filePath = filepath;
        this.inputFileParsedData = new InputFileParsedData();
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

                // based on the header, we can parse and place the data into its respective
                // data structure 
                switch (header) {
                    case NAME:
                        this.inputFileParsedData.name = line;
                        break;
                    case LECTURE_SLOTS:
                        addTimeSlot(commaSeparatedParts, InputFileParsedData.LEC);
                        break;
                    case TUTORIAL_SLOTS:
                        addTimeSlot(commaSeparatedParts, InputFileParsedData.TUT);
                        break;
                    case LECTURES:
                        addLecture(commaSeparatedParts);
                        break;
                    case TUTORIALS:
                        addTutorial(commaSeparatedParts);
                        break;
                    case NOT_COMPATIBLE:
                    
                        break;
                    case UNWANTED:
            
                        break;
                    case PREFERENCES:
        
                        break;
                    case PAIR:
    
                        break;
                    case PARTIAL_ASSIGNMENTS:

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
     * Function that places all the possible slots into a map where 
     * the key tell you if it is a tut or a lec. then the value
     * are the lec/tut slot data under the header from the input file.
     * WARNING(shenukp1) Nov 25, 2026: made these public so I can test them eventually
     */
    public void addTimeSlot(String[] parts, String type) {
        if (!InputFileParsedData.LEC.equals(type) && !InputFileParsedData.TUT.equals(type)) {
            throw new IllegalArgumentException("Not allowed that type. Choose between: " + InputFileParsedData.LEC + " or " + InputFileParsedData.TUT);
        }
        String timeSlot = parts[0] + ", " + parts[1]; // "MO, 8:00" was split so we need to bring it back together
        int max = Integer.parseInt(parts[2]);
        int min = Integer.parseInt(parts[3]);
        int alMax = Integer.parseInt(parts[4]);
        Slot slot = new Slot(timeSlot, max, min, alMax);
        this.inputFileParsedData.slots.get(type).add(slot);
    }
    /**
     * A function that adds lec into the InputFileParsedData.
     * WARNING(shenukp1) Nov 25, 2026: made these public so I can test them eventually
     */
    public void addLecture(String[] parts) {
        String courseId = parts[0];
        boolean alRequired = Boolean.parseBoolean(parts[1]);
        this.inputFileParsedData.lectures.put(courseId, alRequired);
       
    }
    /**
     * A function that add a tut.   
     * WARNING(shenukp1) Nov 25, 2026: made these public so I can test them eventually
     */
    public void addTutorial(String[] parts) {
        String courseId = parts[0];
        boolean alRequired = Boolean.parseBoolean(parts[1]);
        this.inputFileParsedData.tutorials.put(courseId, alRequired);
        
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

    public InputFileParsedData getInputFileParsedData(){
        return this.inputFileParsedData;
    }


}

