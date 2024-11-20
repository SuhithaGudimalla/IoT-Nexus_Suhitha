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
        btnTimerControl.setOnClickListener(v -> {
            // Start the TimerControlActivity
            Intent intent = new Intent(MainActivity.this, TimerControlActivity.class);
            startActivity(intent);
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
        DeviceSchedule schedule = new DeviceSchedule(7, 0, 22, 0, "ON"); // Pass the state as "ON"
        databaseReference.child("device_schedule").child("device_1").setValue(schedule);
    }

    // Read real-time data from Firebase
    private void readFromDatabase() {
        databaseReference.child("devices").child("device_1").addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Device device = snapshot.getValue(Device.class);
                    if (device != null) {
                        // Update device status based on Firebase value
                        String state = device.state;
                        deviceStatus.setText("Device State: " + state);
                    } else {
                        deviceStatus.setText("No device data found.");
                    }
                } else {
                    deviceStatus.setText("No device data available.");
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
