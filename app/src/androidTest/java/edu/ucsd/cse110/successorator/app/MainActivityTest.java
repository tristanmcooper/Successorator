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
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static junit.framework.TestCase.assertEquals;

import static org.junit.Assert.assertNotNull;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

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
import edu.ucsd.cse110.successorator.app.ui.TodayListFragment;
import edu.ucsd.cse110.successorator.app.ui.TomorrowListFragment;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

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
    public void testAdvanceDateButtonChangesDate() {
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
    public void testUI() {
        try (var scenario = ActivityScenario.launch(MainActivity.class)) {
            // Observe the scenario's lifecycle to wait until the activity is created.
            scenario.onActivity(activity -> {
                var rootView = activity.findViewById(R.id.root);
                var binding = ActivityMainBinding.bind(rootView);

                this.model = activity.getMainViewModel();
                model.getRepo().clear();
                Goal goal1 = new Goal(1, "Goal 1", false, LocalDateTime.now().toString(), "Daily", "Work");
                Goal goal2 = new Goal(2, "Goal 2", false, LocalDateTime.now().toString(), "Daily", "Work");


                model.addGoal(goal1);
                model.addGoal(goal2);
                model.changeCompleteStatus(1);
                activity.advanceDate();

            });

            // Simulate moving to the started state (above will then be called).
            scenario.moveToState(Lifecycle.State.STARTED);
        }
    }

    @Test
    public void testUS1() {
        try (var scenario = ActivityScenario.launch(MainActivity.class)) {
            // Observe the scenario's lifecycle to wait until the activity is created.
            scenario.onActivity(activity -> {
                var rootView = activity.findViewById(R.id.root);
                var binding = ActivityMainBinding.bind(rootView);

                this.model = activity.getMainViewModel();
                model.getRepo().clear();

                // Checking if completed goals will delete once date is advanced
                Goal goal1 = new Goal(1, "Goal 1", false, LocalDateTime.now().toString(), "Once", "Work");
                Goal goal2 = new Goal(2, "Goal 2", false, LocalDateTime.now().toString(), "Once", "Work");
                model.addGoal(goal1);
                model.addGoal(goal2);
                assertEquals(2, model.getCount());
                model.changeCompleteStatus(2);
                activity.advanceDate();
                assertEquals(1, model.getCount());

                // Verify if the TodayListFragment is displayed
                TodayListFragment todayFrag = activity.getTodayFrag();
                assertNotNull(todayFrag);

                // Test if the tomorrow date is one day ahead of today
                LocalDateTime todaysDate = todayFrag.getDate();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();

                // Begin fragment transaction to replace with TomorrowListFragment
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, TomorrowListFragment.newInstance())
                        .commitNow();

                // Verify if the TomorrowListFragment is displayed
                TomorrowListFragment tmrwFragment = (TomorrowListFragment) fragmentManager.findFragmentById(R.id.fragment_container);
                assertNotNull(tmrwFragment);

                // Verify if the date in TomorrowListFragment is one day ahead of today
                assertEquals(tmrwFragment.getDate(), todaysDate.plusDays(1));

                Goal goal3 = new Goal(3, "Goal 2", false, tmrwFragment.getDate().toString(), "Once", "Work");
                model.addGoal(goal3);

            });

            // Simulate moving to the started state (above will then be called).
            scenario.moveToState(Lifecycle.State.STARTED);
        }
    }
}