package com.eventu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    }
}
