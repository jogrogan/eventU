package com.eventu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * The Home page that is viewed immediately after logging in
 */
public class HomePageActivity extends AppCompatActivity {

    // NO HARDCODING! Tags used in place of strings
    private static final String EVENT_NAME = "Event Name";
    private static final String EVENT_LOCATION = "Event Location";
    private static final String EVENT_DESCRIPTION = "Event Description";
    private static final String EVENT_DATE = "Event Date";

    // Database References
    private final DocumentReference mDocRef = FirebaseFirestore.getInstance().document(
            "Club Events/Event");

    // UI References
    private TextView mEventName;
    private TextView mEventLocation;
    private TextView mEventDescription;
    private TextView mEventDateAndTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        FloatingActionButton mCreateEvent = findViewById(R.id.event_creation_fab);
        mEventName = findViewById(R.id.event_name_view);
        mEventLocation = findViewById(R.id.event_location_view);
        mEventDescription = findViewById(R.id.event_description_view);
        mEventDateAndTime = findViewById(R.id.event_date_and_time_view);

        UserInfo info = (UserInfo) getIntent().getSerializableExtra("UserInfo");

        String username = info.getUsername();
        Toast.makeText(HomePageActivity.this, "Welcome " + username + "!",
                Toast.LENGTH_SHORT).show();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_timeline:
                                break;
                            case R.id.action_calendar:
                                break;
                            case R.id.action_logout:
                                logout();
                                break;
                        }
                        return true;
                    }
                });


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
                            mEventDateAndTime.setText(
                                    documentSnapshot.getDate(EVENT_DATE).toString());
                        } else if (e != null) {
                            Toast.makeText(HomePageActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Do not want to be able to return to the log in page
     */
    @Override
    public void onBackPressed() {
    }

    public void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(R.string.menu_logout);
        builder.setMessage(R.string.logout_confirmation);
        builder.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomePageActivity.this, StartPageActivity.class));
                        finish();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
