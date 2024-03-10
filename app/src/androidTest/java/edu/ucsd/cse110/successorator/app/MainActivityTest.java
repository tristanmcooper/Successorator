package edu.ucsd.cse110.successorator.app;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static junit.framework.TestCase.assertEquals;

import static org.junit.Assert.assertNotNull;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;
import java.util.ArrayList;

import edu.ucsd.cse110.successorator.app.MainActivity;
import edu.ucsd.cse110.successorator.app.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.app.data.db.SuccessoratorDatabase;
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
                model.changeCompleteStatus(2);
                activity.advanceDate();

                // Verify if the TodayListFragment is displayed
                TodayListFragment todayFrag = activity.getTodayFrag();
                assertNotNull(todayFrag);
                assertEquals(todayFrag.displayedTodayGoals.size(),1);

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

                model.addGoal(new Goal(3,"Goal 3", false,tmrwFragment.getDate().toString(),"Once", "Work"));
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, TomorrowListFragment.newInstance())
                        .commitNow();
                tmrwFragment = (TomorrowListFragment) fragmentManager.findFragmentById(R.id.fragment_container);
                assertEquals(1,tmrwFragment.displayedTodayGoals.size());
                tmrwFragment.updateDate(tmrwFragment.getDate().plusDays(1));
                assertEquals(0, tmrwFragment.displayedTodayGoals.size());

            });

            // Simulate moving to the started state (above will then be called).
            scenario.moveToState(Lifecycle.State.STARTED);
        }
    }
}