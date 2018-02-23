package com.eventu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class HomePageActivity extends AppCompatActivity {

    //NO HARDCODING! Tags used in place of strings
    public static final String EVENT_NAME = "Event Name";
    public static final String EVENT_LOCATION = "Event Location";
    public static final String EVENT_DESCRIPTION = "Event Description";
    public static final String EVENT_DATE = "Event Date";

    //Database References
    private DocumentReference mDocRef = FirebaseFirestore.getInstance().document(
            "Club Events/Event");

    //UI References
    private FloatingActionButton mCreateEvent;
    private TextView mEventName;
    private TextView mEventLocation;
    private TextView mEventDescription;
    private TextView mEventDateandTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);
        mCreateEvent = findViewById(R.id.event_creation_fab);
        mEventName = findViewById(R.id.event_name_view);
        mEventLocation = findViewById(R.id.event_location_view);
        mEventDescription = findViewById(R.id.event_description_view);
        mEventDateandTime = findViewById(R.id.event_date_and_time_view);

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey("username")) {
            String username = extras.getString("username");
            Toast.makeText(HomePageActivity.this, "Welcome " + username + "!",
                    Toast.LENGTH_SHORT).show();
        }


        mCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePageActivity.this, CreateEventActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDocRef.addSnapshotListener(
                this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot,
                            FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()) {
                            mEventName.setText(documentSnapshot.getString(EVENT_NAME));
                            mEventLocation.setText(documentSnapshot.getString(EVENT_LOCATION));
                            mEventDescription.setText(
                                    documentSnapshot.getString(EVENT_DESCRIPTION));
                            mEventDateandTime.setText(
                                    documentSnapshot.getDate(EVENT_DATE).toString());
                        } else if (e != null) {
                            Toast.makeText(HomePageActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
    }
}
