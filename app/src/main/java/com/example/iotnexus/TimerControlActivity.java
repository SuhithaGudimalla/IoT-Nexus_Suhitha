
package com.example.iotnexus;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class TimerControlActivity extends AppCompatActivity {

    private EditText edtStartHour, edtStartMinute, edtEndHour, edtEndMinute;
    private Button btnSubmit;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_control);

        // Initialize views
        edtStartHour = findViewById(R.id.edt_start_hour);
        edtStartMinute = findViewById(R.id.edt_start_minute);
        edtEndHour = findViewById(R.id.edt_end_hour);
        edtEndMinute = findViewById(R.id.edt_end_minute);
        btnSubmit = findViewById(R.id.btn_submit);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("device_schedule");

        // Set up the submit button listener
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user inputs
                int startHour = Integer.parseInt(edtStartHour.getText().toString());
                int startMinute = Integer.parseInt(edtStartMinute.getText().toString());
                int endHour = Integer.parseInt(edtEndHour.getText().toString());
                int endMinute = Integer.parseInt(edtEndMinute.getText().toString());

                // Create a new DeviceSchedule object with state "ON"
                DeviceSchedule schedule = new DeviceSchedule(startHour, startMinute, endHour, endMinute, "ON");

                // Get schedule count and generate a unique schedule key
                getNextScheduleKey(new ScheduleKeyCallback() {
                    @Override
                    public void onKeyReady(String scheduleKey) {
                        // Store the schedule in Firebase under device_1
                        databaseReference.child("device_1").child(scheduleKey).setValue(schedule);

                        // Show success message
                        Toast.makeText(TimerControlActivity.this, "Schedule saved successfully!", Toast.LENGTH_SHORT).show();

                        // Start state check after submitting
                        startDeviceStateCheck();

                        // Navigate back to MainActivity (Home page)
                        finish();  // Close current activity and return to MainActivity
                    }
                });
            }
        });
    }

    // Start checking device state based on schedule
    private void startDeviceStateCheck() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAndUpdateDeviceState();
                handler.postDelayed(this, 60000); // Check every minute
            }
        }, 60000);
    }

    // Check if current time is within any schedule and update the device state
    private void checkAndUpdateDeviceState() {
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        databaseReference.child("device_1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                    DeviceSchedule schedule = scheduleSnapshot.getValue(DeviceSchedule.class);  // Get the actual schedule object

                    if (schedule != null) {
                        // Check if current time is within the schedule
                        boolean isInTimePeriod = (currentHour > schedule.startHour || (currentHour == schedule.startHour && currentMinute >= schedule.startMinute)) &&
                                (currentHour < schedule.endHour || (currentHour == schedule.endHour && currentMinute <= schedule.endMinute));

                        // Update state based on time check
                        if (isInTimePeriod) {
                            schedule.state = "ON"; // Set state to ON if within schedule
                        } else {
                            schedule.state = "OFF"; // Set state to OFF if outside schedule
                        }

                        // Update Firebase with the new state
                        databaseReference.child("device_1").child(scheduleSnapshot.getKey()).child("state").setValue(schedule.state);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
    }

    // Get the next schedule key based on the highest existing schedule key
    private void getNextScheduleKey(final ScheduleKeyCallback callback) {
        databaseReference.child("device_1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int highestScheduleNumber = 0;

                // Iterate through the existing schedules to find the highest schedule number
                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                    String key = scheduleSnapshot.getKey();
                    if (key != null && key.startsWith("schedule_")) {
                        try {
                            int scheduleNumber = Integer.parseInt(key.substring("schedule_".length()));
                            highestScheduleNumber = Math.max(highestScheduleNumber, scheduleNumber);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Generate the next schedule key
                String nextScheduleKey = "schedule_" + (highestScheduleNumber + 1);

                // Return the next schedule key using the callback
                callback.onKeyReady(nextScheduleKey);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
    }

    // Define a callback interface for getting the next schedule key
    interface ScheduleKeyCallback {
        void onKeyReady(String scheduleKey);
    }
}
