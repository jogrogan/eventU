package com.eventu.login_and_registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.eventu.BaseClass;
import com.eventu.R;
import com.eventu.login_and_registration.school_selection.SchoolSelectActivity;

/**
 * A start page that offers two buttons to go to the login screen and the registration screen
 */
public class StartPageActivity extends BaseClass {
    private Button logInButton;
    private Button registerButton;
    private ImageView eventu_logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        eventu_logo = findViewById(R.id.eventu_logo);
        eventu_logo.setImageResource(R.drawable.eventu_logo);

        logInButton = findViewById(R.id.log_in_button);
        logInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartPageActivity.this, LoginActivity.class));
            }
        });
        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartPageActivity.this, SchoolSelectActivity.class));
            }
        });
    }

    /**
     * Back button should have no functionality as this is the starting page
     */
    @Override
    public void onBackPressed() {
    }
}

