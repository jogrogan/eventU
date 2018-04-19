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
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.eventu.login_and_registration.StartPageActivity;

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

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }

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

    /**
     * public void TestCalendarAdaptor() {
     * Espresso.onView(withId(R.id.action_calendar))
     * .perform(click());
     * Espresso.onView(withIndex(withId(R.id.CalendarView),1))
     * .check(matches(isDisplayed()));
     * }
     */
    @Test
    public void TestCreateEvent() {
        Intents.init();
        Espresso.onView(withId(R.id.action_create_event))
                .perform(click());
        intended(hasComponent(CreateEventActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void TestEditEvent() {
        Espresso.onView(withId(R.id.eventRecyclerView));
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
    public void TestEdit() {
        Intents.init();
        Espresso.onView(withId(R.id.eventRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        clickChildViewWithId(R.id.imagebuttonVerticalDots)));
        Espresso.onView(withText("Edit"))
                .perform(click());
        intended(hasComponent(CreateEventActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void TestFavorite() {
        Intents.init();
        int before = MockTestVariables.mockUser.getFavorites().size();
        Espresso.onView(withId(R.id.eventRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        clickChildViewWithId(R.id.imagebuttonFavorite)));
        int after = MockTestVariables.mockUser.getFavorites().size();

        assertEquals(before, after);

        Espresso.onView(withId(R.id.eventRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        clickChildViewWithId(R.id.textViewEventCreator)));
        intended(hasComponent(DisplayClubPageActivity.class.getName()));
        Espresso.pressBack();
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