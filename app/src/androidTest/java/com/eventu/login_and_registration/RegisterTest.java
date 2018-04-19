package com.eventu.login_and_registration;

import static android.Manifest.permission.READ_CONTACTS;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.eventu.DisplayClubPageActivity;
import com.eventu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

public class RegisterTest {

    @Rule
    public final ActivityTestRule<RegisterActivity> mActivityRule = new ActivityTestRule<>(
            RegisterActivity.class, false, false);

    /**
     * Initializes Intents
     */
    @Before
    public void before() {
        Intents.init();
    }

    /**
     * Verifies error on wrong email domain and error on invalid password
     */
    @Test
    public void wrongEmailDomain() {
        ArrayList<String> testDomains = new ArrayList<>();
        testDomains.add("mailinator.com");

        Intent intent = new Intent();
        intent.putExtra("schoolName", "Test");
        intent.putExtra("schoolDomains", testDomains);
        intent.putExtra("isClub", false);

        String clubName = "Test Club";
        String email = "@";
        String password = "a";

        launchActivity(intent);
        Context context = InstrumentationRegistry.getTargetContext();

        Espresso.onView(ViewMatchers.withId(R.id.name)).perform(typeText(clubName));
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(typeText(email));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(typeText(password));
        Espresso.onView(withId(R.id.register_button)).perform(click());

        Espresso.onView(withId(R.id.email)).check(
                matches(hasErrorText(context.getString(R.string.error_no_email_domain_match))));
        Espresso.onView(withId(R.id.password)).check(
                matches(hasErrorText(context.getString(R.string.error_password_short))));
    }

    /**
     * Verifies error on invalid email
     */
    @Test
    public void invalidEmail() {
        ArrayList<String> testDomains = new ArrayList<>();
        testDomains.add("mailinator.com");

        Intent intent = new Intent();
        intent.putExtra("schoolName", "Test");
        intent.putExtra("schoolDomains", testDomains);
        intent.putExtra("isClub", false);

        String clubName = "Test Club";
        String email = "@mailinator.com";
        String password = "password";

        launchActivity(intent);
        Context context = InstrumentationRegistry.getTargetContext();

        Espresso.onView(ViewMatchers.withId(R.id.name)).perform(typeText(clubName));
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(typeText(email));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(typeText(password));
        Espresso.onView(withId(R.id.register_button)).perform(click());

        Espresso.onView(withId(R.id.email)).check(
                matches(hasErrorText(context.getString(R.string.error_invalid_email))));
    }

    /**
     * Verifies field required error on all fields
     */
    @Test
    public void emptyFields() {
        ArrayList<String> testDomains = new ArrayList<>();
        testDomains.add("mailinator.com");

        Intent intent = new Intent();
        intent.putExtra("schoolName", "Test");
        intent.putExtra("schoolDomains", testDomains);
        intent.putExtra("isClub", true);

        launchActivity(intent);
        Context context = InstrumentationRegistry.getTargetContext();

        Espresso.onView(withId(R.id.register_button)).perform(click());
        Espresso.onView(withId(R.id.name)).check(
                matches(hasErrorText(context.getString(R.string.error_field_required))));
        Espresso.onView(withId(R.id.email)).check(
                matches(hasErrorText(context.getString(R.string.error_field_required))));
        Espresso.onView(withId(R.id.password)).check(
                matches(hasErrorText(context.getString(R.string.error_field_required))));

    }

    /**
     * Verifies registration action on click for existing user
     */
    @Test
    public void registerExistingUser() {
        ArrayList<String> testDomains = new ArrayList<>();
        testDomains.add("mailinator.com");

        Intent intent = new Intent();
        intent.putExtra("schoolName", "Test");
        intent.putExtra("schoolDomains", testDomains);
        intent.putExtra("isClub", true);

        String clubName = "Test Club";
        String email = "test@mailinator.com";
        String password = "password";

        launchActivity(intent);
        Context context = InstrumentationRegistry.getTargetContext();

        Espresso.onView(ViewMatchers.withId(R.id.name)).perform(typeText(clubName));
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(typeText(email));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(typeText(password));
        Espresso.onView(withId(R.id.register_button)).perform(click());

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }

        Espresso.onView(withId(R.id.email)).check(
                matches(hasErrorText(context.getString(R.string.error_email_exists))));
    }

    /**
     * Registration action on click for new club user
     */
    @Test
    public void registerNewClubUser() {
        ArrayList<String> testDomains = new ArrayList<>();
        testDomains.add("mailinator.com");

        Intent intent = new Intent();
        intent.putExtra("schoolName", "Test");
        intent.putExtra("schoolDomains", testDomains);
        intent.putExtra("isClub", true);

        String clubName = "Test Club";
        String email = "user@mailinator.com";
        String password = "password";

        launchActivity(intent);

        Espresso.onView(ViewMatchers.withId(R.id.name)).perform(typeText(clubName));
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(typeText(email));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(typeText(password));
        Espresso.onView(withId(R.id.register_button)).perform(click());

        try {
            Thread.sleep(15000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }

        intended(hasComponent(DisplayClubPageActivity.class.getName()));
        Espresso.pressBack();
        intended(hasComponent(DisplayClubPageActivity.class.getName()));

        Espresso.onView(ViewMatchers.withId(R.id.display_club_description)).perform(
                typeText("Test"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(ViewMatchers.withId(R.id.edit_button)).perform(click());

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }

        deleteClubUser();

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Helper function to delete a user from all databases
     */
    private void deleteClubUser() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        final String schoolName = "Test";
        AuthCredential credential = EmailAuthProvider
                .getCredential("user@mailinator.com", "password");

        // Delete User from Cloud Firestore
        FirebaseFirestore.getInstance().collection("universities")
                .document(schoolName).collection("Users")
                .document(user.getUid()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore",
                                "Document successfully deleted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error deleting document", e);
                    }
                });

        // Delete Club Profile Page from Cloud Firestore
        FirebaseFirestore.getInstance().collection("universities")
                .document(schoolName).collection("Club Profile Pages")
                .document(user.getUid()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore",
                                "Document successfully deleted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error deleting document", e);
                    }
                });

        // Delete user from Firebase Authentication
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("TEST", "User account deleted.");
                                        }
                                    }
                                });

                    }
                });
    }

    /**
     * Launches RegisterActivity and allows permissions
     */
    private void launchActivity(Intent intent) {
        mActivityRule.launchActivity(intent);
        intended(hasComponent(RegisterActivity.class.getName()));

        mActivityRule.getActivity().requestPermissions(new String[]{READ_CONTACTS}, 0);
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject allowPermissions = mDevice.findObject(new UiSelector().text("ALLOW"));
        if (allowPermissions.exists()) {
            try {
                allowPermissions.click();
            } catch (UiObjectNotFoundException e) {
                Log.d("TEST", "There is no permissions dialog to interact with ");
            }
        }
    }

    /**
     * Releases Intents
     */
    @After
    public void after() {
        Intents.release();
    }
}
