package com.example.iotnexus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
        databaseReference = FirebaseDatabase.getInstance().getReference();

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

        // Initialize DeviceScheduler and start checking the schedule
        deviceScheduler = new DeviceScheduler();
        deviceScheduler.startCheckingSchedule();

        // Initialize or update device and schedule in Firebase
        writeToDatabase();
    }

    // Write data to Firebase Realtime Database
    private void writeToDatabase() {
        // Example device state data
        Device device = new Device("Light Bulb", "off");
        databaseReference.child("devices").child("device_1").setValue(device);

        // Example schedule data
        DeviceSchedule schedule = new DeviceSchedule(7, 0, 22, 0); // Start at 7:00 AM and end at 10:00 PM
        databaseReference.child("device_schedule").child("device_1").setValue(schedule);
    }

    // Example DeviceSchedule class
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

    // Example Device class
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

    // Read real-time data from Firebase
    private void readFromDatabase() {
        databaseReference.child("device_schedule").child("device_1").addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DeviceSchedule schedule = snapshot.getValue(DeviceSchedule.class);
                    if (schedule != null) {
                        int startHour = schedule.startHour;
                        int startMinute = schedule.startMinute;
                        int endHour = schedule.endHour;
                        int endMinute = schedule.endMinute;

                        // Update device status TextView or log the schedule
                        deviceStatus.setText(String.format("Schedule: Start - %02d:%02d, End - %02d:%02d",
                                startHour, startMinute, endHour, endMinute));
                    } else {
                        deviceStatus.setText("No schedule data found.");
                    }
                } else {
                    deviceStatus.setText("No data available.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
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
