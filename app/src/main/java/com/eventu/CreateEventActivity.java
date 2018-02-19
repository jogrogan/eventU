package com.eventu;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class CreateEventActivity extends AppCompatActivity {

    //UI References
    private EditText mEventName;
    private EditText mEventLocation;
    private EditText mEventDescription;
    private TimePicker mTimePicker;
    private DatePicker mDatePicker;
    private FloatingActionButton nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time_and_date_event);


        mEventName = findViewById(R.id.event_name);
        mEventDescription = findViewById(R.id.event_description);
        mEventLocation = findViewById(R.id.event_location);
        mTimePicker = findViewById(R.id.tp_timepicker);
        mDatePicker = findViewById(R.id.dp_datepicker);

        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Toast.makeText(CreateEventActivity.this,
                        "Hour/Minutes: " + hourOfDay + ':' + minute,
                        Toast.LENGTH_SHORT).show();
            }
        });

        mDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Toast.makeText(CreateEventActivity.this,
                        "Month/Day/Year: " + monthOfYear + "/" + dayOfMonth + "/" + year,
                        Toast.LENGTH_SHORT).show();
            }
        });

        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CreateEventActivity.this,
                        "Event Name: " + mEventName.getText().toString(),
                        Toast.LENGTH_SHORT).show();
                Toast.makeText(CreateEventActivity.this,
                        "Event Locaiton: " + mEventLocation.getText().toString(),
                        Toast.LENGTH_SHORT).show();
                Toast.makeText(CreateEventActivity.this,
                        "Event Description: " + mEventDescription.getText().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
