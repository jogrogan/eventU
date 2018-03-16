package com.eventu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
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
public class HomePageActivity extends BaseClass {
    // Database References
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Current User's Information;
    private UserInfo mCurrentUser;

    // UI References
    private RecyclerView mEventRecyclerView;
    private List<EventInfo> mEventInfoList;
    private ViewFlipper mViewFlipper;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BottomNavigationView mBottomNavigationView;
    private ActionBar mActionBar;

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

        //Sets up the swipe to refresh feature
        mSwipeRefreshLayout = findViewById(R.id.eventRefreshView);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //TODO Add stuff to refresh on swipe
                        Toast.makeText(HomePageActivity.this, "Refreshing!",
                                Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );


        //Sets up the action bar
        mActionBar = getSupportActionBar();

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
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(
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


        //Updating the Recycler View
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

    //Inflate the menu with the icons and such via the action_bar_menu xml file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    //Function that handles all the button clicks for the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_refresh_events:
                //TODO Refresh Menu
                Toast.makeText(HomePageActivity.this, "Refreshing Not Implemented",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_bar_userSettings:
                //TODO Settings Page
                Toast.makeText(HomePageActivity.this, "Settings Page Not Implemented",
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
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
