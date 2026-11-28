import models.*;

public class Mutator {
    public static Schedule mutate(Schedule s, int seed) {
        // Choose a random class or tutorial using seed number for testability and repeatability
        // Find all the slots it could be moved to
        // If there are valid moves:
            // Randomly choose one of those slots
            // Move it to that slot
        // If there are no valid moves:
            // Go to the next lecture/tutorial and try again
            // If there are no legal mutations for any lecture/tutorial return null

        // Use HardConstraintChecker.isValid() to check each slot
        return s;
    }
    
}
