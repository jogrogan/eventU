package com.eventu;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;



public class CreateEventTest {
	@Rule
	public final ActivityTestRule<CreateEventActivity> mActivityRule = new ActivityTestRule<>(
            CreateEventActivity.class, true, false);

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


    @Test
    public void testCreateEventActivity(){

    }

    //FUnctions to test
    /*
        OnCreate
        attemptReadEvent()
        attemptAddEvent()
        uploadImage()
        isValidTime()
     */


}
