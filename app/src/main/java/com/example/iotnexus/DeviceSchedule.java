package com.example.iotnexus;

public class DeviceSchedule {
    public int startHour;
    public int startMinute;
    public int endHour;
    public int endMinute;

    public DeviceSchedule() {
        // Default constructor required for Firebase
    }

    public DeviceSchedule(int startHour, int startMinute, int endHour, int endMinute) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }
}
