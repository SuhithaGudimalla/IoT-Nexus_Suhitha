package com.example.iotnexus;

import android.os.Handler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class DeviceScheduler {
    private final Handler handler = new Handler();
    private DatabaseReference scheduleReference;

    private int startHour, startMinute, endHour, endMinute;

    public DeviceScheduler() {
        // Initialize the reference to the schedule in Firebase
        scheduleReference = FirebaseDatabase.getInstance().getReference("device_schedule");
    }

    public void startCheckingSchedule() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkScheduleAndToggleDevice();
                handler.postDelayed(this, 60000); // Check every minute
            }
        }, 60000); // Initial delay of 1 minute

        // Fetch schedule from Firebase
        scheduleReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Assuming your Firebase data structure has start and end times
                    startHour = snapshot.child("startHour").getValue(Integer.class);
                    startMinute = snapshot.child("startMinute").getValue(Integer.class);
                    endHour = snapshot.child("endHour").getValue(Integer.class);
                    endMinute = snapshot.child("endMinute").getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError error) {
                // Handle error
            }
        });
    }

    private void checkScheduleAndToggleDevice() {
        // Fetch current time and compare with the stored schedule
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        // Check if current time matches the start or end time for toggling the device
        if (currentHour == startHour && currentMinute == startMinute) {
            toggleDevice(true);  // Turn on device
        } else if (currentHour == endHour && currentMinute == endMinute) {
            toggleDevice(false); // Turn off device
        }
    }

    private void toggleDevice(boolean turnOn) {
        // Example toggle logic
        String state = turnOn ? "on" : "off";
        FirebaseDatabase.getInstance().getReference("devices/device_1/state").setValue(state);
    }
}
