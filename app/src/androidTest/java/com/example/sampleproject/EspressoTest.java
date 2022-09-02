package com.example.sampleproject;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.sampleproject.Activities.EntryActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EspressoTest {
  @Rule
  public ActivityScenarioRule<EntryActivity> activityRule = new ActivityScenarioRule<EntryActivity>(EntryActivity.class);

  @Test
  public void testCreatePrivateEvent() {
    onView(withText("eventle")).check(matches(isDisplayed()));
    onView(ViewMatchers.withId(R.id.btn_member2_entry)).perform(ViewActions.click());
    onView(ViewMatchers.withId(R.id.calendar)).perform(ViewActions.click());

    onView(ViewMatchers.withId(R.id.btn_calendar_prev)).perform(ViewActions.click());
    onView(ViewMatchers.withId(R.id.btn_calendar_next)).perform(ViewActions.click());

    onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click());
    onView(ViewMatchers.withId(R.id.btn_public_sel)).perform(ViewActions.click());
    onView(ViewMatchers.withId(R.id.btn_private_sel)).perform(ViewActions.click());
    onView(ViewMatchers.withId(R.id.et_title_dialog)).perform(ViewActions.typeText("event"), ViewActions.closeSoftKeyboard());
    onView(ViewMatchers.withId(R.id.et_description_dialog)).perform(ViewActions.typeText("event"), ViewActions.closeSoftKeyboard());
    onView(ViewMatchers.withId(R.id.btn_submit_event)).perform(ViewActions.click());

    onView(ViewMatchers.withId(R.id.home)).perform(ViewActions.click());
    onView(withText("Welcome,")).check(matches(isDisplayed()));

    onView(ViewMatchers.withId(R.id.profile)).perform(ViewActions.click());
//    DatabaseReference help = FirebaseDatabase.getInstance("https://scheduleapp-3ebb7-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("members").child("member2").child("events");
//
  }
}
