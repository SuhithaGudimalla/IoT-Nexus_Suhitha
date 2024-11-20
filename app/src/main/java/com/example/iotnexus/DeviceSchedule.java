package com.example.iotnexus;

public class DeviceSchedule {
    public int startHour;
    public int startMinute;
    public int endHour;
    public int endMinute;
    public String state;

    // Default constructor required for Firebase
    public DeviceSchedule() {
    }

    // Constructor with parameters for start and end time, and state
    public DeviceSchedule(int startHour, int startMinute, int endHour, int endMinute, String state) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.state = state;
    }
}
