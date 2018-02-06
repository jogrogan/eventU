package com.eventu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class AccountRetrievalActivity extends AppCompatActivity {
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
        String emailAddress = mEmailView.getText().toString();
        mFirebaseAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("yang", "Email sent.");
                        }
                    }
                });
    }
}
