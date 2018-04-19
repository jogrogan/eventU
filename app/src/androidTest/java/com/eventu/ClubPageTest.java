package com.eventu;

import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

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

public class ClubPageTest {
    @Rule
    public final ActivityTestRule<DisplayClubPageActivity> mActivityRule = new ActivityTestRule<>(
            DisplayClubPageActivity.class, false, false);
    CountDownLatch authSignal;

    /**
     * Initializes Intents and signs in the user
     */
    @Before
    public void before() {
        Intents.init();

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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Intent intent = new Intent();
        intent.putExtra("user", "QzWke1Oq06Va7WFO8jWEBwuNkFI2");
        intent.putExtra("school", "Test");
        intent.putExtra("club", "QzWke1Oq06Va7WFO8jWEBwuNkFI2");
        mActivityRule.launchActivity(intent);
        intended(hasComponent(DisplayClubPageActivity.class.getName()));

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Test if edit button enables and disables text fields
     **/
    @Test
    public void editClubPage() {
        //verify initial state is view mode
        Espresso.onView(ViewMatchers.withId(R.id.edit_button)).check(matches(withText("Edit")));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_description)).check(
                matches(not(isEnabled())));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_contact_info)).check(
                matches(not(isEnabled())));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_links)).check(
                matches(not(isEnabled())));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_media_links)).check(
                matches(not(isEnabled())));

        //click edit button
        Espresso.onView(ViewMatchers.withId(R.id.edit_button)).perform(click());

        //verify state after clicking button from view mode is edit mode
        Espresso.onView(ViewMatchers.withId(R.id.edit_button)).check(matches(withText("Done")));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_description)).check(
                matches(isEnabled()));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_contact_info)).check(
                matches(isEnabled()));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_links)).check(matches(isEnabled()));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_media_links)).check(
                matches(isEnabled()));

        //click edit button again
        Espresso.onView(ViewMatchers.withId(R.id.edit_button)).perform(click());

        //verify state after clicking button from edit mode is view mode
        Espresso.onView(ViewMatchers.withId(R.id.edit_button)).check(matches(withText("Edit")));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_description)).check(
                matches(not(isEnabled())));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_contact_info)).check(
                matches(not(isEnabled())));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_links)).check(
                matches(not(isEnabled())));
        Espresso.onView(ViewMatchers.withId(R.id.display_club_media_links)).check(
                matches(not(isEnabled())));
    }

    /**
     * Test if edit button enables and disables text fields
     **/
    @Test
    public void emptyField() {
        Context context = InstrumentationRegistry.getTargetContext();

        Espresso.onView(ViewMatchers.withId(R.id.edit_button)).perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.display_club_description)).perform(clearText());
        Espresso.onView(ViewMatchers.withId(R.id.edit_button)).perform(click());
        Espresso.onView(withId(R.id.display_club_description)).check(
                matches(hasErrorText(context.getString(R.string.error_field_required))));
    }

    /**
     * Releases Intents
     */
    @After
    public void after() {
        Intents.release();
    }
}
