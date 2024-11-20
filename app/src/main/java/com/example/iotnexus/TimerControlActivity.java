package com.example.iotnexus;

import android.os.Bundle;
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

                // Create a new DeviceSchedule object
                DeviceSchedule schedule = new DeviceSchedule(startHour, startMinute, endHour, endMinute);

                // Call getNextScheduleKey to get the next available key
                getNextScheduleKey(new NextScheduleKeyCallback() {
                    @Override
                    public void onNextScheduleKeyFetched(String scheduleKey) {
                        // Store the schedule in Firebase under the device ID (e.g., "device_1")
                        databaseReference.child("device_1").child(scheduleKey).setValue(schedule);

                        // Show success message
                        Toast.makeText(TimerControlActivity.this, "Schedule saved successfully!", Toast.LENGTH_SHORT).show();

                        // Navigate back to MainActivity (Home page)
                        finish();  // This will close the current activity and return to MainActivity
                    }
                });
            }
        });
    }

    // Get the next schedule key (e.g., schedule_1, schedule_2, etc.)
    private void getNextScheduleKey(final NextScheduleKeyCallback callback) {
        databaseReference.child("device_1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int maxScheduleNumber = 0;
                for (DataSnapshot scheduleSnapshot : snapshot.getChildren()) {
                    // Check if the schedule key is in the format schedule_X
                    if (scheduleSnapshot.getKey() != null && scheduleSnapshot.getKey().startsWith("schedule_")) {
                        String scheduleKey = scheduleSnapshot.getKey();
                        try {
                            int scheduleNumber = Integer.parseInt(scheduleKey.substring("schedule_".length()));
                            if (scheduleNumber > maxScheduleNumber) {
                                maxScheduleNumber = scheduleNumber;
                            }
                        } catch (NumberFormatException e) {
                            // Ignore invalid schedule key formats
                        }
                    }
                }

                // Generate the next schedule key
                String nextScheduleKey = "schedule_" + (maxScheduleNumber + 1);
                callback.onNextScheduleKeyFetched(nextScheduleKey);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
                callback.onNextScheduleKeyFetched("schedule_1");  // If error, default to schedule_1
            }
        });
    }

    // Callback interface for returning the next schedule key
    interface NextScheduleKeyCallback {
        void onNextScheduleKeyFetched(String scheduleKey);
    }

    // DeviceSchedule class to hold the timer data
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
}
