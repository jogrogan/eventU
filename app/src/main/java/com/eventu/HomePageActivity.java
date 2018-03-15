package com.eventu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.eventu.login_and_registration.StartPageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * The Home page that is viewed immediately after logging in
 */
public class HomePageActivity extends AppCompatActivity {
    // Database References
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Current User's Information;
    private UserInfo mCurrentUser;

    // UI References
    private RecyclerView mEventRecyclerView;
    private List<EventInfo> mEventInfoList;
    private ViewFlipper mViewFlipper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);

        //Display Welcome Message to Current User via toast
        //Current User Info coming from whatever activity that launched this one
        mCurrentUser = (UserInfo) getIntent().getSerializableExtra("UserInfo");
        String username = mCurrentUser.getUsername();
        Toast.makeText(HomePageActivity.this, "Welcome " + username + "!",
                Toast.LENGTH_SHORT).show();

        //Sets Up the View Flipper for toggling between calendar and timeline
        mViewFlipper = findViewById(R.id.eventViewFlipper);

        //Set up Recylcer View for Events
        mEventRecyclerView = findViewById(R.id.RecycleViewEvents);
        mEventRecyclerView.setHasFixedSize(true);
        mEventRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //The list of events in the timeline view
        mEventInfoList = new ArrayList<>();


        //Set up onClick Listener - this one is for clicking the floating action button
        FloatingActionButton mCreateEvent = findViewById(R.id.event_creation_fab);
        mCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePageActivity.this, CreateEventActivity.class));
            }
        });

        //Sets up bottom navigation pane
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int displayNum;
                        switch (item.getItemId()) {
                            case R.id.action_timeline:
                                displayNum = mViewFlipper.indexOfChild(
                                        findViewById(R.id.homepage_timeline_view));
                                mViewFlipper.setDisplayedChild(displayNum);
                                break;
                            case R.id.action_calendar:
                                displayNum = mViewFlipper.indexOfChild(
                                        findViewById(R.id.homepage_calendar_view));
                                mViewFlipper.setDisplayedChild(displayNum);
                                break;
                            case R.id.action_logout:
                                logout();
                                break;
                        }
                        return true;
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        String mCampusEventPath = "/universities/" + mCurrentUser.getSchoolName() + "/Club Events/";
        db.collection(mCampusEventPath)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot QuerySnap,
                            @Nullable FirebaseFirestoreException e) {
                        if (e != null || QuerySnap == null) {
                            Toast.makeText(HomePageActivity.this, "Listen Failed",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (DocumentChange dc : QuerySnap.getDocumentChanges()) {
                            if (dc != null) {
                                switch (dc.getType()) {
                                    case REMOVED:
                                        break;
                                    case ADDED:
                                        EventInfo mEventInfo = dc.getDocument().toObject(
                                                EventInfo.class);
                                        mEventInfoList.add(mEventInfo);
                                    case MODIFIED:
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                });

        EventInfoAdapter mEventAdapter = new EventInfoAdapter(this, mEventInfoList);
        mEventRecyclerView.setAdapter(mEventAdapter);
    }

    /**
     * Do not want to be able to return to the log in page
     */
    @Override
    public void onBackPressed() {
    }

    private void logout() {
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
