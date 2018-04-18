package com.eventu.login_and_registration;

import static android.Manifest.permission.READ_CONTACTS;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
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

public class RegisterTest {

    @Rule
    public final ActivityTestRule<RegisterActivity> mActivityRule = new ActivityTestRule<>(
            RegisterActivity.class, false, false);

    /**
     * Initializes Intents
     */
    @Before
    public void before() {
        Intents.init();
    }

    /**
     * Verifies error on wrong email domain and error on invalid password
     */
    @Test
    public void wrongEmailDomain() {
        ArrayList<String> testDomains = new ArrayList<>();
        testDomains.add("mailinator.com");

        Intent intent = new Intent();
        intent.putExtra("schoolName", "Test");
        intent.putExtra("schoolDomains", testDomains);
        intent.putExtra("isClub", false);

        String clubName = "Test Club";
        String email = "@";
        String password = "a";

        launchActivity(intent);
        Context context = InstrumentationRegistry.getTargetContext();

        Espresso.onView(ViewMatchers.withId(R.id.name)).perform(typeText(clubName));
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(typeText(email));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(typeText(password));
        Espresso.onView(withId(R.id.register_button)).perform(click());

        Espresso.onView(withId(R.id.email)).check(
                matches(hasErrorText(context.getString(R.string.error_no_email_domain_match))));
        Espresso.onView(withId(R.id.password)).check(
                matches(hasErrorText(context.getString(R.string.error_password_short))));
    }

    /**
     * Verifies error on invalid email
     */
    @Test
    public void invalidEmail() {
        ArrayList<String> testDomains = new ArrayList<>();
        testDomains.add("mailinator.com");

        Intent intent = new Intent();
        intent.putExtra("schoolName", "Test");
        intent.putExtra("schoolDomains", testDomains);
        intent.putExtra("isClub", false);

        String clubName = "Test Club";
        String email = "@mailinator.com";
        String password = "password";

        launchActivity(intent);
        Context context = InstrumentationRegistry.getTargetContext();

        Espresso.onView(ViewMatchers.withId(R.id.name)).perform(typeText(clubName));
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(typeText(email));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(typeText(password));
        Espresso.onView(withId(R.id.register_button)).perform(click());

        Espresso.onView(withId(R.id.email)).check(
                matches(hasErrorText(context.getString(R.string.error_invalid_email))));
    }

    /**
     * Verifies field required error on all fields and registration action on click
     */
    @Test
    public void registration() {
        ArrayList<String> testDomains = new ArrayList<>();
        testDomains.add("mailinator.com");

        Intent intent = new Intent();
        intent.putExtra("schoolName", "Test");
        intent.putExtra("schoolDomains", testDomains);
        intent.putExtra("isClub", true);

        String clubName = "Test Club";
        String email = "test@mailinator.com";
        String password = "password";

        launchActivity(intent);
        Context context = InstrumentationRegistry.getTargetContext();

        Espresso.onView(withId(R.id.register_button)).perform(click());
        Espresso.onView(withId(R.id.name)).check(
                matches(hasErrorText(context.getString(R.string.error_field_required))));
        Espresso.onView(withId(R.id.email)).check(
                matches(hasErrorText(context.getString(R.string.error_field_required))));
        Espresso.onView(withId(R.id.password)).check(
                matches(hasErrorText(context.getString(R.string.error_field_required))));

        Espresso.onView(ViewMatchers.withId(R.id.name)).perform(typeText(clubName));
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(typeText(email));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(typeText(password));
        Espresso.onView(withId(R.id.register_button)).perform(click());
    }

    /**
     * Launches RegisterActivity and allows permissions
     */
    private void launchActivity(Intent intent) {
        mActivityRule.launchActivity(intent);
        intended(hasComponent(RegisterActivity.class.getName()));

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
    }

    @After
    public void after() {
        Intents.release();
    }
}
