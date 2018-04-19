package com.eventu;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SettingsActivityTest {

    @Rule
    public final ActivityTestRule<SettingsActivity> mActivityRule =
            new ActivityTestRule<SettingsActivity>(SettingsActivity.class, false, true);

    private Context mContext = InstrumentationRegistry.getTargetContext();

    private static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }

    @Test
    public void TestonCreate() {
        //Tests general settings are working
        Espresso.onView(withText("General"))
                .perform(click());
        Espresso.onView(
                withIndex(withText(mContext.getString(R.string.pref_title_social_recommendations)),
                        0))
                .check(matches(isDisplayed()));
        Espresso.onView(
                withIndex(withText(mContext.getString(R.string.pref_title_display_name)), 0))
                .check(matches(isDisplayed()));
        Espresso.onView(
                withIndex(withText(mContext.getString(R.string.pref_title_add_friends_to_messages)),
                        0))
                .check(matches(isDisplayed()));
        Espresso.onView(withContentDescription("Navigate up"))
                .perform(click());

        //Tests notification settings are working
        Espresso.onView(withText("Notifications"))
                .perform(click());
        Espresso.onView(withIndex(
                withText(mContext.getString(R.string.pref_title_new_message_notifications)), 0))
                .check(matches(isDisplayed()));
        Espresso.onView(withIndex(withText(mContext.getString(R.string.pref_title_ringtone)), 0))
                .check(matches(isDisplayed()));
        Espresso.onView(withIndex(withText(mContext.getString(R.string.pref_title_vibrate)), 0))
                .check(matches(isDisplayed()));
        Espresso.onView(withContentDescription("Navigate up"))
                .perform(click());


        //Tests sycn and data settings are working
        Espresso.onView(withText("Data & sync"))
                .perform(click());
        Espresso.onView(
                withIndex(withText(mContext.getString(R.string.pref_title_sync_frequency)), 0))
                .check(matches(isDisplayed()));
        Espresso.onView(
                withIndex(withText(mContext.getString(R.string.pref_title_system_sync_settings)),
                        0))
                .check(matches(isDisplayed()));
        Espresso.onView(withContentDescription("Navigate up"))
                .perform(click());
    }
}