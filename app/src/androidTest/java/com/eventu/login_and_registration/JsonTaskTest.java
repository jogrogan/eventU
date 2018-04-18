package com.eventu.login_and_registration;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;
import android.widget.ListView;

import com.eventu.login_and_registration.school_selection.JsonTask;
import com.eventu.login_and_registration.school_selection.SchoolInfo;
import com.eventu.login_and_registration.school_selection.SchoolSelectionListAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class JsonTaskTest {
    private static boolean called;
    private static SchoolInfo testSchoolInfo;
    private static Context context;
    private static CountDownLatch signal;
    private static ListView mSchoolList;

    /**
     * Creates a few test objects
     */
    @BeforeClass
    public static void beforeClass() {
        ArrayList<String> testDomains = new ArrayList<>();
        testDomains.add("rpi.edu");
        testSchoolInfo = new SchoolInfo();
        testSchoolInfo.setName("Rensselaer Polytechnic Institute");
        testSchoolInfo.setDomains(testDomains);

        context = InstrumentationRegistry.getTargetContext();
        signal = new CountDownLatch(1);
        mSchoolList = new ListView(context);
        SchoolSelectionListAdapter adapter = new SchoolSelectionListAdapter(context,
                Collections.<SchoolInfo>emptyList());
        mSchoolList.setAdapter(adapter);
    }

    /**
     * Verifies the asynchronous class JsonTask works for a valid url and retrieves the
     * correct information to populate the ListView
     */
    @Test
    public void jsonTaskTestValidURL() {
        final String validURL
                = "http://universities.hipolabs.com/search?name=ren&country=United%20States";
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                new JsonTask(context, mSchoolList) {
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);

                        SchoolInfo[] schoolJSONObjects = new Gson().fromJson(result,
                                SchoolInfo[].class);
                        called = true;
                        Assert.assertTrue(schoolJSONObjects.length > 0);
                        Assert.assertTrue(
                                Arrays.asList(schoolJSONObjects).contains(testSchoolInfo));
                        signal.countDown();
                    }
                }.execute(validURL);
            }
        });

        try {
            signal.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Assert.assertTrue(called);
    }

    /**
     * Verifies the asynchronous class JsonTask works for a url with invalid json
     */
    @Test
    public void jsonTaskTestInvalidJSON() {
        final String badURL = "http://universities.hipolabs.com/";
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                new JsonTask(context, mSchoolList) {
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);

                        SchoolInfo[] schoolJSONObjects = null;
                        try {
                            schoolJSONObjects = new Gson().fromJson(result, SchoolInfo[].class);
                        } catch (JsonParseException e) {
                            Log.e("ERROR", e.getMessage());
                        }

                        called = true;
                        Assert.assertNull(schoolJSONObjects);
                        signal.countDown();
                    }
                }.execute(badURL);
            }
        });

        try {
            signal.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Assert.assertTrue(called);
    }

    /**
     * Verifies the asynchronous class JsonTask works for an invalid url
     */
    @Test
    public void jsonTaskTestInvalidURL() {
        final String invalidURL = "q.xyz";
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                new JsonTask(context, mSchoolList) {
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);

                        SchoolInfo[] schoolJSONObjects = null;
                        try {
                            schoolJSONObjects = new Gson().fromJson(result, SchoolInfo[].class);
                        } catch (JsonParseException e) {
                            Log.e("ERROR", e.getMessage());
                        }

                        called = true;
                        Assert.assertNull(schoolJSONObjects);
                        signal.countDown();
                    }
                }.execute(invalidURL);
            }
        });

        try {
            signal.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Assert.assertTrue(called);
    }
}
