package com.eventu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Homepage that is viewed immediately after logging in
 */
public class HomePageActivity extends AppCompatActivity {
    static final int RESULT_IMAGE_CHANGE = 1;

    // Database References
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Current User's Information;
    private UserInfo mCurrentUser;
    private String username;
    private SharedPreferences mSharedPref;

    // Adapter References
    private List<EventInfo> mEventInfoList;
    private Map<String, EventInfo> mCalendarEvents;
    private EventInfoAdapter mEventAdapter;

    // Other UI References
    private ViewFlipper mViewFlipper;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BottomNavigationView mBottomNavigationView;
    private CalendarView mCalendarView;
    private LinearLayout mContainerLayout;
    private RelativeLayout mMainLayout;
    private View mPopupView;
    private PopupWindow mPopupWindow;
    private RecyclerView mCalendarPopUpRecyclerView;

    // Other
    private Context self;
    private boolean isTimelineSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isTimelineSelected = true;
        self = this;

        setContentView(R.layout.activity_homepage);

        // Display Welcome Message to Current User via toast
        mCurrentUser = (UserInfo) getIntent().getSerializableExtra("UserInfo");
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = mSharedPref.getString("example_text", mCurrentUser.getUsername());
        Toast.makeText(HomePageActivity.this, "Welcome " + mCurrentUser.getUsername() + "!",
                Toast.LENGTH_SHORT).show();

        // Sets up the swipe to refresh feature
        mSwipeRefreshLayout = findViewById(R.id.eventSwipeRefreshView);
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

        // Sets Up the ViewFlipper for toggling between calendar and timeline
        mViewFlipper = findViewById(R.id.eventViewFlipper);

        // Set up RecyclerView for Events
        RecyclerView mEventRecyclerView = findViewById(R.id.eventRecyclerView);
        mEventRecyclerView.setHasFixedSize(true);
        mEventRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // The list of events in the timeline view
        mEventInfoList = new ArrayList<>();
        mCalendarEvents = new HashMap<>();

        mEventAdapter = new EventInfoAdapter(this, this, mEventInfoList, mCurrentUser);
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
                                isTimelineSelected = true;
                                break;
                            case R.id.action_calendar:
                                displayNum = mViewFlipper.indexOfChild(
                                        findViewById(R.id.homepage_calendar_view));
                                mViewFlipper.setDisplayedChild(displayNum);
                                isTimelineSelected = false;
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

        // Sets up Calendar View and necessary UI References to handle popup windows on date select
        mMainLayout = findViewById(R.id.homePageMainView);
        mPopupView = LayoutInflater.from(this).inflate(R.layout.calendar_popup_view, mMainLayout,
                false);
        mPopupWindow = new PopupWindow(mPopupView, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        mCalendarPopUpRecyclerView = mPopupView.findViewById(R.id.eventRecyclerView);
        mCalendarPopUpRecyclerView.setHasFixedSize(true);
        mCalendarPopUpRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mContainerLayout = new LinearLayout(this);
        mContainerLayout.setOrientation(LinearLayout.VERTICAL);

        mCalendarView = findViewById(R.id.CalendarView);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month,
                    int dayOfMonth) {
                Calendar selectedDate = new GregorianCalendar(year, month, dayOfMonth);
                Calendar eventDate = Calendar.getInstance();
                mCalendarEvents.clear();
                for (EventInfo info : mEventInfoList) {
                    eventDate.setTime(info.getEventDate());
                    if (selectedDate.get(Calendar.YEAR) == eventDate.get(Calendar.YEAR) &&
                            selectedDate.get(Calendar.DAY_OF_YEAR) == eventDate.get(
                                    Calendar.DAY_OF_YEAR)) {
                        mCalendarEvents.put(info.getEventID(), info);
                    }
                }

                if (setCalendarAdapter()) {
                    mPopupWindow.showAtLocation(mPopupView, Gravity.CENTER_VERTICAL, 0, 0);
                    mPopupWindow.update(0, 0, mMainLayout.getWidth(), mCalendarView.getHeight());
                    mPopupWindow.setContentView(mContainerLayout);
                }
            }
        });

        // Updating the RecyclerViews with real time updates from the club events database
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
                                EventInfo mEventInfo = dc.getDocument().toObject(EventInfo.class);
                                mEventInfo.setEventID(dc.getDocument().getId());

                                boolean calendarCheck = false;
                                if (!isTimelineSelected && mCalendarEvents.containsKey(
                                        mEventInfo.getEventID())) {
                                    calendarCheck = true;
                                }

                                Date currentDate = Calendar.getInstance().getTime();
                                if (currentDate.after(mEventInfo.getEventDate())) {
                                    continue;
                                }

                                switch (dc.getType()) {
                                    case REMOVED:
                                        if (calendarCheck) {
                                            mCalendarEvents.remove(mEventInfo.getEventID());
                                            setCalendarAdapter();
                                        }
                                        mEventInfoList.remove(mEventInfo);
                                        mCurrentUser.removeFavorite(mEventInfo.getEventID());
                                        break;
                                    case ADDED:
                                        mEventInfoList.add(mEventInfo);
                                        break;
                                    case MODIFIED:
                                        // If in calendar mode, check to see the date wasn't changed
                                        if (calendarCheck) {
                                            Calendar eventDate = Calendar.getInstance();
                                            Calendar eventBeforeChange = Calendar.getInstance();
                                            eventDate.setTime(mEventInfo.getEventDate());
                                            eventBeforeChange.setTime(mCalendarEvents.get(
                                                    mEventInfo.getEventID()).getEventDate());
                                            if (eventBeforeChange.get(Calendar.YEAR)
                                                    == eventDate.get(Calendar.YEAR) &&
                                                    eventBeforeChange.get(Calendar.DAY_OF_YEAR)
                                                            == eventDate.get(
                                                            Calendar.DAY_OF_YEAR)) {
                                                mCalendarEvents.put(mEventInfo.getEventID(),
                                                        mEventInfo);
                                            } else {
                                                mCalendarEvents.remove(mEventInfo.getEventID());
                                            }
                                            setCalendarAdapter();
                                        }
                                        int index = mEventInfoList.indexOf(mEventInfo);
                                        if (mEventInfoList.get(index).getEventDate().compareTo(
                                                mEventInfo.getEventDate()) == 0) {
                                            mEventAdapter.notifyItemChanged(index, mEventInfo);
                                            continue;
                                        }
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
     * If an image was changed then update the adapter
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_IMAGE_CHANGE && resultCode == RESULT_OK && data != null) {
            boolean imageChanged = data.getBooleanExtra("imageChanged", false);
            if (imageChanged) {
                EventInfo eventInfo = (EventInfo) data.getSerializableExtra("eventInfo");
                int index = mEventInfoList.indexOf(eventInfo);
                mEventInfoList.set(index, eventInfo);
                mEventAdapter.notifyDataSetChanged();
            }
        } else {
            Log.d("Activity Result", "No image change made");
        }
    }

    /**
     * Helper function to take the events on a given calendar day and store them into the
     * RecyclerView
     */
    private boolean setCalendarAdapter() {
        if (mCalendarEvents.isEmpty()) {
            mPopupWindow.dismiss();
            Toast.makeText(self, "No events on this day", Toast.LENGTH_SHORT).show();
            return false;
        }
        List<EventInfo> eventsOnDay = new ArrayList<>(mCalendarEvents.values());
        Collections.sort(eventsOnDay);
        EventInfoAdapter adapter = new EventInfoAdapter(this, this, eventsOnDay, mCurrentUser);
        mCalendarPopUpRecyclerView.setAdapter(adapter);
        return true;
    }

    /**
     * Always default back to the timeline format when resuming the HomePageActivity
     */
    @Override
    public void onResume() {
        super.onResume();
        username = mSharedPref.getString("example_text", mCurrentUser.getUsername());
        if (isTimelineSelected) {
            mBottomNavigationView.setSelectedItemId(R.id.action_timeline);
        } else {
            mBottomNavigationView.setSelectedItemId(R.id.action_calendar);
        }
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
                startActivity(new Intent(HomePageActivity.this, SettingsActivity.class));
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
                if (isTimelineSelected) {
                    mBottomNavigationView.setSelectedItemId(R.id.action_timeline);
                } else {
                    mBottomNavigationView.setSelectedItemId(R.id.action_calendar);
                }
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
