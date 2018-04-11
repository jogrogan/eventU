package com.eventu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows the user to create an event
 */
public class CreateEventActivity extends BaseClass {
    // Firebase References
    private final FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

    // UI References
    private EditText mEventName;
    private EditText mEventLocation;
    private EditText mEventDescription;
    private TimePicker mTimePicker;
    private DatePicker mDatePicker;
    private Button mImageButton;
    private Bitmap bmp;

    // Database References
    private CollectionReference mSchoolClubEvents;

    // Intent
    private Intent intent;

    // Other Event Information
    private String eventCreator;
    private int tally = 0;
    private String ClubID = "";
    private String eventID = "";

    private int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        setContentView(R.layout.activity_create_event);
        mEventName = findViewById(R.id.event_name);
        mEventDescription = findViewById(R.id.event_description);
        mEventLocation = findViewById(R.id.event_location);
        mTimePicker = findViewById(R.id.tp_timepicker);
        mDatePicker = findViewById(R.id.dp_datepicker);
        mDatePicker.setMinDate(System.currentTimeMillis());
        mImageButton = findViewById(R.id.select_image_button);
        FloatingActionButton nextButton = findViewById(R.id.next_button);

        if (mCurrentUser == null) {
            Log.d("NULL", "NULL user found.");
            return;
        }
        String mEventPath = "universities/" + mCurrentUser.getDisplayName() + "/Club Events";
        mSchoolClubEvents = FirebaseFirestore.getInstance().collection(mEventPath);

        attemptReadEvent();
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAddEvent();
            }
        });

        // On click of image button, open new activity to select an image from the phone's gallery
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
                } catch (Exception e) {
                    Log.i("Error", e.getMessage());
                }
            }
        });

    }

    /**
     * Reads image as a bitmap when image selection activity is finished.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            try {
                final InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                bmp = BitmapFactory.decodeStream(imageStream);
            } catch (IOException e) {
                Log.i("Error", e.getMessage());
            }
        } else {
            Toast.makeText(CreateEventActivity.this, "You haven't picked Image",
                    Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Attempt to read the event from the Firestore database if user is trying to modify the event.
     */
    private void attemptReadEvent() {
        if (!intent.hasExtra("eventID")) {
            return;
        }
        eventID = intent.getStringExtra("eventID");
        DocumentReference doc = mSchoolClubEvents.document(eventID);
        doc.get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        EventInfo eventInfo = documentSnapshot.toObject(EventInfo.class);
                        mEventName.setText(eventInfo.getEventName());
                        mEventLocation.setText(eventInfo.getEventLocation());
                        mEventDescription.setText(eventInfo.getEventDescription());
                        tally = eventInfo.getEventTally();
                        eventCreator = eventInfo.getEventCreator();
                        ClubID = eventInfo.getClubID();

                        Date selectedDate = eventInfo.getEventDate();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(selectedDate);
                        mTimePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                        mTimePicker.setMinute(calendar.get(Calendar.MINUTE));
                        mDatePicker.updateDate(calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
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

        if (intent.hasExtra("username")) {
            eventCreator = intent.getStringExtra("username");
        }

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
        eventData.put(getString(R.string.event_name), eventName);
        eventData.put(getString(R.string.event_location), eventLocation);
        eventData.put(getString(R.string.event_description), eventDescription);
        Date eventDate = new GregorianCalendar(
                mDatePicker.getYear(),
                mDatePicker.getMonth(),
                mDatePicker.getDayOfMonth(),
                mTimePicker.getHour(),
                mTimePicker.getMinute()).getTime();
        eventData.put(getString(R.string.event_date), eventDate);
        eventData.put(getString(R.string.event_creator), eventCreator);
        eventData.put(getString(R.string.event_tally), tally);

        if (ClubID.isEmpty()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                ClubID = user.getUid();
            }
        }
        eventData.put(getString(R.string.clubID), ClubID);

        if (eventID.isEmpty()) {
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
        } else {
            mSchoolClubEvents.document(eventID).set(eventData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CreateEventActivity.this, "Successful Write!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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