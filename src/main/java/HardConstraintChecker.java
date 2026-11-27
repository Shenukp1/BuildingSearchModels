public class HardConstraintChecker{

    /**
     * I think the idea is that we give it all the inputs and that will serve
     * as information to check
     */
    public InputFileParsedData data;

    public HardConstraintChecker(InputFileParsedData data){
        this.data = data;
    }

    /**
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


}