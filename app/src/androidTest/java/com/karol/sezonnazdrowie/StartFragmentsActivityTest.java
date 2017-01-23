package com.karol.sezonnazdrowie;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.karol.sezonnazdrowie.view.FragmentsActivity;
import com.karol.sezonnazdrowie.view.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class StartFragmentsActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void startFragmentsActivityToSeeFruits() {
        onView(withId(R.id.fruitsBtn))
                .perform(click());

        intended(toPackage("com.karol.sezonnazdrowie"));
        intended(hasComponent(FragmentsActivity.class.getName()));
        intended(hasExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_FRUITS));
    }

    @Test
    public void startFragmentsActivityToSeeVegetables() {
        onView(withId(R.id.vegetablesBtn))
                .perform(click());

        intended(toPackage("com.karol.sezonnazdrowie"));
        intended(hasComponent(FragmentsActivity.class.getName()));
        intended(hasExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_VEGETABLES));
    }

    @Test
    public void startFragmentsActivityToSeeIncoming() {
        onView(withId(R.id.incomingSeasonBtn))
                .perform(click());

        intended(toPackage("com.karol.sezonnazdrowie"));
        intended(hasComponent(FragmentsActivity.class.getName()));
        intended(hasExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_INCOMING));
    }

    @Test
    public void startFragmentsActivityToSeeCalendar() {
        onView(withId(R.id.calendarBtn))
                .perform(click());

        intended(toPackage("com.karol.sezonnazdrowie"));
        intended(hasComponent(FragmentsActivity.class.getName()));
        intended(hasExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_CALENDAR));
    }

    @Test
    public void startFragmentsActivityToSeeList() {
        onView(withId(R.id.shopListBtn))
                .perform(click());

        intended(toPackage("com.karol.sezonnazdrowie"));
        intended(hasComponent(FragmentsActivity.class.getName()));
        intended(hasExtra(FragmentsActivity.INTENT_WHAT, FragmentsActivity.INTENT_WHAT_SHOPPING_LIST));
    }
}