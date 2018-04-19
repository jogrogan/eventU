package com.eventu;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class HomePageTest {
    @Rule
    public final ActivityTestRule<HomePageActivity> mActivityRule = new ActivityTestRule<>(
            HomePageActivity.class, false, false);

    @Before
    public void before() {
        Intents.init();
    }

    @Test
    public void testHomePageActivity() {
        Intent mIntent = new Intent();
        UserInfo mMockUser = new UserInfo("test@mailinator.com", new ArrayList<String>(),
                "Test Club", "Test",
                "QzWke1Oq06Va7WFO8jWEBwuNkFI2", true);
        mIntent.putExtra("UserInfo", mMockUser);
        mActivityRule.launchActivity(mIntent);
        intended(hasComponent(HomePageActivity.class.getName()));
        Intents.release();
    }
}
