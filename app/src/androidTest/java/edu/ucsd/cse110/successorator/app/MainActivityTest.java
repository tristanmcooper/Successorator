package edu.ucsd.cse110.successorator.app;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static junit.framework.TestCase.assertEquals;

import static org.junit.Assert.assertNotNull;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;

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
public class MainActivityTest {

    private MainViewModel model;


    @Test
    public void testUI() {
        try (var scenario = ActivityScenario.launch(MainActivity.class)) {
            // Observe the scenario's lifecycle to wait until the activity is created.
            scenario.onActivity(activity -> {
                var rootView = activity.findViewById(R.id.root);
                var binding = ActivityMainBinding.bind(rootView);

                this.model = activity.getMainViewModel();
                model.getRepo().clear();
                Goal goal1 = new Goal(1, "Goal 1", false, LocalDateTime.now().toString(), "Daily", "W", null);
                Goal goal2 = new Goal(2, "Goal 2", false, LocalDateTime.now().toString(), "Daily", "W", null);


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
                Goal goal1 = new Goal(1, "Goal 1", false, LocalDateTime.now().toString(), "Once", "Work", null);
                Goal goal2 = new Goal(2, "Goal 2", false, LocalDateTime.now().toString(), "Once", "Work", null);
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

                Goal goal3 = new Goal(3, "Goal 2", false, tmrwFragment.getDate().toString(), "Once", "W", null);
                model.addGoal(goal3);

            });

            // Simulate moving to the started state (above will then be called).
            scenario.moveToState(Lifecycle.State.STARTED);
        }
    }
}