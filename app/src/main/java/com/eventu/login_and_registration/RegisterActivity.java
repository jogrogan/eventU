package com.eventu.login_and_registration;

import static android.Manifest.permission.READ_CONTACTS;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eventu.BaseClass;
import com.eventu.ClubPageInfo;
import com.eventu.DisplayClubPageActivity;
import com.eventu.R;
import com.eventu.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A register screen that offers registering via email/password.
 */
public class RegisterActivity extends BaseClass implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    // User Information
    private boolean isClub;

    // UI references
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mRegisterFormView;
    private View mFocusView;
    private EditText mNameView;

    // Firebase References
    private FirebaseAuth mFirebaseAuth;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();

        intent = getIntent();
        isClub = intent.getBooleanExtra("isClub", false);
        if (isClub) {
            TextInputLayout til = findViewById(R.id.name_widget);
            til.setHint(getResources().getString(R.string.prompt_club_name));
            TextView tv = new TextView(this);
            til.addView(tv);
        }

        // Set up the registration form.
        mEmailView = findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = findViewById(R.id.password);
        mNameView = findViewById(R.id.name);
        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    /**
     * Attempts to register the account specified by the register form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual registration attempt is made.
     */
    private void attemptRegister() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the registration attempt.
        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        final String name = mNameView.getText().toString();

        mFocusView = null;

        // Check for a valid name
        if (name.isEmpty()) {
            mNameView.setError(getString(R.string.error_field_required));
            mFocusView = mNameView;
        }

        // Check for a valid password.
        if (password.isEmpty()) {
            mPasswordView.setError(getString(R.string.error_field_required));
            mFocusView = mPasswordView;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_password_short));
            mFocusView = mPasswordView;
        }

        // Check for a valid email address.
        if (email.isEmpty()) {
            mEmailView.setError(getString(R.string.error_field_required));
            mFocusView = mEmailView;
        } else if (!isEmailValid(email)) {
            mFocusView = mEmailView;
        }

        if (mFocusView != null) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            mFocusView.requestFocus();
        } else {
            // Show a progress spinner, and perform the user registration attempt.
            showProgress(true);
            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Registration success
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                if (user == null) {
                                    showProgress(false);
                                    // If registration fails, display a message to the user.
                                    mEmailView.setError(getString(R.string.error_register_failed));
                                    mFocusView = mEmailView;
                                    mFocusView.requestFocus();
                                } else {
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RegisterActivity.this,
                                                                "Verification email sent",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                    final String schoolName = intent.getStringExtra("schoolName");
                                    final String userID = user.getUid();
                                    UserProfileChangeRequest profileUpdates
                                            = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(schoolName)
                                            .build();
                                    user.updateProfile(profileUpdates);

                                    isClub = intent.getBooleanExtra("isClub", false);

                                    UserInfo userInfo = new UserInfo(email, new ArrayList<String>(),
                                            name, schoolName, userID, isClub);

                                    FirebaseFirestore.getInstance().collection("universities")
                                            .document(schoolName).collection("Users")
                                            .document(userID).set(userInfo)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("Firestore",
                                                            "Document successfully added");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("Firestore", "Error writing document", e);
                                                }
                                            });

                                    // If account is a club create a corresponding club page and
                                    // switch to that page to edit it
                                    if (isClub) {
                                        ClubPageInfo clubPage = new ClubPageInfo(name, userID);
                                        FirebaseFirestore.getInstance().collection("universities")
                                                .document(schoolName).collection(
                                                "Club Profile Pages")
                                                .document(userID).set(clubPage)
                                                .addOnSuccessListener(
                                                        new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Intent newClubPage = new Intent(
                                                                        RegisterActivity.this,
                                                                        DisplayClubPageActivity
                                                                                .class);
                                                                newClubPage.putExtra("school",
                                                                        schoolName);
                                                                newClubPage.putExtra("club",
                                                                        userID);
                                                                newClubPage.putExtra("createPage",
                                                                        true);
                                                                newClubPage.putExtra(
                                                                        "fromRegistration",
                                                                        true);
                                                                startActivity(newClubPage);
                                                            }
                                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("Firestore", "Error adding club", e);
                                                    }
                                                });
                                    } else {
                                        startActivity(new Intent(RegisterActivity.this,
                                                LoginActivity.class));
                                    }
                                }
                            } else {
                                showProgress(false);
                                try {
                                    if (task.getException() != null) {
                                        throw task.getException();
                                    }
                                }
                                // If the users email already exists
                                catch (FirebaseAuthUserCollisionException existEmail) {
                                    mEmailView.setError(getString(R.string.error_email_exists));
                                    mFocusView = mEmailView;
                                } catch (Exception e) {
                                    mEmailView.setError(getString(R.string.error_register_failed));
                                    mFocusView = mEmailView;
                                }
                                mFocusView.requestFocus();
                            }
                        }
                    });
        }
    }

    /**
     * Clubs can have any valid email.
     * If registering as a student, the given email must match the domain of the school
     * chosen in the SchoolSelectActivity.
     * Displays email form errors
     */
    private boolean isEmailValid(String email) {
        Pattern emailPattern;
        isClub = intent.getBooleanExtra("isClub", false);
        ArrayList<String> domains = intent.getStringArrayListExtra("schoolDomains");
        if (isClub) {
            emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                    Pattern.CASE_INSENSITIVE);
        } else {
            boolean matches = false;
            String emailDomain = "";
            int indexDomain = email.indexOf('@');
            if (indexDomain >= 0 && indexDomain + 1 < email.length()) {
                emailDomain = email.substring(indexDomain + 1);
            }
            for (int i = 0; i < domains.size(); i++) {
                if (emailDomain.equals(domains.get(i))) {
                    matches = true;
                    break;
                }
            }
            if (!matches) {
                mEmailView.setError(getString(R.string.error_no_email_domain_match));
                return false;
            }
            emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.edu$",
                    Pattern.CASE_INSENSITIVE);
        }
        Matcher emailMatcher = emailPattern.matcher(email);
        if (!emailMatcher.find()) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the registration form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Handles field autocompletion
     */
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Request contacts needed in order for email autocompletion
     */
    private boolean mayRequestContacts() {
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Load email autocomplete data
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    /**
     * Stores email to be used by future autocompletion
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    /**
     * Adds list of autocomplete email suggestions
     */
    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Object used for requesting contacts
     */
    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }
}

