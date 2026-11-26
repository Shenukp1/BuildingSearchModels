import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import models.Preference;
import models.Slot;

/**
 * Holds all the data that we parse from the input file.
 * Sorted based on the headers.
 * 
 * IMPORTANT(shenukp1) Nov 25, 2026: this can clearly be subject to change
 */
public class InputFileParsedData {


    public String name;

    // for slots, the key is if it is a lecture or a tut and then the object will contain
    // the slot(tut/lec) information(i.e., Lec: Day, Start time, lecturemax, lecturemin, allecturemax
    // or for Tut: Day, Start time, tutorialmax, tutorialmin, altutorialmax)
    public static final String LEC = "LEC";
    public static final String TUT = "TUT";
    public HashMap<String, List<Slot>> slots;

    // for lect/tut, the key is course ID (i.e., "CPSC 231 LEC 01") -> true/false for alRequired
    public HashMap<String, Boolean> lectures;
    public HashMap<String, Boolean> tutorials;

    // for each header not-compatible, unwanted, partial assignment, pre-assigned, and pair,
    // we use a map where the key is course ID and the values are a set
    public HashMap<String, Set<String>> notCompatible;
    public HashMap<String, Set<String>> unwanted;
    public HashMap<String, Set<String>> partialAssignments;
    public HashMap<String, Set<String>> preAssigned;
    public HashMap<String, Set<String>> pairs;

    // preference can be a list of tuples (aka a class of preference)
    public ArrayList<Preference> preferences;

    public InputFileParsedData() {
        // IMPORTANT(shenukp1) Nov 25, 2026: all classes are initialized(i.e., new ...) so I can preform their
        // methods on them
        this.name = "";
        this.slots = new HashMap<>();
        this.slots.put(LEC, new ArrayList<>());
        this.slots.put(TUT, new ArrayList<>());

        this.lectures = new HashMap<>();
        this.tutorials = new HashMap<>();
        this.notCompatible = new HashMap<>();
        this.unwanted = new HashMap<>();
        this.partialAssignments = new HashMap<>();
        this.preAssigned = new HashMap<>();
        this.pairs = new HashMap<>();
        this.preferences = new ArrayList<>();
    }
    
    /**
     * simple helper to print everything out and verify our parser works
     */
    public void printAll() {
        System.out.println("Name: " + this.name);
        System.out.println("\nLecture slots:");
        for (Slot s : this.slots.get(LEC)) {
            System.out.println("  " + s.timeSlot + " (max=" + s.max + ", min=" + s.min + ", alMax=" + s.alMax + ")");
        }
        System.out.println("\nTutorial slots:");
        for (Slot s : this.slots.get(TUT)) {
            System.out.println("  " + s.timeSlot + " (max=" + s.max + ", min=" + s.min + ", alMax=" + s.alMax + ")");
        }
        System.out.println("\nLectures: " + this.lectures);
        System.out.println("Tutorials: " + this.tutorials);
        System.out.println("Not compatible: " + this.notCompatible);
        System.out.println("Unwanted: " + this.unwanted);
        System.out.println("Pairs: " + this.pairs);
        System.out.println("Partial assignments: " + this.partialAssignments);
        System.out.println("\nPreferences:");
        for (Preference p : this.preferences) {
            System.out.println("  " + p.slotTime + " -> " + p.courseId + " (value=" + p.preference + ")");
        }
    }
}