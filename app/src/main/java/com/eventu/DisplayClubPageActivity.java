package com.eventu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.eventu.login_and_registration.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Displays the club profile page of the club ID included in the Intent.
 * Allows the club user who owns this page to edit it.
 */
public class DisplayClubPageActivity extends AppCompatActivity {

    // UI References
    private EditText mClubDescription;
    private EditText mClubWebsite;
    private EditText mClubMediaLinks;
    private EditText mClubContactInfo;
    private Button mEditButton;

    private ClubPageInfo mClubPage;
    private DocumentReference doc;
    private boolean edit_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_club_page);

        final Intent intent = getIntent();
        String school_name = intent.getStringExtra("school");
        final String club_id = intent.getStringExtra("club");

        // Reads the page from the firebase database
        String mPath = "universities/" + school_name + "/Club Profile Pages/" + club_id;
        doc = FirebaseFirestore.getInstance().document(mPath);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        mClubPage = doc.toObject(ClubPageInfo.class);
                        TextView name = findViewById(R.id.display_club_name);
                        mClubDescription = findViewById(R.id.display_club_description);
                        mClubWebsite = findViewById(R.id.display_club_links);
                        mClubMediaLinks = findViewById(R.id.display_club_media_links);
                        mClubContactInfo = findViewById(R.id.display_club_contact_info);
                        mEditButton = findViewById(R.id.edit_button);

                        ArrayList<String> array = mClubPage.getClubSocial();
                        StringBuffer media = new StringBuffer();
                        for (int i = 0; i < array.size(); i++) {
                            media.append(array.get(i));
                            if (i != array.size() - 1) {
                                media.append('\n');
                            }
                        }

                        name.setText(mClubPage.getClubName());
                        mClubDescription.setText(mClubPage.getClubDescription());
                        mClubWebsite.setText(mClubPage.getClubWebsite());
                        mClubMediaLinks.setText(media);
                        mClubContactInfo.setText(mClubPage.getClubContact());

                        if (intent.hasExtra("createPage")) {
                            createPage();
                        } else {
                            String user_id = intent.getStringExtra("user");
                            edit_mode = false;
                            if (user_id.equals(club_id)) {
                                mEditButton.setVisibility(View.VISIBLE);
                                mEditButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        edit_mode = !edit_mode;
                                        editPage();
                                    }
                                });
                            }
                        }
                    } else {
                        Log.d("DisplayClubPageError", "Doc does not exist");
                    }
                } else {
                    Log.d("DisplayClubPageError", "Task failed");
                }
            }
        });
    }

    /**
     * Provides error checking for club page.
     */
    private boolean clubPageErrorChecking() {
        String clubDescription = mClubDescription.getText().toString();
        View focusView = null;
        if (clubDescription.isEmpty()) {
            mClubDescription.setError(getString(R.string.error_field_required));
            focusView = mClubDescription;
        }

        if (focusView != null) {
            focusView.requestFocus();
            return true;
        }
        return false;
    }

    /**
     * Sets all fields as editable
     */
    private void enableEdits() {
        mClubDescription.setEnabled(true);
        mClubWebsite.setEnabled(true);
        mClubMediaLinks.setEnabled(true);
        mClubContactInfo.setEnabled(true);
    }

    /**
     * Disables all fields from being editable
     */
    private void disableEdits() {
        mClubDescription.setEnabled(false);
        mClubWebsite.setEnabled(false);
        mClubMediaLinks.setEnabled(false);
        mClubContactInfo.setEnabled(false);
    }

    /**
     * Sets ClubPageInfo fields based on text boxes
     */
    private void setClubPageInfo() {
        mClubPage.setClubDescription(mClubDescription.getText().toString());
        mClubPage.setClubWebsite(mClubWebsite.getText().toString());
        String s = mClubMediaLinks.getText().toString();
        ArrayList<String> newList = new ArrayList<>(Arrays.asList(s.split("\n")));
        mClubPage.setClubSocial(newList);
        mClubPage.setClubContact(mClubContactInfo.getText().toString());
    }

    /**
     * Called after registration when the club makes their club page.
     */
    private void createPage() {
        mEditButton.setText(R.string.done);
        enableEdits();

        mEditButton.setVisibility(View.VISIBLE);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clubPageErrorChecking()) {
                    return;
                }
                setClubPageInfo();
                doc.set(mClubPage).addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                disableEdits();
                                startActivity(new Intent(DisplayClubPageActivity.this,
                                        LoginActivity.class));
                                finish();
                            }
                        });
            }
        });
    }

    /**
     * Called when the club who owns this page clicks the "Edit" Button on their page.
     */
    private void editPage() {
        if (edit_mode) {
            enableEdits();
            mEditButton.setText(R.string.done);
        } else {
            if (clubPageErrorChecking()) {
                edit_mode = !edit_mode;
                return;
            }
            disableEdits();
            mEditButton.setText(R.string.edit);
            setClubPageInfo();
            doc.set(mClubPage);
        }
    }

    /**
     * Back button should have no functionality if from registration sequence because profile
     * page creation is required.
     */
    @Override
    public void onBackPressed() {
        if (!getIntent().getBooleanExtra("fromRegistration", false)) {
            super.onBackPressed();
        }
    }
}