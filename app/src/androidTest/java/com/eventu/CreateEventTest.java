package com.eventu;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class CreateEventTest {
    @Rule
    public final ActivityTestRule<CreateEventActivity> mActivityRule = new ActivityTestRule<>(
            CreateEventActivity.class, false, false);
    CountDownLatch authSignal;

    /**
     * Used to set the time of the Timepicker within Espresso calls
     */
    private static ViewAction setTime(final int hour) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                TimePicker tp = (TimePicker) view;
                tp.setHour(hour);
            }

            @Override
            public String getDescription() {
                return "Set the passed time into the TimePicker";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(TimePicker.class);
            }
        };
    }

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
    }

    /**
     * Verifies launching and return of the image selection from the phone's storage
     */
    @Test
    public void imageSelection() {
        mActivityRule.launchActivity(new Intent());
        intended(hasComponent(CreateEventActivity.class.getName()));

        Uri uri = Uri.parse("android.resource://" + R.class.getPackage().getName() + "/"
                + R.drawable.test_logo);
        Intent resultData = new Intent();
        resultData.setData(uri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(
                Activity.RESULT_OK, resultData);

        Matcher<Intent> expectedIntent = Matchers.allOf(hasAction(Intent.ACTION_PICK),
                hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));

        intending(expectedIntent).respondWith(result);
        Espresso.onView(withId(R.id.select_image_button)).perform(click());
        intended(expectedIntent);
    }

    /**
     * Verifies error checking for required fields
     */
    @Test
    public void emptyFields() {
        mActivityRule.launchActivity(new Intent());
        intended(hasComponent(CreateEventActivity.class.getName()));

        Context context = InstrumentationRegistry.getTargetContext();

        Espresso.onView(withId(R.id.next_button)).perform(scrollTo(), click());
        Espresso.onView(withId(R.id.event_name)).check(
                matches(hasErrorText(context.getString(R.string.error_field_required))));
        Espresso.onView(withId(R.id.event_location)).check(
                matches(hasErrorText(context.getString(R.string.error_field_required))));
        Espresso.onView(withId(R.id.event_description)).check(
                matches(hasErrorText(context.getString(R.string.error_field_required))));
    }

    /**
     * Verifies the ability for a club to modify their event, tests image modification as well
     */
    @Test
    public void modifyEvent() {
        Intent intent = new Intent();
        intent.putExtra("eventID", "SAAQuotM98szfnrZcWWs");
        mActivityRule.launchActivity(intent);
        intended(hasComponent(CreateEventActivity.class.getName()));

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }

        Espresso.onView(withId(R.id.event_name)).check(
                matches(withText("Test Name")));
        Espresso.onView(withId(R.id.event_location)).check(
                matches(withText("Test Location")));
        Espresso.onView(withId(R.id.event_description)).check(
                matches(withText("Test Description")));

        Uri uri = Uri.parse("android.resource://" + R.class.getPackage().getName() + "/"
                + R.drawable.test_logo);
        Intent resultData = new Intent();
        resultData.setData(uri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(
                Activity.RESULT_OK, resultData);

        Matcher<Intent> expectedIntent = Matchers.allOf(hasAction(Intent.ACTION_PICK),
                hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));

        intending(expectedIntent).respondWith(result);
        Espresso.onView(withId(R.id.select_image_button)).perform(scrollTo(), click());
        intended(expectedIntent);

        Espresso.onView(withId(R.id.next_button)).perform(scrollTo(), click());

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Verifies creating a new event
     */
    @Test
    public void newEvent() {
        mActivityRule.launchActivity(new Intent());
        intended(hasComponent(CreateEventActivity.class.getName()));

        String eventName = "Event Name";
        String eventLocation = "Event Location";
        String eventDescription = "Event Description";

        Espresso.onView(ViewMatchers.withId(R.id.event_name)).perform(typeText(eventName));
        Espresso.onView(ViewMatchers.withId(R.id.event_location)).perform(typeText(eventLocation));
        Espresso.onView(ViewMatchers.withId(R.id.event_description)).perform(
                typeText(eventDescription));

        Calendar calendar = Calendar.getInstance();

        Espresso.onView(withId(R.id.tp_timepicker)).perform(
                setTime((calendar.get(Calendar.HOUR_OF_DAY) + 1) % 24));
        Espresso.onView(withId(R.id.next_button)).perform(scrollTo(), click());

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }

        // TODO: Add event deletion
    }

    /**
     * Releases Intents
     */
    @After
    public void after() {
        Intents.release();
    }

}
