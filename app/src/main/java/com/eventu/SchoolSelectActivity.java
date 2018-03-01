package com.eventu;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Allows the user to select the university they attend
 */
public class SchoolSelectActivity extends BaseClass {

    SchoolObject[] schoolJSONObjects;

    // UI references.
    private EditText mSchoolView;
    private ListView mSchoolList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_select);

        mSchoolView = findViewById(R.id.school);
        mSchoolList = findViewById(R.id.school_list);

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
                Intent intent = new Intent(SchoolSelectActivity.this, AccountTypeActivity.class);
                String schoolName = schoolJSONObjects[position].getName().replace('/', ' ');
                intent.putExtra("schoolName", schoolName);
                intent.putExtra("schoolDomains", schoolJSONObjects[position].getDomains());
                startActivity(intent);
            }
        });
    }

    /**
     * Handles form errors for the school field.
     * Starts the process to get JSON information about the universities matching the school string
     */
    private void searchSchools() {
        schoolJSONObjects = new SchoolObject[0];

        String school = mSchoolView.getText().toString();

        if (school.length() < 3) {
            updateAdapter();
            return;
        }

        String url = "http://universities.hipolabs.com/search?name=" + school
                + "&country=United%20States";
        new JsonTask().execute(url);
    }

    /**
     * Updates the list view with the list of schools found
     */
    private void updateAdapter() {
        ArrayList<String> schools = new ArrayList<String>();
        for (SchoolObject school : schoolJSONObjects) {
            schools.add(school.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, schools);
        mSchoolList.setAdapter(adapter);
    }

    /**
     * Connects to the url specified when created a new JsonTask object and gets all the valid
     * json objects
     */
    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    line += "\n";
                    buffer.append(line);
                }

                return buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
         * If Json objects were found then update the list view
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            schoolJSONObjects = new Gson().fromJson(result, SchoolObject[].class);
            if (schoolJSONObjects != null) {
                updateAdapter();
            }
        }
    }

    /**
     * Used to turn the JSON found into an object with a name and domains field
     */
    private class SchoolObject {

        private String name;
        private ArrayList<String> domains;

        private String getName() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }

        private ArrayList<String> getDomains() {
            return domains;
        }

        private void setDomains(ArrayList<String> domains) {
            this.domains = new ArrayList<String>(domains);
        }
    }
}