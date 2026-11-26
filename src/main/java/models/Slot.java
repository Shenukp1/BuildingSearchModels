package models;

public class Slot {
    public String timeSlot;
    public int max;
    public int min;
    public int alMax;

    public Slot(String timeSlot, int max, int min, int alMax) {
        this.timeSlot = timeSlot;
        this.max = max;
        this.min = min;
        this.alMax = alMax;
    }
}
