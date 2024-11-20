package com.example.iotnexus;

public class Device {
    public String name;
    public String state;

    public Device() {
        // Default constructor required for Firebase
    }

    public Device(String name, String state) {
        this.name = name;
        this.state = state;
    }
}
