package com.eventu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
    private EventAdapter mEventAdapter;
    private List<EventInfo> mEventInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize Views and Content
        setContentView(R.layout.activity_homepage);
        FloatingActionButton mCreateEvent = findViewById(R.id.event_creation_fab);
        mEventRecyclerView = findViewById(R.id.RecycleViewEvents);
        mEventRecyclerView.setHasFixedSize(true);
        mEventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mEventInfoList = new ArrayList<>();
        mCurrentUser = (UserInfo) getIntent().getSerializableExtra("UserInfo");
        //Set up onClick Listener - this one is for clicking the floating action button
        mCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePageActivity.this, CreateEventActivity.class));
            }
        });

        //Display Welcome Message to Current User via toast
        String username = mCurrentUser.getUsername();
        Toast.makeText(HomePageActivity.this, "Welcome " + username + "!",
                Toast.LENGTH_SHORT).show();
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
                        if (e != null) {
                            Toast.makeText(HomePageActivity.this, "Listen Failed",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (DocumentChange dc : QuerySnap.getDocumentChanges()) {
                            if (dc != null) {
                                switch (dc.getType()) {
                                    case REMOVED:
                                        Log.d("yang", "Document Removed");
                                        break;
                                    case ADDED:
                                        EventInfo mEventInfo = dc.getDocument().toObject(
                                                EventInfo.class);
                                        mEventInfoList.add(mEventInfo);
                                        Log.d("yang", "Document Added" + dc.getDocument().getString(
                                                "EventName"));
                                    case MODIFIED:
                                        break;
                                    default:
                                        Log.d("yang", "no cases matched");
                                        break;
                                }
                            }
                        }
                    }
                });

        mEventAdapter = new EventAdapter(this, mEventInfoList);
        mEventRecyclerView.setAdapter(mEventAdapter);
    }

    /**
     * Do not want to be able to return to the log in page
     */
    @Override
    public void onBackPressed() {
    }
}
