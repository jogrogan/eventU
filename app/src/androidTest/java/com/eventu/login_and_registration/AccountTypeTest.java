package com.eventu.login_and_registration;

import static android.Manifest.permission.READ_CONTACTS;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.eventu.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

public class AccountTypeTest {
    @Rule
    public final ActivityTestRule<AccountTypeActivity> mActivityRule = new ActivityTestRule<>(
            AccountTypeActivity.class, false, false);
    private Intent intent;

    /**
     * Creates a few test objects
     */
    @Before
    public void before() {
        Intents.init();
        ArrayList<String> testDomains = new ArrayList<>();
        testDomains.add("mailinator.com");

        intent = new Intent();
        intent.putExtra("schoolName", "Test");
        intent.putExtra("schoolDomains", testDomains);
    }

    /**
     * Verifies "Club" button brings user to RegisterActivity, selects "Deny" in dialog box
     */
    @Test
    public void clubAccount() {
        mActivityRule.launchActivity(intent);
        intended(hasComponent(AccountTypeActivity.class.getName()));

        mActivityRule.getActivity().requestPermissions(new String[]{READ_CONTACTS}, 0);
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject denyPermissions = mDevice.findObject(new UiSelector().text("DENY"));
        if (denyPermissions.exists()) {
            try {
                denyPermissions.click();
            } catch (UiObjectNotFoundException e) {
                Log.d("TEST", "There is no permissions dialog to interact with ");
            }
        }
        Espresso.onView(ViewMatchers.withId(R.id.club_button)).perform(click());
    }

    /**
     * Verifies "Student" button brings user to RegisterActivity, selects "Allow" in dialog box
     */
    @Test
    public void studentAccount() {
        mActivityRule.launchActivity(intent);
        intended(hasComponent(AccountTypeActivity.class.getName()));

        mActivityRule.getActivity().requestPermissions(new String[]{READ_CONTACTS}, 0);
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject allowPermissions = mDevice.findObject(new UiSelector().text("ALLOW"));
        if (allowPermissions.exists()) {
            try {
                allowPermissions.click();
            } catch (UiObjectNotFoundException e) {
                Log.d("TEST", "There is no permissions dialog to interact with ");
            }
        }

        Espresso.onView(withId(R.id.student_button)).perform(click());
        intended(hasComponent(RegisterActivity.class.getName()));
    }

    /**
     * Releases Intents
     */
    @After
    public void after() {
        Intents.release();
    }
}
