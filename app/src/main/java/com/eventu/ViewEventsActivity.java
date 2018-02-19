package com.eventu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ViewEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey("username")) {
            String username = extras.getString("username");
            Toast.makeText(ViewEventsActivity.this, "Welcome " + username + "!",
                    Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton createEvent = (FloatingActionButton) findViewById(
                R.id.event_creation_fab);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("yang", "Got here!");
                startActivity(new Intent(ViewEventsActivity.this, CreateEventActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}
