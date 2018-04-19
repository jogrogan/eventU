package com.eventu.login_and_registration;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.EditText;

import com.eventu.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class AccountRetrievalTest {

    @Rule
    public final ActivityTestRule<AccountRetrievalActivity> mActivityRule = new ActivityTestRule<>(
            AccountRetrievalActivity.class, false, false);


    /**
     * Used to assert a view has no error text
     */
    public static Matcher<View> hasNoErrorText() {
        return new BoundedMatcher<View, EditText>(EditText.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("has no error text: ");
            }

            @Override
            protected boolean matchesSafely(EditText view) {
                return view.getError() == null;
            }
        };
    }

    /**
     * Initializes Intents
     */
    @Before
    public void before() {
        Intents.init();

        mActivityRule.launchActivity(new Intent());
        intended(hasComponent(AccountRetrievalActivity.class.getName()));
    }

    /**
     * Verifies the user cannot leave no email
     */
    @Test
    public void blankEmail() {
        Context context = InstrumentationRegistry.getTargetContext();

        Espresso.onView(withId(R.id.reset_password)).perform(click());
        Espresso.onView(withId(R.id.password_retrieval)).check(
                matches(hasErrorText(context.getString(R.string.error_field_required))));
    }

    /**
     * Verifies the error checking if an email does not correspond to a real user
     */
    @Test
    public void emailDoesNotExist() {
        Context context = InstrumentationRegistry.getTargetContext();

        Espresso.onView(ViewMatchers.withId(R.id.password_retrieval)).perform(
                typeText("test@email.com"));
        Espresso.onView(withId(R.id.reset_password)).perform(click());

        pause();

        Espresso.onView(withId(R.id.password_retrieval)).check(
                matches(hasErrorText(context.getString(R.string.error_email_not_exists))));
    }

    /**
     * Verifies the user is sent an email if the email corresponds to a real user
     */
    @Test
    public void validEmail() {
        Espresso.onView(ViewMatchers.withId(R.id.password_retrieval)).perform(
                typeText("test@mailinator.com"));
        Espresso.onView(withId(R.id.reset_password)).perform(click());

        pause();

        Espresso.onView(withId(R.id.password_retrieval)).check(matches(hasNoErrorText()));
    }

    /**
     * Verifies further error checking for invalid emails
     */
    @Test
    public void invalidEmail() {
        Context context = InstrumentationRegistry.getTargetContext();

        Espresso.onView(ViewMatchers.withId(R.id.password_retrieval)).perform(typeText("a"));
        Espresso.onView(withId(R.id.reset_password)).perform(click());

        pause();

        Espresso.onView(withId(R.id.password_retrieval)).check(
                matches(hasErrorText(context.getString(R.string.error_reset_password))));
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

    @After
    public void after() {
        Intents.release();
    }
}
