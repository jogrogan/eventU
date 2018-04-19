package com.eventu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.ByteArrayOutputStream;
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
    private final int RESULT_LOAD_IMAGE = 1;
    // UI References
    private EditText mEventName;
    private EditText mEventLocation;
    private EditText mEventDescription;
    private TimePicker mTimePicker;
    private DatePicker mDatePicker;
    private Button mImageButton;
    private ImageView mEventImage;

    // Database References
    private CollectionReference mSchoolClubEvents;
    private String mEventPath;

    // Intent
    private Intent intent;
    private StorageReference mStorageReference;
    private String eventCreator;
    private int tally = 0;
    private String ClubID = "";
    private String eventID = "";
    // Other Event Information
    private Map<String, Object> eventData;
    // Keep track if image was changed to determine if we should reload the image on the HomePage
    private boolean imageChanged = false;

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
        mEventImage = findViewById(R.id.event_image);
        FloatingActionButton nextButton = findViewById(R.id.next_button);

        if (mCurrentUser == null) {
            Log.d("NULL", "NULL user found.");
            return;
        }
        mEventPath = "universities/" + mCurrentUser.getDisplayName() + "/Club Events";
        mSchoolClubEvents = FirebaseFirestore.getInstance().collection(mEventPath);
        mStorageReference = FirebaseStorage.getInstance().getReference();

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
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
                } catch (Exception e) {
                    Log.i("Error", e.getMessage());
                }
            }
        });

    }

    /**
     * Reads image as a bitmap and populates the ImageView when image selection activity is
     * finished.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                    mEventImage.setImageBitmap(bmp);
                    imageChanged = true;
                }
            } catch (IOException e) {
                Log.i("Error", e.getMessage());
            }
        }
    }

    /**
     * Attempt to read the event from the Firestore database if user is trying to modify the event.
     */
    private void attemptReadEvent() {
        if (!intent.hasExtra("eventID")) {
            mEventImage.setImageResource(R.drawable.eventu_logo);
            return;
        }
        eventID = intent.getStringExtra("eventID");

        // Handles reading the image used
        StorageReference imageStorage = mStorageReference.child(mEventPath + "/" + eventID);
        imageStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(getApplicationContext()).load(imageURL).into(mEventImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                mEventImage.setImageResource(R.drawable.eventu_logo);
                Log.i("Error", "Failed to get image");
            }
        });

        // Handles reading the event information
        DocumentReference doc = mSchoolClubEvents.document(eventID);
        doc.get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        EventInfo eventInfo = documentSnapshot.toObject(EventInfo.class);
                        if (eventInfo == null) {
                            return;
                        }
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

        // Error checking
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

        // Stores the event data to be written to the Firestore database
        eventData = new HashMap<>();
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

        // If this is a new event
        if (eventID.isEmpty()) {
            mSchoolClubEvents.add(eventData)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                eventID = task.getResult().getId();
                                Toast.makeText(CreateEventActivity.this, "Successful Write!",
                                        Toast.LENGTH_SHORT).show();
                                uploadImage();
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
                                finish();
                            }
                        }
                    });

        }
        // If the user is modifying this event
        else {
            mSchoolClubEvents.document(eventID).set(eventData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CreateEventActivity.this, "Successful Edit!",
                                    Toast.LENGTH_SHORT).show();

                            // The Intent to return back to the homepage to handle loading image
                            // changes if necessary
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("imageChanged", imageChanged);
                            if (imageChanged) {
                                Gson gson = new Gson();
                                JsonElement data = gson.toJsonTree(eventData);
                                EventInfo eventInfo = gson.fromJson(data, EventInfo.class);
                                eventInfo.setEventID(eventID);
                                returnIntent.putExtra("eventInfo", eventInfo);
                            }
                            setResult(Activity.RESULT_OK, returnIntent);
                            uploadImage();
                        }
                    });
        }
    }

    /**
     * Attempts to upload the mImageView image to the FireStorage database.
     */
    private void uploadImage() {
        try {
            StorageReference imageStorage = mStorageReference.child(mEventPath + "/" + eventID);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap bmp = ((BitmapDrawable) mEventImage.getDrawable()).getBitmap();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            UploadTask uploadTask = imageStorage.putBytes(outputStream.toByteArray());
            uploadTask.addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("Image ", "Storage Success");
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Image", "Storage Failed");
                    finish();
                }
            });
        } catch (Exception e) {
            Log.d("Image", "Upload Failed: " + e.getMessage());
            finish();
        }
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