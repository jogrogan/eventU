package com.eventu;

import android.support.v7.app.AppCompatActivity;

/**
 * Overrides the app action bar back button to function the same as the normal back button
 */
public class BaseClass extends AppCompatActivity {
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
