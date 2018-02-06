package com.eventu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * A start page that offers two buttons to go to the login screen and the registration screen
 * and another one for resetting password in case you forgot yours
 */
public class StartPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        // Set up the login form.

        Button logInButton = findViewById(R.id.log_in_button);
        logInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartPageActivity.this, LoginActivity.class));
            }
        });
        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartPageActivity.this, SchoolSelectActivity.class));
            }
        });

        Button forgotpasswordButton = findViewById(R.id.forgot_password);
        forgotpasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartPageActivity.this, AccountRetrievalActivity.class));
            }
        });
    }
}

