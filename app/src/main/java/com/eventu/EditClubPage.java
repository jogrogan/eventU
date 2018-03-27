package com.eventu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class EditClubPage extends AppCompatActivity {

    private CollectionReference mSchoolClubPage;
    String club;
    String school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_club_page);

        Intent intent = getIntent();
        club = intent.getStringExtra("club");
        school = intent.getStringExtra("school");
        TextView club_name = findViewById(R.id.club_name);
        club_name.setText(club);
    }

    //update club page information
    public void updatePage(View view){
        Intent intent = new Intent(this, DisplayClubPage.class);

        EditText club_description = findViewById(R.id.club_description);
        String description = club_description.getText().toString();

        EditText club_links = findViewById(R.id.club_links);
        String links = club_links.getText().toString();

        EditText club_social_media = findViewById(R.id.club_social_media);
        String social_media = club_social_media.getText().toString();

        EditText club_contact_info = findViewById(R.id.club_contact_info);
        String contact_info = club_contact_info.getText().toString();

        Map<String, String> data = new HashMap<>();
        data.put("CLUB_DESCRIPTION", description);
        data.put("CLUB_LINKS", links);
        data.put("CLUB_SOCIAL_MEDIA", social_media);
        data.put("CLUB_CONTACT_INFO", contact_info);

        String mEventPath = "universities/" + school + "/Club Profile Pages/" + club;
        mSchoolClubPage = FirebaseFirestore.getInstance().collection(mEventPath);
        mSchoolClubPage.add(data);

        startActivity(intent);
    }
}
