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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

import com.eventu.HomePageActivity;
import com.eventu.R;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginTest {

    @Rule
    public final ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class, false, false);

    /**
     * Initializes Intents and clears app preferences
     */
    @Before
    public void before() {
        Intents.init();

        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(
                context).edit();
        preferencesEditor.clear();
        preferencesEditor.commit();

        mActivityRule.launchActivity(new Intent());
        intended(hasComponent(LoginActivity.class.getName()));

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

    /**
     * Tests clicking login with empty fields
     */
    @Test
    public void emptyFields() {
        Context context = InstrumentationRegistry.getTargetContext();

        Espresso.onView(withId(R.id.log_in_button)).perform(click());
        Espresso.onView(withId(R.id.email)).check(
                matches(hasErrorText(context.getString(R.string.error_field_required))));
        Espresso.onView(withId(R.id.password)).check(
                matches(hasErrorText(context.getString(R.string.error_field_required))));
    }

    /**
     * Tests clicking login with invalid email
     */
    @Test
    public void invalidEmail() {
        Context context = InstrumentationRegistry.getTargetContext();

        String email = "email";
        String password = "password";
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(typeText(email));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(typeText(password));

        Espresso.onView(withId(R.id.log_in_button)).perform(click());
        Espresso.onView(withId(R.id.email)).check(
                matches(hasErrorText(context.getString(R.string.error_invalid_email))));
    }

    /**
     * Tests selecting and deselecting "Remember me" option
     */
    @Test
    public void rememberMe() {
        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean rememberme = preferences.getBoolean(context.getString(R.string.RememberAccess),
                false);
        Assert.assertEquals(false, rememberme);

        Espresso.onView(withId(R.id.remembermeCheckBox)).perform(click());
        rememberme = preferences.getBoolean(context.getString(R.string.RememberAccess), false);
        Assert.assertEquals(true, rememberme);

        Espresso.onView(withId(R.id.remembermeCheckBox)).perform(click());
        rememberme = preferences.getBoolean(context.getString(R.string.RememberAccess), false);
        Assert.assertEquals(false, rememberme);
    }

    /**
     * Tests selecting "Forgot Password?" option
     */
    @Test
    public void forgotPassword() {
        Espresso.onView(withId(R.id.forgot_password)).perform(click());
        intended(hasComponent(AccountRetrievalActivity.class.getName()));
    }

    /**
     * Verifies back button returns user to StartPageActivity
     */
    @Test
    public void backButton() {
        Espresso.pressBack();
        intended(hasComponent(StartPageActivity.class.getName()));
    }

    /**
     * Verifies a valid login takes user to the HomePageActivity
     */
    @Test
    public void validLogin() {
        String email = "test@mailinator.com";
        String password = "password";

        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(typeText(email));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(typeText(password));
        Espresso.onView(withId(R.id.log_in_button)).perform(click());

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }

        intended(hasComponent(HomePageActivity.class.getName()));
    }

    /**
     * Verifies error checking if email is invalid
     */
    @Test
    public void emailNotExist() {
        Context context = InstrumentationRegistry.getTargetContext();

        String email = "test@email.com";
        String password = "password";

        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(typeText(email));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(typeText(password));
        Espresso.onView(withId(R.id.log_in_button)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }

        Espresso.onView(withId(R.id.email)).check(
                matches(hasErrorText(context.getString(R.string.error_invalid_email))));
    }

    /**
     * Verifies error checking if user is not verified
     */
    @Test
    public void emailNotVerified() {
        Context context = InstrumentationRegistry.getTargetContext();

        String email = "test2@mailinator.com";
        String password = "password";

        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(typeText(email));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(typeText(password));
        Espresso.onView(withId(R.id.log_in_button)).perform(click());

        pause();

        Espresso.onView(withId(R.id.email)).check(
                matches(hasErrorText(context.getString(R.string.error_email_not_verified))));
    }

    /**
     * Verifies error checking if password is wrong
     */
    @Test
    public void passwordIncorrect() {
        Context context = InstrumentationRegistry.getTargetContext();

        String email = "test@mailinator.com";
        String password = "incorrect";

        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(typeText(email));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(typeText(password));
        Espresso.onView(withId(R.id.log_in_button)).perform(click());

        pause();

        Espresso.onView(withId(R.id.password)).check(
                matches(hasErrorText(context.getString(R.string.error_wrong_password))));
    }

    /**
     * Adds pause to main thread
     */
    private void pause() {
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Releases Intents
     */
    @After
    public void after() {
        Intents.release();
    }
}
