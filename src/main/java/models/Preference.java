package models;
public class Preference {
    public String courseId;
    public String slotTime; 
    public int preference;

    public Preference(String courseId, String slotTime, int preference) {
        this.slotTime = slotTime;
        this.preference = preference;
        this.courseId = courseId;
    }
}