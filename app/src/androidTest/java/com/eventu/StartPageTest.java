package com.eventu;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

import com.eventu.login_and_registration.LoginActivity;
import com.eventu.login_and_registration.StartPageActivity;
import com.eventu.login_and_registration.school_selection.SchoolSelectActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class StartPageTest {

    @Rule
    public final ActivityTestRule<StartPageActivity> mActivityRule = new ActivityTestRule<>(
            StartPageActivity.class, false, false);

    @Before
    public void before() {
        Intents.init();
        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
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
     * Verifies "Register" button brings user to SchoolSelectActivity
     */
    @Test
    public void registerButton() {
        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        preferencesEditor.putBoolean(context.getString(R.string.RememberAccess), false);
        preferencesEditor.commit();

        mActivityRule.launchActivity(new Intent());
        intended(hasComponent(StartPageActivity.class.getName()));
        Espresso.onView(withId(R.id.register_button)).perform(click());
        intended(hasComponent(SchoolSelectActivity.class.getName()));
    }

    /**
     * Verifies "Login" button brings user to LoginActivity
     */
    @Test
    public void logInButton() {
        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        preferencesEditor.putBoolean(context.getString(R.string.RememberAccess), false);
        preferencesEditor.commit();

        mActivityRule.launchActivity(new Intent());
        intended(hasComponent(StartPageActivity.class.getName()));
        Espresso.onView(withId(R.id.log_in_button)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }

    /**
     * Sets the "Remember Me" variable in shared preferences to true
     */
    @Test
    public void rememberMePreference() {
        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences.Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        preferencesEditor.putBoolean(context.getString(R.string.RememberAccess), true);
        preferencesEditor.commit();

        mActivityRule.launchActivity(new Intent());
        intended(hasComponent(StartPageActivity.class.getName()));
    }

    @After
    public void after() {
        Intents.release();
    }
}
