package com.eventu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Allows the user to retrieve their account if they forgot their password
 */
public class AccountRetrievalActivity extends BaseClass {

    // UI references.
    private AutoCompleteTextView mEmailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_retrieval);

        mEmailView = findViewById(R.id.password_retrival);
        Button resetPassword = findViewById(R.id.reset_password);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPasswordResetEmail();
            }
        });
    }

    private void sendPasswordResetEmail() {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        final String emailAddress = mEmailView.getText().toString();
        mFirebaseAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AccountRetrievalActivity.this,
                                "Email Sent to " + emailAddress + "!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
