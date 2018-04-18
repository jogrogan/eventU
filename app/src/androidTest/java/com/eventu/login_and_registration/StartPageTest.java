package com.eventu.login_and_registration;

import static android.Manifest.permission.READ_CONTACTS;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.eventu.R;
import com.eventu.login_and_registration.school_selection.SchoolSelectActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class StartPageTest {
    @Rule
    public final ActivityTestRule<StartPageActivity> mActivityRule = new ActivityTestRule<>(
            StartPageActivity.class, false, false);
    private CountDownLatch authSignal;

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
    }

    /**
     * Verifies back button keeps user on the same page
     */
    @Test
    public void backButton() {
        mActivityRule.launchActivity(new Intent());
        Espresso.pressBack();
        intended(hasComponent(StartPageActivity.class.getName()));
    }

    /**
     * Verifies "Register" button, brings user to SchoolSelectActivity
     */
    @Test
    public void registerButton() {
        noRememberMePreference();

        Espresso.onView(withId(R.id.register_button)).perform(click());
        intended(hasComponent(SchoolSelectActivity.class.getName()));
    }

    /**
     * Verifies "Login" button brings user to LoginActivity, allows preferences
     */
    @Test
    public void logInButtonAllow() {
        noRememberMePreference();

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

        Espresso.onView(withId(R.id.log_in_button)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }

    /**
     * Verifies "Login" button brings user to LoginActivity, denies preferences
     */
    @Test
    public void logInButtonDeny() {
        noRememberMePreference();

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

        Espresso.onView(withId(R.id.log_in_button)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }

    /**
     * Sets "Remember Me" to false and launches StartPageActivity
     */
    private void noRememberMePreference() {
        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(
                context).edit();
        preferencesEditor.putBoolean(context.getString(R.string.RememberAccess), false);
        preferencesEditor.commit();

        mActivityRule.launchActivity(new Intent());
        intended(hasComponent(StartPageActivity.class.getName()));
    }

    /**
     * Sets the "Remember Me" to true and signs in a test user if user is not signed in already
     */
    @Test
    public void rememberMePreference() {
        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(
                context).edit();
        preferencesEditor.putBoolean(context.getString(R.string.RememberAccess), true);
        preferencesEditor.commit();

        authSignal = new CountDownLatch(1);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword("test@mailinator.com",
                "password").addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        authSignal.countDown();
                    }
                });

        try {
            authSignal.await(10, TimeUnit.SECONDS);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        mActivityRule.launchActivity(new Intent());
        intended(hasComponent(StartPageActivity.class.getName()));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
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
