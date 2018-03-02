package com.eventu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows the user to create an event
 */
public class CreateEventActivity extends BaseClass {

    // NO HARDCODING! Tags used in place of strings
    private static final String EVENT_NAME = "Event Name";
    private static final String EVENT_LOCATION = "Event Location";
    private static final String EVENT_DESCRIPTION = "Event Description";
    private static final String EVENT_DATE = "Event Date";
    private static final String OWNER = "Event Creator";
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
        FloatingActionButton nextButton = findViewById(R.id.next_button);

        if (mCurrentUser == null || mCurrentUser.getDisplayName() == null) {
            return;
        }
        String mEventPath = "universities/" + mCurrentUser.getDisplayName() + "/Club Events";
        mSchoolClubEvents = FirebaseFirestore.getInstance().collection(mEventPath);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> eventData = new HashMap<>();
                eventData.put(EVENT_NAME, mEventName.getText().toString());
                eventData.put(EVENT_LOCATION, mEventLocation.getText().toString());
                eventData.put(EVENT_DESCRIPTION, mEventDescription.getText().toString());
                Date eventDate = new GregorianCalendar(
                        mDatePicker.getYear(),
                        mDatePicker.getMonth(),
                        mDatePicker.getDayOfMonth(),
                        mTimePicker.getHour(),
                        mTimePicker.getMinute()).getTime();
                eventData.put(EVENT_DATE, eventDate);
                eventData.put(OWNER, mCurrentUser.getDisplayName());

                mSchoolClubEvents.add(eventData)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CreateEventActivity.this, "Successful Write!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    if (task.getException() != null
                                            && task.getException().getMessage() != null) {
                                        Toast.makeText(CreateEventActivity.this,
                                                task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
            }
        });
    }
}
