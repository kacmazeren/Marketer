package com.example.marketer10;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void checkLoginRegisterButtonIsDisplayed() {

        onView(withId(R.id.loginRegisterButton)).check(matches(isDisplayed()));
    }
    @Test
    public void checkDairyButtonIsDisplayed() {

        onView(withId(R.id.dairyButton)).check(matches(isDisplayed()));
    }
    @Test
    public void checkBasketButtonIsDisplayed() {

        onView(withId(R.id.setBasketButton)).check(matches(isDisplayed()));
    }
}
