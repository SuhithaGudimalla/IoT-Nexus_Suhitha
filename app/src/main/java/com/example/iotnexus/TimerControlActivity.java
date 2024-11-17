package com.example.iotnexus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

                // Store the schedule in Firebase under the device ID (e.g., "device_1")
                databaseReference.child("device_1").setValue(schedule);

                // Show success message
                Toast.makeText(TimerControlActivity.this, "Schedule saved successfully!", Toast.LENGTH_SHORT).show();

                // Navigate back to MainActivity (Home page)
                finish();  // This will close the current activity and return to MainActivity
            }
        });

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