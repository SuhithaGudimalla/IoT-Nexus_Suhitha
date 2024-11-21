package com.example.iotnexus;

public class Device {
    public String name;
    public String state;

    // Default constructor required for Firebase
    public Device() {
    }

    public Device(String name, String state) {
        this.name = name;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
