package com.eventu;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClubPageTest {
    Context mContext;

    @Before
    public void testsetup() {
        Intents.init();
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.eventu", appContext.getPackageName());
        Intents.release();
    }

    /*
    Test if edit button enables and disables text fields
     */
    @Test
    public void edit(){
        //verify initial state is view mode
        Espresso.onView(ViewMatchers.withId(R.id.edit_button)).check(matches(withText("Edit")));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_description)).check(matches(not(isEnabled())));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_contact_info)).check(matches(not(isEnabled())));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_links)).check(matches(not(isEnabled())));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_media_links)).check(matches(not(isEnabled())));

        //click edit button
        Espresso.onView(ViewMatchers.withId(R.id.edit_button)).perform(click());

        //verify state after clicking button from view mode is edit mode
        Espresso.onView(ViewMatchers.withId(R.id.edit_button)).check(matches(withText("Done")));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_description)).check(matches(isEnabled()));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_contact_info)).check(matches(isEnabled()));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_links)).check(matches(isEnabled()));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_media_links)).check(matches(isEnabled()));

        //click edit button again
        Espresso.onView(ViewMatchers.withId(R.id.edit_button)).perform(click());

        //verify state after clicking button from edit mode is view mode
        Espresso.onView(ViewMatchers.withId(R.id.edit_button)).check(matches(withText("Edit")));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_description)).check(matches(not(isEnabled())));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_contact_info)).check(matches(not(isEnabled())));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_links)).check(matches(not(isEnabled())));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_media_links)).check(matches(not(isEnabled())));
    }

    @After
    public void after() {
        Intents.release();
    }
}
