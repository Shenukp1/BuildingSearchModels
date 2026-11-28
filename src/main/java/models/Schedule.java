
package models;
import java.util.HashMap;
import java.util.Objects;

public class Schedule {
    // maps "CPSC 231 LEC 01" to the time "MO, 8:00"
    /**
     * TODO(shenukp1) Nov 26, 2026: maybe we should add some kind of checks
     * to say that the key needs to be a lec and the value is the time
     * gotta use it like the following:
     *  1. schedule.assign.put("CPSC 231 LEC 01", "MO, 8:00");
     */
    public HashMap<String, String> assign;
    public int evalScore; // since 0 is the best technically, so we will use negative -1 as the inital

    public Schedule() {
        this.assign =  new HashMap<>();
        this.evalScore = -1;
    }

    // since referencing of object and all being a problem
    // we can do this: Schedule child = new Schedule(parent); with
    // the following constructor below
    public Schedule(Schedule other) {
        this.assign = new HashMap<>(other.assign);
        this.evalScore = other.evalScore;
    }

    /**
     * This helps us compare schedules
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schedule)) return false;
        Schedule other = (Schedule) o;
        return Objects.equals(this.assign, other.assign);
    }
    @Override
    public int hashCode() {
        return Objects.hash(this.assign);
    }

}