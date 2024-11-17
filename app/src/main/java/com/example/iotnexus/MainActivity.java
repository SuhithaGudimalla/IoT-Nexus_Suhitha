package com.example.iotnexus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Button btnTimerControl;
    private TextView deviceStatus;
    private DatabaseReference databaseReference;
    private DeviceScheduler deviceScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("devices");

        // Initialize UI components
        btnTimerControl = findViewById(R.id.btn_timer_control);
        deviceStatus = findViewById(R.id.device_status);

        // Set up click listener for the Timer Control button
        btnTimerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the TimerControlActivity
                Intent intent = new Intent(MainActivity.this, TimerControlActivity.class);
                startActivity(intent);
            }
        });

        // Initialize DeviceScheduler and start it
        deviceScheduler = new DeviceScheduler();
        deviceScheduler.startCheckingSchedule();  // Start checking the schedule

        // Initialize or update device state in Firebase
        writeToDatabase();
    }

    // Write data to Firebase Realtime Database
    private void writeToDatabase() {
        // Example data for device state
        Device device = new Device("Light Bulb", "off");
        databaseReference.child("devices").child("device_1").setValue(device);

        // Example data for device schedule
        DeviceSchedule schedule = new DeviceSchedule(7, 0, 22, 0);  // start at 7:00 AM and end at 10:00 PM
        databaseReference.child("device_schedule").child("device_1").setValue(schedule);
    }

    // Example schedule class
    public static class DeviceSchedule {
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

    // Example device class
    public static class Device {
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

    // Firebase listener for reading real-time data
    private void readFromDatabase() {
        databaseReference.child("device_1").addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Device device = snapshot.getValue(Device.class);
                    if (device != null) {
                        // Update UI with the retrieved data
                        deviceStatus.setText("Device: " + device.name + " | State: " + device.state);
                    }
                }
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error reading data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start listening to the Firebase database when the activity starts
        readFromDatabase();
    }
}
