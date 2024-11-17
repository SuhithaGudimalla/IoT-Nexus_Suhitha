package com.example.iotnexus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TimerControlActivity extends AppCompatActivity {

    private TimePicker startTimePicker;
    private TimePicker endTimePicker;
    private Button btnSubmitSchedule;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_control);

        // Initialize the UI components
        startTimePicker = findViewById(R.id.start_time_picker);
        endTimePicker = findViewById(R.id.end_time_picker);
        btnSubmitSchedule = findViewById(R.id.btn_submit_schedule);
        errorMessage = findViewById(R.id.error_message);

        // Set onClick listener for the submit button
        btnSubmitSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSubmitSchedule();
            }
        });
    }

    // Validate start time and end time
    private void validateAndSubmitSchedule() {
        int startHour = startTimePicker.getHour();
        int startMinute = startTimePicker.getMinute();
        int endHour = endTimePicker.getHour();
        int endMinute = endTimePicker.getMinute();

        // Check if the start time is later than the end time
        if (isStartTimeAfterEndTime(startHour, startMinute, endHour, endMinute)) {
            errorMessage.setVisibility(View.VISIBLE);  // Show error message
        } else {
            errorMessage.setVisibility(View.GONE);  // Hide error message
            // Proceed with saving the schedule
            saveSchedule(startHour, startMinute, endHour, endMinute);
            Toast.makeText(TimerControlActivity.this, "Schedule Saved", Toast.LENGTH_SHORT).show();
        }
    }

    // Check if start time is after end time
    private boolean isStartTimeAfterEndTime(int startHour, int startMinute, int endHour, int endMinute) {
        // Compare times as minutes of the day
        int startTimeInMinutes = startHour * 60 + startMinute;
        int endTimeInMinutes = endHour * 60 + endMinute;
        return startTimeInMinutes > endTimeInMinutes;
    }

    // Save the schedule (this is where you'd handle database saving)
    private void saveSchedule(int startHour, int startMinute, int endHour, int endMinute) {
        // Add your database logic here to save the schedule
    }
}
