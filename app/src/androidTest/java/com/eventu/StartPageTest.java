package com.eventu;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
            StartPageActivity.class);

    @Before
    public void before() {
        Intents.init();
    }

    /**
     * Verifies "Register" button brings user to SchoolSelectActivity
     */
    @Test
    public void registerButton() {
        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences prefs = context.getSharedPreferences(
                context.getString(R.string.USER_PREFS_FILE), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.RememberAccess), false);
        editor.commit();

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
        SharedPreferences prefs = context.getSharedPreferences(
                context.getString(R.string.USER_PREFS_FILE), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.RememberAccess), false);
        editor.commit();

        mActivityRule.launchActivity(new Intent());
        intended(hasComponent(StartPageActivity.class.getName()));
        Espresso.onView(withId(R.id.log_in_button)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }

    @After
    public void after() {
        mActivityRule.finishActivity();
        Intents.release();
    }
}
