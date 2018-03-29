package com.eventu.login_and_registration.school_selection;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.eventu.BaseClass;
import com.eventu.R;
import com.eventu.login_and_registration.AccountTypeActivity;

import java.util.Collections;

/**
 * Allows the user to select the university they attend
 */
public class SchoolSelectActivity extends BaseClass {

    private SchoolSelectionListAdapter adapter;

    // UI references.
    private EditText mSchoolView;
    private ListView mSchoolList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_select);

        mSchoolView = findViewById(R.id.school);
        mSchoolList = findViewById(R.id.school_list);
        adapter = new SchoolSelectionListAdapter(this, Collections.<SchoolInfo>emptyList());
        mSchoolList.setAdapter(adapter);

        mSchoolView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchSchools();
            }
        });

        mSchoolList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SchoolInfo school = (SchoolInfo) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(SchoolSelectActivity.this, AccountTypeActivity.class);
                String schoolName = school.getName().replace('/', ' ');
                intent.putExtra("schoolName", schoolName);
                intent.putExtra("schoolDomains", school.getDomains());
                startActivity(intent);
            }
        });
    }

    /**
     * Handles form errors for the school field.
     * Starts the process to get JSON information about the universities matching the string the
     * user entered
     */
    private void searchSchools() {
        String school = mSchoolView.getText().toString();

        /* Wait for at least three characters before updating the school list */
        if (school.length() < 3) {
            adapter = new SchoolSelectionListAdapter(this, Collections.<SchoolInfo>emptyList());
            mSchoolList.setAdapter(adapter);
            return;
        }

        String url = "http://universities.hipolabs.com/search?name=" + school
                + "&country=United%20States";
        new JsonTask(this, mSchoolList).execute(url);
    }
}