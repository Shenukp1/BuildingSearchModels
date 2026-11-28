import models.*;

public class Mutator {
    public static Schedule mutate(Schedule s, int seed){
        // Choose a random class or tutorial using seed number for testability and repeatability
        // Find all the slots it could be moved to
        // If there are valid moves:
            // Randomly choose one of those slots
            // Move it to that slot
        // If there are not valid moves:
            // If there are no legal mutations go to the next class/lecture and try again

        // A lecture l can be moved to a slot s iff:
            // not more than lecutremax
            // not more than tutorialmax
            // not more than ALlecturemax
            // not more than ALtutorialmax
            // no notcompatible(l, x) where x is in slot s
            // l isn't in partassign 
            // it's not unwanted(l, s)

        return s;
    }
    
}
