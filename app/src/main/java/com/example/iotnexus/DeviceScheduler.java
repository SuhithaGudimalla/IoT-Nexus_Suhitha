package com.example.iotnexus;

import android.os.Handler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class DeviceScheduler {
    private final Handler handler = new Handler();
    private DatabaseReference scheduleReference;

    private int startHour = 0, startMinute = 0, endHour = 0, endMinute = 0; // Default values to avoid null

    public DeviceScheduler() {
        // Initialize the reference to the schedule in Firebase
        scheduleReference = FirebaseDatabase.getInstance().getReference("device_schedule/device_1");
    }

    public void startCheckingSchedule() {
        // Fetch schedule from Firebase
        scheduleReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Safely retrieve values with null checks
                    Integer fetchedStartHour = snapshot.child("startHour").getValue(Integer.class);
                    Integer fetchedStartMinute = snapshot.child("startMinute").getValue(Integer.class);
                    Integer fetchedEndHour = snapshot.child("endHour").getValue(Integer.class);
                    Integer fetchedEndMinute = snapshot.child("endMinute").getValue(Integer.class);

                    // Assign default values if any field is null
                    startHour = (fetchedStartHour != null) ? fetchedStartHour : 0;
                    startMinute = (fetchedStartMinute != null) ? fetchedStartMinute : 0;
                    endHour = (fetchedEndHour != null) ? fetchedEndHour : 0;
                    endMinute = (fetchedEndMinute != null) ? fetchedEndMinute : 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });

        // Start periodic schedule checking
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkScheduleAndToggleDevice();
                handler.postDelayed(this, 60000); // Check every minute
            }
        }, 60000); // Initial delay of 1 minute
    }

    private void checkScheduleAndToggleDevice() {
        // Fetch current time and compare with the stored schedule
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        // Check if current time matches the start or end time for toggling the device
        if (currentHour == startHour && currentMinute == startMinute) {
            toggleDevice(true);  // Turn on the device
        } else if (currentHour == endHour && currentMinute == endMinute) {
            toggleDevice(false); // Turn off the device
        }
    }

    private void toggleDevice(boolean turnOn) {
        // Toggle device state in Firebase
        String state = turnOn ? "on" : "off";
        FirebaseDatabase.getInstance().getReference("devices/device_1/state").setValue(state);
    }
}