
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import models.Schedule;
import models.Slot;

public class HardConstraintChecker{

    /**
     * I think the idea is that we give it all the inputs and that will serve
     * as information to check
     */
    public final int INCREASE_COURSE_COUNT = 1;
    public InputFileParsedData data;
    // A simple way to look up the Slot object via slot key
    private HashMap<String, Slot> lectureSlotMap;
    private HashMap<String, Slot> tutorialSlotMap;

    public HardConstraintChecker(InputFileParsedData data){
        this.data = data;
        this.lectureSlotMap = new HashMap<>();
        this.tutorialSlotMap = new HashMap<>();
        // populating the slot maps for fast look ups
        List<Slot> lecSlots = data.slots.get(InputFileParsedData.LEC);
        if (lecSlots != null) {
            for (Slot s : lecSlots) {
                this.lectureSlotMap.put(s.timeSlot, s);
            }
        }
        List<Slot> tutSlots = data.slots.get(InputFileParsedData.TUT);
        if (tutSlots != null) {
            for (Slot s : tutSlots) {
                this.tutorialSlotMap.put(s.timeSlot, s);
            }
        }
    }

    /**
     * I think the idea is that if we cant check for them, we return true, that way
     * we can use this within the or-tree
     * 
     * isValid class that checks the following hard constraints:
     *  1. checks the capacity lecturemax, tutorialmax, allecturemax, and altutorialmax.
     *     and if alRequired is true for tut or lec, thats when we consider almax
     *  2. check is there isnt a a course that is placed into its unwanted slot
     *  3. check if the course is assign in the partial assigned slot
     *  4. all lec and tut that have 9 prefixed within the number(i.e., LEC 9, LEC 9*)
     *     must be schedules 18:00 and past.
     *  5. No lec can be scheduled on tuesday 11-12pm, this is the department meeting.
     *     - ok so this one nice because we can compare string to check since 
     *       nothing falls between 11-12:30, as in, 10:30-11:30 is not a thing. only on fri
     *       it is.
     *  6. two courses are not compatible 
     *  7. check if lec does not overlap with its own tut/labs
     *  8. 5XX level courses must not be overlapping in time slots
     *  9. cpsc 851 cannot overlap with lec/tut of cpsc 351 and
     *     cpsc 913 cannot overlap any lec/tutorial of cpsc 413
     *  
     */
    public boolean isValid(Schedule schedule){
        // if we fail one of the check, we simply return false,
        // if not we return true
        if(!checkLecAndTutCapacity(schedule)) return false;
        if(!checkPartialAssignments(schedule)) return false;

        return true;
    }
    /**
     * Hard Constraint 1: Capacity check
     * This function simply loops through the schedule and grabs a slot,
     * for that slot, it will increase its count if a lec/tut is put into it.
     * If it reaches the max, we terminate.
     */
    public boolean checkLecAndTutCapacity(Schedule schedule){
        // to keep count of the lec/tut/al 
        HashMap<String, Integer> lectureCount = new HashMap<>();
        HashMap<String, Integer> lectureAlCount = new HashMap<>();
        HashMap<String, Integer> tutorialCount = new HashMap<>();
        HashMap<String, Integer> tutorialCountAlCount = new HashMap<>();

        // loop through the schedule we have and increment count
        // if we reach max, we return false
        for( String lecOrTutId : schedule.assign.keySet()){
            String timeSlot = schedule.assign.get(lecOrTutId);
            // I would assume that if the timeslot does not exist
            // something went wrong
            if(timeSlot == null){
                throw new NullPointerException("Should not happen: for the following lec/tut={"+lecOrTutId+"}, the time slot is null.");
            }
            // we need to check if it is a tutorial or lecture
            if(isLecture(lecOrTutId)){
                Slot slot = lectureSlotMap.get(timeSlot);

                int newLecCountTotal = lectureCount.getOrDefault(timeSlot, 0) + INCREASE_COURSE_COUNT;
                if (newLecCountTotal > slot.max) return false; // we hit the max, hard constraint fails
                lectureCount.put(timeSlot, newLecCountTotal); // else, we increase the count

                // if it is Active learning, then we need to check the count of that too
                if(data.lectures.getOrDefault(lecOrTutId, false)){
                    int newAlTotal = lectureAlCount.getOrDefault(timeSlot, 0) + 1;
                    if (newAlTotal > slot.alMax) return false;
                    lectureAlCount.put(timeSlot, newAlTotal);
                }

            }
            else if (isTutorial(lecOrTutId)) {
                Slot slot = tutorialSlotMap.get(timeSlot);

                int newTutCountTotal = tutorialCount.getOrDefault(timeSlot, 0) + INCREASE_COURSE_COUNT;
                if (newTutCountTotal > slot.max) return false; // we hit the max, hard constraint fails
                tutorialCount.put(timeSlot, newTutCountTotal); // else, we increase the count
                // if it is Active learning, then we need to check the count of that too
                if (data.tutorials.getOrDefault(lecOrTutId, false)) {
                    int newAlTotal = tutorialCountAlCount.getOrDefault(timeSlot, 0) + INCREASE_COURSE_COUNT;
                    if (newAlTotal > slot.alMax) return false;
                    tutorialCountAlCount.put(timeSlot, newAlTotal);
                }
                
            } else {
                throw new IllegalArgumentException("The following course={"+lecOrTutId+"} is not a valid course based on what we parsed.");
            }

            // check for that timeSlot, do we have a current count


        }


        return true;

    }

    /**
     * Hard Constraint 2:
     * 
     * Checking to see if we have partial assignment within the schedule
     */
    public boolean checkPartialAssignments(Schedule schedule) {
        for (String courseId : data.partialAssignments.keySet()) {
            
            String assignedSlot = schedule.assign.get(courseId);
            // if we have not set the partial assignment, then we do not meet the hard constraints
            if (assignedSlot == null) return false;

            // if we have a course id, there must be a timeslot and that timeslot
            // should be the same as the assignedSlot
            Set<String> requiredSlots = data.partialAssignments.get(courseId);
            if (!requiredSlots.contains(assignedSlot)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper functions
     */
    // TODO(shenukp1) Nov 27, 2026: I use this 
    public boolean isLecture(String courseId) {
        return data.lectures.containsKey(courseId);
    }
    public boolean isTutorial(String courseId) {
        // WARNING(shenukp1) Nov 27, 2026: the input problem spec says we add this
        // when we have certain lec but these are tutorials so we need to
        // check in the tutorial section
        if (courseId.startsWith("CPSC 851") || courseId.startsWith("CPSC 913")) {
            return true;
        }
        return data.tutorials.containsKey(courseId);
    }

}