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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_club_page);

        Intent intent = getIntent();
        TextView club_name = findViewById(R.id.club_name);
        club_name.setText(intent.getStringExtra("club_name"));
    }

    //update club page information
    public void updatePage(View view){
        Intent intent = new Intent(this, DisplayClubPage.class);

        //pass info to club page
        intent.putExtra("class", "edit_club_page");
        //club name
        TextView club_name = findViewById(R.id.club_name);
        String name = club_name.getText().toString();
        intent.putExtra("club_name", name);
        //club description
        EditText club_description = findViewById(R.id.club_description);
        String description = club_description.getText().toString();
        intent.putExtra("club_description", description);
        //club links
        EditText club_links = findViewById(R.id.club_links);
        String links = club_links.getText().toString();
        intent.putExtra("club_links", links);
        //club social media links
        EditText club_social_media = findViewById(R.id.club_social_media);
        String social_media = club_social_media.getText().toString();
        intent.putExtra("club_social_media", social_media);
        //club contact information
        EditText club_contact_info = findViewById(R.id.club_contact_info);
        String contact_info = club_contact_info.getText().toString();
        intent.putExtra("club_contact_info", contact_info);

        //put data in database
        Map<String, String> data = new HashMap<>();
        data.put("CLUB_DESCRIPTION", description);
        data.put("CLUB_LINKS", links);
        data.put("CLUB_SOCIAL_MEDIA", social_media);
        data.put("CLUB_CONTACT_INFO", contact_info);

        String mEventPath = "universities/<school name>/Club Profile Pages/" + name;
        mSchoolClubPage = FirebaseFirestore.getInstance().collection(mEventPath);
        mSchoolClubPage.add(data);

        startActivity(intent);
    }
}
