
package models;
import java.util.HashMap;

class Schedule {
    // maps "CPSC 231 LEC 01" to the time "MO, 8:00"
    public HashMap<String, String> assign;
    public int evalScore = -1; // since 0 is the best technically, so we will use negative -1 as the inital

    public Schedule() {
        this.assign =  new HashMap<>();
    }

    // since referencing of object and all being a problem
    // we can do this: Schedule child = new Schedule(parent); with
    // the following constructor below
    public Schedule(Schedule other) {
        this.assign = new HashMap<>(other.assign);
    }
}