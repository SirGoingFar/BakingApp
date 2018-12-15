package com.eemf.sirgoingfar.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import com.eemf.sirgoingfar.bakingapp.activities.MealListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class SimpleActivityFragmentFlowTest {

    private final String MEAL_NAME = "Nutella Pie";

    @Rule
    public ActivityTestRule<MealListActivity> mActivityTestRule
            = new ActivityTestRule<>(MealListActivity.class);

    @Test
    public void MealListRecyclerViewShowing() {
        onView(withId(R.id.rv_meal_list)).check(matches(isDisplayed()));
    }

    @Test
    public void clickMealListRecyclerView_OpensWithCorrectTitleBarText() {

        //clicks on the RecylerView's descendant with MEAL_NAME if exist
        onView(withId(R.id.rv_meal_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //checks if the ActionBar Title corresponds to the MEAL_NAME
        onView(allOf(instanceOf(TextView.class),
                withParent(withResourceName("action_bar")))).check(matches(withText(MEAL_NAME)));
    }

    @Test
    public void clickMealIngredientStepRecyclerViewFirstItem_OpensIngredientFragment_ShowsHeaderView() {

        onView(withId(R.id.rv_meal_step_ingredient_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.tv_header_text)).check(matches(isDisplayed()));
    }

    @Test
    public void clickMealIngredientStepRecyclerViewOtherItem_OpensStepFragment_ShowsExoPlayerView() {

        onView(withId(R.id.rv_meal_step_ingredient_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.exo_player)).check(matches(isDisplayed()));
    }
}
