package com.karol.sezonnazdrowie;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.StringEndsWith.endsWith;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.karol.sezonnazdrowie.view.FragmentsActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ShoppingListFragmentTest {

    @Rule
    public ActivityTestRule<FragmentsActivity> mActivityTestRule = new ActivityTestRule<>(FragmentsActivity.class, false, false);

    @Before
    public void startActivityWithProperIntent() {
        Intent i = new Intent();
        i.putExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_SHOPPING_LIST);
        mActivityTestRule.launchActivity(i);
    }

    @Test
    public void addingAndRemovingItem() {
        onView(withText("BIKE")).check(doesNotExist());

        onView(withId(R.id.addToShoppingListButton))
                .perform(click());

        onView(withClassName(endsWith("EditText")))
                .perform(typeText("BIKE"));

        onView(withId(android.R.id.button1))
                .perform(click());

        onView(withText("BIKE")).check(matches(isDisplayed()));

        onView(withText("BIKE")).perform(click());

        onView(withId(android.R.id.button1))
                .perform(click());

        onView(withText("BIKE")).check(doesNotExist());
    }
}
