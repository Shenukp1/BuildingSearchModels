import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import models.Schedule;

class HardConstraintCheckerTest {

    @TempDir
    Path tempDir;

    
    
    private static final String BASE_INPUT = """
            Name:
            CapacityTest

            Lecture slots:
            MO, 8:00, 2, 0, 1
            MO, 9:00, 2, 0, 1

            Tutorial slots:
            MO, 8:00, 2, 0, 1
            MO, 9:00, 2, 0, 1

            Lectures:
            CPSC 231 LEC 01, false
            CPSC 231 LEC 02, false
            CPSC 233 LEC 01, true
            CPSC 331 LEC 01, true

            Tutorials:
            CPSC 231 LEC 01 TUT 01, false
            CPSC 231 LEC 02 TUT 01, false
            CPSC 233 LEC 01 TUT 01, true
            CPSC 331 LEC 01 TUT 01, true
            """;

    // WARNING(shenukp1) Nov 27, 2026: this is a bit of duplicated code lol
    public Path createTempInputFile(String content) throws IOException {
        Path file = tempDir.resolve("input.txt");
        Files.writeString(file, content);
        return file;
    }
    /**
     * 
     */
    public HardConstraintChecker createChecker(String inputContent) throws IOException {
        Path file = createTempInputFile(inputContent);
        InputFileParser parser = new InputFileParser(file);
        parser.parseInputFile();
        return new HardConstraintChecker(parser.getInputFileParsedData());
    }



    @Test
    void emptyScheduleIsValid() throws IOException {
        HardConstraintChecker checker = createChecker(BASE_INPUT);
        Schedule schedule = new Schedule();

        assertTrue(checker.isValid(schedule));
    }


    /**
     *  Constraint 1: Checking Lecture & Tutorial Capacity
     */
    @Test
    void validWhenWithinLectureAndTutorialCapacity() throws IOException {
        HardConstraintChecker checker = createChecker(BASE_INPUT);
        Schedule schedule = new Schedule();

        // If we add to  MO, 8:00 twice and the BASE_INPUT has max=2, alMax=1
        // for that slot 1 normal + 1 AL is valid
        schedule.assign.put("CPSC 231 LEC 01", "MO, 8:00");
        schedule.assign.put("CPSC 233 LEC 01", "MO, 8:00");

        // MO, 9:00 tutorial slot has max=2, alMax=1, thus 1 normal + 1 AL is valid
        schedule.assign.put("CPSC 231 LEC 02 TUT 01", "MO, 9:00");
        schedule.assign.put("CPSC 331 LEC 01 TUT 01", "MO, 9:00");

        assertTrue(checker.isValid(schedule));
    }

    @Test
    void failsWhenLectureExceedsMax() throws IOException {
        HardConstraintChecker checker = createChecker(BASE_INPUT);
        Schedule schedule = new Schedule();

        // MO, 8:00 lecture slot has max=2, thus 3 lectures should fail
        schedule.assign.put("CPSC 231 LEC 01", "MO, 8:00");
        schedule.assign.put("CPSC 231 LEC 02", "MO, 8:00");
        schedule.assign.put("CPSC 233 LEC 01", "MO, 8:00");

        assertFalse(checker.isValid(schedule));
    }

    @Test
    void failsWhenLectureExceedsAlMax() throws IOException {
        HardConstraintChecker checker = createChecker(BASE_INPUT);
        Schedule schedule = new Schedule();

        // MO, 8:00 lecture slot has max=2, alMax=1, thus 2 AL lectures fails alMax
        schedule.assign.put("CPSC 233 LEC 01", "MO, 8:00");
        schedule.assign.put("CPSC 331 LEC 01", "MO, 8:00");

        assertFalse(checker.isValid(schedule));
    }

    @Test
    void failsWhenTutorialExceedsMax() throws IOException {
        HardConstraintChecker checker = createChecker(BASE_INPUT);
        Schedule schedule = new Schedule();

        // MO, 8:00 tutorial slot has max=2, thus 3 tutorials should fail
        schedule.assign.put("CPSC 231 LEC 01 TUT 01", "MO, 8:00");
        schedule.assign.put("CPSC 231 LEC 02 TUT 01", "MO, 8:00");
        schedule.assign.put("CPSC 233 LEC 01 TUT 01", "MO, 8:00");

        assertFalse(checker.isValid(schedule));
    }

    @Test
    void failsWhenTutorialExceedsAlMax() throws IOException {
        HardConstraintChecker checker = createChecker(BASE_INPUT);
        Schedule schedule = new Schedule();

        // MO, 8:00 tutorial slot has max=2, alMax=1, thus 2 AL tutorials fails alMax
        schedule.assign.put("CPSC 233 LEC 01 TUT 01", "MO, 8:00");
        schedule.assign.put("CPSC 331 LEC 01 TUT 01", "MO, 8:00");

        assertFalse(checker.isValid(schedule));
    }

    @Test
    void throwsForUnknownCourseInSchedule() throws IOException {
        HardConstraintChecker checker = createChecker(BASE_INPUT);
        Schedule schedule = new Schedule();

        schedule.assign.put("CPSC 999 LEC 01", "MO, 8:00");

        assertThrows(IllegalArgumentException.class, () -> checker.isValid(schedule));
    }

    @Test
    void throwsForNullTimeSlotInSchedule() throws IOException {
        // WARNING(shenukp1) Nov 27, 2026: this should not happen.
        HardConstraintChecker checker = createChecker(BASE_INPUT);
        Schedule schedule = new Schedule();

        schedule.assign.put("CPSC 231 LEC 01", null);

        assertThrows(NullPointerException.class, () -> checker.isValid(schedule));
    }

  
    /**
     *  Constraint 2: Partial Assignments
     */

    @Test
    void validWhenPartialAssignmentsMatchRequiredSlots() throws IOException {
        // we just add in addition to the base input to consider the partial assignment header
        HardConstraintChecker checker = createChecker(BASE_INPUT + """
                Partial assignments:
                CPSC 231 LEC 01, MO, 8:00
                CPSC 231 LEC 02 TUT 01, MO, 9:00
                """);
        Schedule schedule = new Schedule();

        schedule.assign.put("CPSC 231 LEC 01", "MO, 8:00");
        schedule.assign.put("CPSC 231 LEC 02 TUT 01", "MO, 9:00");

        assertTrue(checker.isValid(schedule));
    }

    @Test
    void failsWhenPartialAssignmentInWrongSlot() throws IOException {
        HardConstraintChecker checker = createChecker(BASE_INPUT + """
                Partial assignments:
                CPSC 231 LEC 01, MO, 8:00
                """);
        Schedule schedule = new Schedule();

        // requires cpsc 231 in MO, 8:00, but placed in MO, 9:00
        schedule.assign.put("CPSC 231 LEC 01", "MO, 9:00");

        assertFalse(checker.isValid(schedule));
    }

    @Test
    void failsWhenPartialAssignmentIsMissingFromSchedule() throws IOException {
        HardConstraintChecker checker = createChecker(BASE_INPUT + """
                Partial assignments:
                CPSC 231 LEC 01, MO, 8:00
                """);
        Schedule schedule = new Schedule();

        // CPSC 231 LEC 01 was never assigned in the schedule, instead was lec 2
        schedule.assign.put("CPSC 231 LEC 02", "MO, 8:00");

        assertFalse(checker.isValid(schedule));
    }



    /**
     * Constraint 3: unwanted 
     * Checking if a lec or tut is placed into an unwanted slot
     */
    @Test
    void validWhenCoursesNotInUnwantedSlots() throws IOException {
        HardConstraintChecker checker = createChecker(BASE_INPUT + """
                Unwanted:
                CPSC 231 LEC 01, MO, 8:00
                CPSC 231 LEC 02 TUT 01, MO, 9:00
                """);
        Schedule schedule = new Schedule();

        // if we place them in slots that are not in their unwanted list, thus it should be valid
        schedule.assign.put("CPSC 231 LEC 01", "MO, 9:00");
        schedule.assign.put("CPSC 231 LEC 02 TUT 01", "MO, 8:00");

        assertTrue(checker.isValid(schedule));
    }

    @Test
    void failsWhenLectureInUnwantedSlot() throws IOException {
        HardConstraintChecker checker = createChecker(BASE_INPUT + """
                Unwanted:
                CPSC 231 LEC 01, MO, 8:00
                """);
        Schedule schedule = new Schedule();

        // we put the lec in MO, 8:00 which is unwanted, thus this should fail
        schedule.assign.put("CPSC 231 LEC 01", "MO, 8:00");

        assertFalse(checker.isValid(schedule));
    }

    @Test
    void failsWhenTutorialInUnwantedSlot() throws IOException {
        HardConstraintChecker checker = createChecker(BASE_INPUT + """
                Unwanted:
                CPSC 231 LEC 01 TUT 01, MO, 9:00
                """);
        Schedule schedule = new Schedule();

        // we put the tut in MO, 9:00 which is unwanted, thus this should fail
        schedule.assign.put("CPSC 231 LEC 01 TUT 01", "MO, 9:00");

        assertFalse(checker.isValid(schedule));
    }
}
