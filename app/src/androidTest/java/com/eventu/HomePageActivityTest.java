package com.eventu;

import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

import com.eventu.login_and_registration.StartPageActivity;

import org.junit.Rule;
import org.junit.Test;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class HomePageActivityTest {
    @Rule
    public final ActivityTestRule<HomePageActivity> mActivityRule
            = new ActivityTestRule<HomePageActivity>(
            HomePageActivity.class, false, true) {
        @Override
        protected Intent getActivityIntent() {
            Context mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent mIntent = new Intent(mContext, HomePageActivity.class);
            mIntent.putExtra("UserInfo", MockTestVariables.mockUser);
            return mIntent;
        }
    };

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.eventu", appContext.getPackageName());

    }

    @Test
    public void TestonCreateHomePage() {
        //Checks that all the views are properly displayed to the user
        Espresso.onView(withId(R.id.eventRecyclerView))
                .check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.eventViewFlipper))
                .check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.eventSwipeRefreshView))
                .check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.bottom_navigation))
                .check(matches(isDisplayed()));
    }

    @Test
    public void TestViewFipper() {
        Espresso.onView(withId(R.id.action_timeline))
                .perform(click())
                .check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.action_calendar))
                .perform(click())
                .check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.eventRecyclerView))
                .check(matches(not(isClickable())));
    }

    @Test
    public void TestCalendarAdaptor() {
        Espresso.onView(withId(R.id.action_calendar))
                .perform(click())
                .check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.CalendarView))
                .check(matches(isDisplayed()));
    }

    @Test
    public void TestSettings() {
        Intents.init();
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        Espresso.onView(withText("Settings"))
                .perform(click());
        intended(hasComponent(SettingsActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void TestLogOut() {
        Intents.init();
        Espresso.onView(withId(R.id.action_logout))
                .perform(click());
        Espresso.onView(withText(R.string.confirm))
                .perform(click());

        intended(hasComponent(StartPageActivity.class.getName()));
        Intents.release();
    }
}