package com.eventu.login_and_registration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.eventu.BaseClass;
import com.eventu.HomePageActivity;
import com.eventu.R;
import com.eventu.UserInfo;
import com.eventu.login_and_registration.school_selection.SchoolSelectActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A start page that offers two buttons to go to the login screen and the registration screen
 */
public class StartPageActivity extends BaseClass {
    private Button logInButton;
    private Button registerButton;
    private ImageView eventu_logo;

    private FirebaseUser mFirebaseUser;
    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // If the user exists and has specified that this device remembers their password, log in
        // immediately
        if (mFirebaseUser != null && mFirebaseUser.getDisplayName() != null &&
                mSharedPref.getBoolean(getString(R.string.RememberAccess), false)) {
            DocumentReference doc
                    = FirebaseFirestore.getInstance().collection(
                    "universities")
                    .document(mFirebaseUser.getDisplayName()).collection("Users")
                    .document(mFirebaseUser.getUid());

            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot mUserDoc = task.getResult();
                        UserInfo mUser = mUserDoc.toObject(UserInfo.class);
                        Intent HomePageIntent = new Intent(StartPageActivity.this,
                                HomePageActivity.class);
                        HomePageIntent.putExtra("UserInfo", mUser);
                        startActivity(HomePageIntent);
                    } else {
                        Log.d("StartPageError", "Cannot access user document");
                    }
                }
            });
        }

        //New User or user has requested not to remember password normal start up
        else {
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
                    startActivity(
                            new Intent(StartPageActivity.this, SchoolSelectActivity.class));
                }
            });
        }
    }

    /**
     * Back button should have no functionality as this is the starting page
     */
    @Override
    public void onBackPressed() {
    }
}