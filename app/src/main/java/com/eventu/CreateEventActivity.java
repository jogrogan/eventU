package com.eventu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows the user to create an event
 */
public class CreateEventActivity extends BaseClass {

    // NO HARDCODING! Tags used in place of strings
    private static final String EVENT_NAME = "EventName";
    private static final String EVENT_LOCATION = "EventLocation";
    private static final String EVENT_DESCRIPTION = "EventDescription";
    private static final String EVENT_DATE = "EventDate";
    private static final String EVENT_CREATOR = "EventCreator";
    private static final String EVENT_TALLY = "EventTally";
    private static final String CLUB_ID = "ClubID";

    // Firebase References
    private final FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

    // UI References
    private EditText mEventName;
    private EditText mEventLocation;
    private EditText mEventDescription;
    private TimePicker mTimePicker;
    private DatePicker mDatePicker;
    // Database References
    private CollectionReference mSchoolClubEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_event);
        mEventName = findViewById(R.id.event_name);
        mEventDescription = findViewById(R.id.event_description);
        mEventLocation = findViewById(R.id.event_location);
        mTimePicker = findViewById(R.id.tp_timepicker);
        mDatePicker = findViewById(R.id.dp_datepicker);
        mDatePicker.setMinDate(System.currentTimeMillis());
        FloatingActionButton nextButton = findViewById(R.id.next_button);

        if (mCurrentUser == null) {
            Log.d("NULL", "NULL user found.");
            return;
        }
        String mEventPath = "universities/" + mCurrentUser.getDisplayName() + "/Club Events";
        mSchoolClubEvents = FirebaseFirestore.getInstance().collection(mEventPath);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAddEvent();
            }
        });

    }

    /**
     * Attempts to add the user inputted information into the event database
     * Includes error handling for missing and invalid fields.
     */
    private void attemptAddEvent() {
        String eventName = mEventName.getText().toString();
        String eventLocation = mEventLocation.getText().toString();
        String eventDescription = mEventDescription.getText().toString();

        View focusView = null;
        if (!isValidTime()) {
            Toast.makeText(this, R.string.error_timepicker, Toast.LENGTH_SHORT).show();
            focusView = mTimePicker;
        }

        if (eventDescription.isEmpty()) {
            mEventDescription.setError(getString(R.string.error_field_required));
            focusView = mEventDescription;
        }

        if (eventLocation.isEmpty()) {
            mEventLocation.setError(getString(R.string.error_field_required));
            focusView = mEventLocation;
        }

        if (eventName.isEmpty()) {
            mEventName.setError(getString(R.string.error_field_required));
            focusView = mEventName;
        }

        if (focusView != null) {
            focusView.requestFocus();
            return;
        }

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EVENT_NAME, eventName);
        eventData.put(EVENT_LOCATION, eventLocation);
        eventData.put(EVENT_DESCRIPTION, eventDescription);
        Date eventDate = new GregorianCalendar(
                mDatePicker.getYear(),
                mDatePicker.getMonth(),
                mDatePicker.getDayOfMonth(),
                mTimePicker.getHour(),
                mTimePicker.getMinute()).getTime();
        eventData.put(EVENT_DATE, eventDate);
        eventData.put(EVENT_CREATOR, getIntent().getStringExtra("username"));
        eventData.put(EVENT_TALLY, 0);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            eventData.put(CLUB_ID, user.getUid());
        }

        mSchoolClubEvents.add(eventData)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateEventActivity.this, "Successful Write!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String message;
                            if (task.getException() == null) {
                                message = "null pointer exception";
                            } else {
                                message = task.getException().getMessage();
                            }
                            Toast.makeText(CreateEventActivity.this,
                                    message,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        finish();
    }

    /**
     * Makes sure the time and date entered in the timepicker and datepicker
     * refer to a future event.
     */
    private boolean isValidTime() {
        Calendar calendar = new GregorianCalendar(mDatePicker.getYear(),
                mDatePicker.getMonth(),
                mDatePicker.getDayOfMonth(),
                mTimePicker.getHour(),
                mTimePicker.getMinute());

        return calendar.compareTo(Calendar.getInstance()) > 0;
    }
}
