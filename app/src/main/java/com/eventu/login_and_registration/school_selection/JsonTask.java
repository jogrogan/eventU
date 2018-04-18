package com.eventu.login_and_registration.school_selection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Used as an asynchronous task to read from the University Domains and Names Data API. Once
 * read a SchoolInfo is created and set into the ListView
 */
public class JsonTask extends AsyncTask<String, String, String> {

    // WeakReferences to the calling class as to prevent memory leaks
    private final WeakReference<Context> context;
    private final WeakReference<ListView> mSchoolList;

    public JsonTask(Context context, ListView mSchoolList) {
        this.context = new WeakReference<>(context);
        this.mSchoolList = new WeakReference<>(mSchoolList);
    }

    // Read Json from the url
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
     * If Json objects were found then update the ListView
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result == null) {
            return;
        }
        // Update the school ListView with the new objects found
        try {
            SchoolInfo[] schoolJSONObjects = new Gson().fromJson(result, SchoolInfo[].class);
            final SchoolSelectionListAdapter adapter = new SchoolSelectionListAdapter(context.get(),
                    Arrays.asList(schoolJSONObjects));
            mSchoolList.get().setAdapter(adapter);
        } catch (JsonParseException e) {
            Log.e("ERROR", e.getMessage());
        }
    }
}
