package com.eventu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.eventu.login_and_registration.LoginActivity;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DisplayClubPage extends AppCompatActivity {

    EditText description;
    EditText links;
    EditText media;
    EditText contact;
    Button edit;
    boolean edit_mode;
    ClubPage mClubPage;
    DocumentReference doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_club_page);

        Intent intent = getIntent();
        String school_name = intent.getStringExtra("school");
        String club_name = intent.getStringExtra("club");

         doc
                = FirebaseFirestore.getInstance().collection(
                "universities")
                .document(school_name).collection("Club Profile Pages")
                .document(club_name);
        doc.get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(
                            DocumentSnapshot documentSnapshot) {
                        mClubPage = documentSnapshot.toObject(ClubPage.class);
                    }
                });

        TextView name = findViewById(R.id.display_club_name);
        description = findViewById(R.id.display_club_description);
        links = findViewById(R.id.display_club_links);
        media = findViewById(R.id.display_club_media_links);
        contact = findViewById(R.id.display_club_contact_info);
        edit = findViewById(R.id.edit_button);

        ArrayList<String> array = mClubPage.getSocial();
        String s = "";
        for (int i = 0; i < array.size(); i++){
            s += array.get(i);
            s += "\n";
        }

        name.setText(mClubPage.getName());
        description.setText(mClubPage.getDescription());
        links.setText(mClubPage.getWebsite());
        contact.setText(mClubPage.getContact());
        media.setText(s);


        String user_id = intent.getStringExtra("user");
        String club_id = mClubPage.getId();
        if (user_id.equals(club_id)){
            edit.setVisibility(View.VISIBLE);
        }
        edit_mode = false;
    }

    //toggle edit mode
    public void editPage(View view){
        if (!edit_mode){
            description.setEnabled(true);
            links.setEnabled(true);
            media.setEnabled(true);
            contact.setEnabled(true);
            edit.setText("Done");
        }
        else{
            description.setEnabled(false);
            links.setEnabled(false);
            media.setEnabled(false);
            contact.setEnabled(false);
            edit.setText("Edit");

            mClubPage.setDescription(description.getText().toString());
            mClubPage.setWebsite(links.getText().toString());
            mClubPage.setSocial(media.getText().toString());
            mClubPage.setContact(contact.getText().toString());
            doc.set(mClubPage);
        }
    }
}
