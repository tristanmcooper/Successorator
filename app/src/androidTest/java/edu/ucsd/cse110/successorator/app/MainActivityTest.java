package edu.ucsd.cse110.successorator.app;

// ESPRESSO IMPORTS
import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.fragment.app.Fragment;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static junit.framework.TestCase.assertEquals;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;

import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;

import edu.ucsd.cse110.successorator.app.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.app.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.app.databinding.DialogTodayTomorrowAddGoalsBinding;
import edu.ucsd.cse110.successorator.app.ui.TodayListFragment;
import edu.ucsd.cse110.successorator.app.ui.TomorrowListFragment;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import org.junit.Rule;
import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    private MainViewModel model;
    TextViewStrikeThroughMatcher ts;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);


    @Test
    public void testAdvanceDateButtonChangesDateDisplay() {
        // reproduce date format from MainActivity
        SimpleDateFormat formatter = new SimpleDateFormat("E, M/d", Locale.getDefault());
        // Calculate expected date by adding 1
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        String expectedDate = formatter.format(calendar.getTime());

        // Click advance date button
        onView(withId(R.id.button_advance_date)).perform(click());

        // Check expected matches actual
        onView(withId(R.id.dateTextView)).check(matches(withText(expectedDate)));
    }

    @Test
    public void changeViewsToTomorrow() {
        // Open the menu
        //onView(withId(R.id.add_goal_button)).perform(click());
        //onView(withText("")).perform(click());
        onView(withId(R.id.views_drop_down)).perform(click());
        onView(withText("Tomorrow's Goals")).perform(click());

        // Click on the "Tomorrow's Goals" menu item
        //onView(withId(R.id.tomorrow_view)).perform(click());
        Fragment currentFragment = getCurrentFragment();

        assert(currentFragment instanceof TomorrowListFragment);

    }
    @Test
    public void addGoalButtonOpensAddGoalDialog() {
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withText("New Goal")).check(matches(isDisplayed()));
        // used RootMatchers class for exhaustive two-way check
        onView(withText("New Goal")).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed()));
    }

    @Test
    public void recurringGoalTest(){
        onView(withId(R.id.views_drop_down)).perform(click());
        onView(withText("Recurring Goals")).perform(click());

        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.editText)).perform(typeText("Daily Goal"), closeSoftKeyboard());
        onView(withId(R.id.daily)).perform(click());
        onView(withText("Create")).perform(click());

        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.editText)).perform(typeText("Weekly Goal"), closeSoftKeyboard());
        onView(withId(R.id.weekly)).perform(click());
        onView(withText("Create")).perform(click());

        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.editText)).perform(typeText("Monthly Goal"), closeSoftKeyboard());
        onView(withId(R.id.monthly)).perform(click());
        onView(withText("Create")).perform(click());

        onView(withId(R.id.views_drop_down)).perform(click());
        onView(withText("Today's Goals")).perform(click());

        onView(withText("Daily Goal")).perform(click());
        onView(withText("Weekly Goal")).perform(click());
        onView(withText("Monthly Goal")).perform(click());

        onView(withText("Daily Goal")).check(matches(TextViewStrikeThroughMatcher.withStrikeThrough()));
        onView(withText("Weekly Goal")).check(matches((TextViewStrikeThroughMatcher.withStrikeThrough())));
        onView(withId(R.id.button_advance_date)).perform(click());

        onView(withText("Daily Goal")).check(matches(not(TextViewStrikeThroughMatcher.withStrikeThrough())));
        for(int i = 0; i<6; i++){
            onView(withId(R.id.button_advance_date)).perform(click());
        }
        for(int i = 0; i<28; i++){
            onView(withId(R.id.button_advance_date)).perform(click());
        }
        onView(withText("Weekly Goal")).check(matches(not(TextViewStrikeThroughMatcher.withStrikeThrough())));
        onView(withText("Monthly Goal")).check(matches(not(TextViewStrikeThroughMatcher.withStrikeThrough())));
        onView(withText("Monthly Goal")).perform(click());
        onView(withText("Monthly Goal")).check(matches((TextViewStrikeThroughMatcher.withStrikeThrough())));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Handle interrupted exception
            e.printStackTrace();
        }
    }

    @Test
    public void pendingGoalTest(){
        onView(withId(R.id.views_drop_down)).perform(click());
        onView(withText("Pending Goals")).perform(click());

        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.editText)).perform(typeText("Today Goal"), closeSoftKeyboard());
        onView(withText("Create")).perform(click());

        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.editText)).perform(typeText("Tomorrow Goal"), closeSoftKeyboard());
        onView(withText("Create")).perform(click());

        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.editText)).perform(typeText("Finished Goal"), closeSoftKeyboard());
        onView(withText("Create")).perform(click());

        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withId(R.id.editText)).perform(typeText("Deleted Goal"), closeSoftKeyboard());
        onView(withText("Create")).perform(click());
        //moving everything out of pending
        onView(withText("Today Goal")).perform(longClick());
        onView(withText("Today")).perform(click());

        onView(withText("Tomorrow Goal")).perform(longClick());
        onView(withText("Tomorrow")).perform(click());

        onView(withText("Finished Goal")).perform(longClick());
        onView(withText("Finished")).perform(click());

        onView(withText("Deleted Goal")).perform(longClick());
        onView(withText("Delete")).perform(click());

        onView(withId(R.id.uncompleted_goal_list))
                .check(matches(not(hasDescendant(withText("Today Goal")))));
        onView(withId(R.id.uncompleted_goal_list))
                .check(matches(not(hasDescendant(withText("Tomorrow Goal")))));
        onView(withId(R.id.uncompleted_goal_list))
                .check(matches(not(hasDescendant(withText("Finished Goal")))));
        onView(withId(R.id.uncompleted_goal_list))
                .check(matches(not(hasDescendant(withText("Deleted Goal")))));

        //going to today
        onView(withId(R.id.views_drop_down)).perform(click());
        onView(withText("Today's Goals")).perform(click());

        onView(withText("Today Goal")).check(matches(not(TextViewStrikeThroughMatcher.withStrikeThrough())));
        onView(withText("Finished Goal")).check(matches((TextViewStrikeThroughMatcher.withStrikeThrough())));
        onView(withId(R.id.uncompleted_goal_list))
                .check(matches(not(hasDescendant(withText("Deleted Goal")))));
        onView(withId(R.id.completed_goal_list))
                .check(matches(not(hasDescendant(withText("Deleted Goal")))));

        onView(withId(R.id.views_drop_down)).perform(click());
        onView(withText("Tomorrow's Goals")).perform(click());
        onView(withText("Tomorrow Goal")).check(matches(not(TextViewStrikeThroughMatcher.withStrikeThrough())));
        onView(withId(R.id.uncompleted_goal_list))
                .check(matches(not(hasDescendant(withText("Deleted Goal")))));
        onView(withId(R.id.completed_goal_list))
                .check(matches(not(hasDescendant(withText("Deleted Goal")))));
    }

    private Fragment getCurrentFragment() {
        ActivityScenario<MainActivity> activityScenario = activityRule.getScenario();
        MainActivity activity = getActivity(activityScenario);
        if (activity != null) {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            if (fragmentManager != null) {
                // Find the currently displayed fragment by tag or ID
                return fragmentManager.findFragmentById(R.id.fragment_container);
            }
        }
        return null;
    }
    private MainActivity getActivity(ActivityScenario<MainActivity> activityScenario) {
        final MainActivity[] activity = new MainActivity[1];
        activityScenario.onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity mainActivity) {
                activity[0] = mainActivity;
            }
        });
        return activity[0];
    }
}
