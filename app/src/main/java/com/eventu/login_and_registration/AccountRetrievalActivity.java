package com.eventu.login_and_registration;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.eventu.BaseClass;
import com.eventu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

/**
 * Allows the user to retrieve their account if they forgot their password
 */
public class AccountRetrievalActivity extends BaseClass {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private View mFocusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_retrieval);

        mEmailView = findViewById(R.id.password_retrieval);
        Button resetPassword = findViewById(R.id.reset_password);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPasswordResetEmail();
            }
        });
    }

    /**
     * Handles sending password reset email.
     * Includes valid email verification.
     */
    private void sendPasswordResetEmail() {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        final String emailAddress = mEmailView.getText().toString();
        mFocusView = null;

        if (emailAddress.isEmpty()) {
            mEmailView.setError(getString(R.string.error_field_required));
            mFocusView = mEmailView;
        }

        if (mFocusView != null) {
            mFocusView.requestFocus();
            return;
        }

        mFirebaseAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(AccountRetrievalActivity.this,
                                    "Email Sent to " + emailAddress + "!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                if (task.getException() != null) {
                                    throw task.getException();
                                }
                            } catch (FirebaseAuthInvalidUserException invalidEmail) {
                                mEmailView.setError(getString(R.string.error_email_not_exists));
                                mFocusView = mEmailView;
                            } catch (Exception e) {
                                mEmailView.setError(getString(R.string.error_reset_password));
                                mFocusView = mEmailView;
                            }
                            if (mFocusView != null) {
                                mFocusView.requestFocus();
                            }
                        }
                    }
                });
    }
}
