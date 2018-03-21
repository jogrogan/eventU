package com.eventu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayClubPage extends AppCompatActivity {

    private UserInfo mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_club_page);

        Intent intent = getIntent();

        //display if coming from home page
        if (intent.getStringExtra("class").equals("home_page")){
            //if club page belongs to current user display edit button
            mCurrentUser = (UserInfo) getIntent().getSerializableExtra("UserInfo");
            TextView club_name = findViewById(R.id.display_club_name);
            club_name.setText(intent.getStringExtra("club_name"));
            if (mCurrentUser.getUsername().equals(club_name.getText())){
                Button edit = findViewById(R.id.edit_button);
                edit.setVisibility(View.VISIBLE);
            }

            //retrieve data from database
            //todo
        }

        //display if coming from edit page
        if (intent.getStringExtra("class").equals("edit_club_page")){
            String name = intent.getStringExtra("club_name");
            TextView club_name = findViewById(R.id.display_club_name);
            club_name.setText(name);
            //display description
            String description = intent.getStringExtra("club_description");
            TextView display_description = findViewById(R.id.display_club_description);
            display_description.setText(description);
            //display links
            String links = intent.getStringExtra("club_links");
            TextView display_links = findViewById(R.id.display_club_links);
            display_links.setText(links);
            //display social media links
            String social_media = intent.getStringExtra("club_social_media");
            TextView display_social_media = findViewById(R.id.display_club_media_links);
            display_social_media.setText(social_media);
            //display contact info
            String contact_info = intent.getStringExtra("club_contact_info");
            TextView display_contact_info = findViewById(R.id.display_club_contact_info);
            display_contact_info.setText(contact_info);
        }
    }

    //edit page button
    public void editPage(View view){
        Intent intent = new Intent(this, EditClubPage.class);

        //pass club name to edit page
        TextView club_name = findViewById(R.id.display_club_name);
        String name = club_name.getText().toString();
        intent.putExtra("club_name", name);

        startActivity(intent);
    }
}
