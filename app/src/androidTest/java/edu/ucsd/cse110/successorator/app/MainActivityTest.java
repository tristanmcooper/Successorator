package edu.ucsd.cse110.successorator.app;

// ESPRESSO IMPORTS
import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.action.ViewActions.click;
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

import static org.junit.Assert.assertNotNull;

import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;

import edu.ucsd.cse110.successorator.app.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.app.databinding.DialogTodayTomorrowAddGoalsBinding;
import edu.ucsd.cse110.successorator.app.ui.TodayListFragment;
import edu.ucsd.cse110.successorator.app.ui.TomorrowListFragment;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import org.junit.Rule;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    private MainViewModel model;

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

    /*
    @Test
    public void changeViewsToTomorrow() {
        // UNFINISHED
        onView(withId(R.id.views_drop_down)).perform(click());
        try {
            Thread.sleep(1000); // Adjust the sleep time as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //onView(withId(R.id.tomorrow_view)).check(matches(isDisplayed()));
    }

     */
    @Test
    public void changeViewsToTomorrow() {
        // Open the menu
        onView(withId(R.id.add_goal_button)).perform(click());
        onView(withText("")).perform(click());
        onView(withId(R.id.views_drop_down)).perform(click());

        // Click on the "Tomorrow's Goals" menu item
        onView(withText("Tomorrow's Goals")).perform(click());
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