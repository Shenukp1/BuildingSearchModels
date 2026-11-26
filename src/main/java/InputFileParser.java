import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
                        addNotCompatible(commaSeparatedParts);
                        break;
                    case UNWANTED:
                        addUnwanted(commaSeparatedParts);
                        break;
                    case PREFERENCES:
                        
                        break;
                    case PAIR:
                        addPair(commaSeparatedParts);
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

    public void addNotCompatible(String[] parts) {
        String a = parts[0];
        String b = parts[1];
        // the spec says it is symmetric. as in, if LEC A is not compatible with LEC B
        // the the opposite is true too 
        addToSetMap(this.inputFileParsedData.notCompatible, a, b);
        addToSetMap(this.inputFileParsedData.notCompatible, b, a);
    }

    public void addUnwanted(String[] parts) {

        String courseId = parts[0];
        // because we split it by comma, we need to put it back together
        String timeSlot = parts[1] + ", " + parts[2];

        // TODO(shenukp1) Nov 25, 2026: consider if we can possibly flip timeslot and course id,
        // because then we simply search "for time slot x, what is unwanted"
        addToSetMap(this.inputFileParsedData.unwanted, courseId, timeSlot);
    }
    
    public void addPair(String[] parts) {
        String a = parts[0];
        String b = parts[1];
        addToSetMap(this.inputFileParsedData.pairs, a, b);
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
    /**
     * A function to make it easy to create a new key and a set to it
     * if that key does not exist.
     * 
     * This function will be used to add to: not-compatible, unwanted, partial assignment, pre-assigned, and pair
     */
    public void addToSetMap(HashMap<String, Set<String>> map, String key, String value) {
        if (!map.containsKey(key)) {
            map.put(key, new HashSet<>());
        }
        map.get(key).add(value);
    }

    public InputFileParsedData getInputFileParsedData(){
        return this.inputFileParsedData;
    }


}

