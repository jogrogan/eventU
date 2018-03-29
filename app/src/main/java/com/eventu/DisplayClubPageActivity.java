package com.eventu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;

public class DisplayClubPageActivity extends AppCompatActivity {

    private EditText description;
    private EditText links;
    private EditText media;
    private EditText contact;
    private Button edit;
    private boolean edit_mode;
    private ClubPageInfo mClubPage;
    private DocumentReference doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_club_page);

        final Intent intent = getIntent();
        String school_name = intent.getStringExtra("school");
        final String club_id = intent.getStringExtra("club");
        String mPath = "universities/" + school_name + "/Club Profile Pages/" + club_id;
        doc = FirebaseFirestore.getInstance().document(mPath);
        doc.get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // Issue serializing the clubSocial ArrayList, the following Gson code is
                        // another way of converting the database data into a ClubPageInfo object
                        Gson gson = new Gson();
                        JsonElement jsonElement = gson.toJsonTree(documentSnapshot.getData());
                        mClubPage = gson.fromJson(jsonElement, ClubPageInfo.class);


                        TextView name = findViewById(R.id.display_club_name);
                        description = findViewById(R.id.display_club_description);
                        links = findViewById(R.id.display_club_links);
                        media = findViewById(R.id.display_club_media_links);
                        contact = findViewById(R.id.display_club_contact_info);
                        edit = findViewById(R.id.edit_button);

                        edit_mode = false;
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                edit_mode = !edit_mode;
                                editPage(view);
                            }
                        });

                        ArrayList<String> array = mClubPage.getClubSocial();
                        StringBuffer s = new StringBuffer();
                        for (int i = 0; i < array.size(); i++) {
                            s.append(array.get(i));
                            if (i != array.size() - 1) {
                                s.append('\n');
                            }
                        }

                        name.setText(mClubPage.getClubName());
                        description.setText(mClubPage.getClubDescription());
                        links.setText(mClubPage.getClubWebsite());
                        contact.setText(mClubPage.getClubContact());
                        media.setText(s);

                        String user_id = intent.getStringExtra("user");
                        if (user_id.equals(club_id)) {
                            edit.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }

    //toggle edit mode
    public void editPage(View view) {
        if (edit_mode) {
            description.setEnabled(true);
            links.setEnabled(true);
            media.setEnabled(true);
            contact.setEnabled(true);
            edit.setText(R.string.done);
        } else {
            description.setEnabled(false);
            links.setEnabled(false);
            media.setEnabled(false);
            contact.setEnabled(false);
            edit.setText(R.string.edit);

            mClubPage.setClubDescription(description.getText().toString());
            mClubPage.setClubWebsite(links.getText().toString());
            mClubPage.setClubSocial(media.getText().toString());
            mClubPage.setClubContact(contact.getText().toString());
            doc.set(mClubPage);
        }
    }
}
