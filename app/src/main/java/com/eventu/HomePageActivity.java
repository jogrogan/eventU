package com.eventu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Collections;
import java.util.List;

/**
 * The Home page that is viewed immediately after logging in
 */
public class HomePageActivity extends AppCompatActivity {
    // Database References
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Current User's Information;
    private UserInfo mCurrentUser;

    // UI References
    private List<EventInfo> mEventInfoList;
    private ViewFlipper mViewFlipper;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EventInfoAdapter mEventAdapter;
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);

        // Display Welcome Message to Current User via toast
        // Current User Info coming from whatever activity that launched this one
        mCurrentUser = (UserInfo) getIntent().getSerializableExtra("UserInfo");
        String username = mCurrentUser.getUsername();
        Toast.makeText(HomePageActivity.this, "Welcome " + username + "!",
                Toast.LENGTH_SHORT).show();

        // Sets up the swipe to refresh feature
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

        // Sets up the action bar
        ActionBar mActionBar = getSupportActionBar();

        // Sets Up the ViewFlipper for toggling between calendar and timeline
        mViewFlipper = findViewById(R.id.eventViewFlipper);

        // Set up RecyclerView for Events
        RecyclerView mEventRecyclerView = findViewById(R.id.RecycleViewEvents);
        mEventRecyclerView.setHasFixedSize(true);
        mEventRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // The list of events in the timeline view
        mEventInfoList = new ArrayList<>();

        mEventAdapter = new EventInfoAdapter(this, mEventInfoList, mCurrentUser);
        mEventRecyclerView.setAdapter(mEventAdapter);

        // Sets up bottom navigation pane
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        if (!mCurrentUser.getClub()) {
            mBottomNavigationView.getMenu().removeItem(R.id.action_create_event);
        }
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
                            case R.id.action_create_event:
                                Intent intent = new Intent(HomePageActivity.this,
                                        CreateEventActivity.class);
                                intent.putExtra("username", mCurrentUser.getUsername());
                                startActivity(intent);
                                break;
                            case R.id.action_logout:
                                logout();
                                break;
                        }
                        return true;
                    }
                });


        // Updating the RecyclerView with real time updates from the club events database
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
                                EventInfo mEventInfo = dc.getDocument().toObject(
                                        EventInfo.class);
                                mEventInfo.setEventID(dc.getDocument().getId());
                                switch (dc.getType()) {
                                    case REMOVED:
                                        mEventInfoList.remove(mEventInfo);
                                        // If the event removed was a favorite of the user then
                                        // remove it
                                        if (mCurrentUser.removeFavorite(mEventInfo.getEventID())) {
                                            UserDatabaseUpdater.updateFavorites(mCurrentUser);
                                        }
                                        break;
                                    case ADDED:
                                        mEventInfoList.add(mEventInfo);
                                        break;
                                    case MODIFIED:
                                        int index = mEventInfoList.indexOf(mEventInfo);
                                        mEventInfoList.set(index, mEventInfo);
                                        break;
                                    default:
                                        break;
                                }

                                // Maintain sorting of events by date
                                Collections.sort(mEventInfoList);
                                mEventAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    /**
     * Always default back to the timeline format when resuming the HomePageActivity
     */
    @Override
    public void onResume() {
        super.onResume();
        mBottomNavigationView.setSelectedItemId(R.id.action_timeline);
    }

    /**
     * Inflate the menu with the icons and such via the action_bar_menu xml file
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    /**
     * Function that handles all the button clicks for the action bar
     */
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

    /**
     * Handles logging out of the eventU app
     */
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

    /**
     * Overriding back button
     * Do not want to be able to return to the log in page
     */
    @Override
    public void onBackPressed() {
    }

}
