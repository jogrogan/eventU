package com.eventu.login_and_registration;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.eventu.R;
import com.eventu.login_and_registration.school_selection.SchoolInfo;
import com.eventu.login_and_registration.school_selection.SchoolSelectActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

public class SchoolSelectTest {
    @Rule
    public final ActivityTestRule<SchoolSelectActivity> mActivityRule = new ActivityTestRule<>(
            SchoolSelectActivity.class, false, false);

    /**
     * Used to compare SchoolInfo objects within Espresso calls
     */
    private static Matcher<Object> withSchoolName(final String name) {
        return new BoundedMatcher<Object, SchoolInfo>(SchoolInfo.class) {
            @Override
            protected boolean matchesSafely(SchoolInfo info) {
                return name.equals(info.getName());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with name: " + name);
            }
        };
    }

    /**
     * Verifies when entering text into the "Enter School" field, correct options are displayed
     */
    @Test
    public void schoolSelect() {
        Intents.init();

        mActivityRule.launchActivity(new Intent());
        intended(hasComponent(SchoolSelectActivity.class.getName()));

        Espresso.onView(ViewMatchers.withId(R.id.school)).perform(typeText("ren"));
        Espresso.onView(withId(R.id.school)).check(matches(withText("ren")));

        Espresso.onData(withSchoolName("Rensselaer Polytechnic Institute")).perform(click());
        intended(hasComponent(AccountTypeActivity.class.getName()));

        mActivityRule.finishActivity();
        Intents.release();
    }
}
