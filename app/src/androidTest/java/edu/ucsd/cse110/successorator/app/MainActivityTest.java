package edu.ucsd.cse110.successorator.app;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import static junit.framework.TestCase.assertEquals;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import edu.ucsd.cse110.successorator.app.MainActivity;
import edu.ucsd.cse110.successorator.app.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.app.data.db.SuccessoratorDatabase;
import edu.ucsd.cse110.successorator.app.databinding.ActivityMainBinding;
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
    public void testUI(){
        try (var scenario = ActivityScenario.launch(MainActivity.class)) {
            // Observe the scenario's lifecycle to wait until the activity is created.
            scenario.onActivity(activity -> {
                var rootView = activity.findViewById(R.id.root);
                var binding = ActivityMainBinding.bind(rootView);

                this.model = activity.getMainViewModel();
                model.getRepo().clear();
                Goal goal1 = new Goal(1, "Goal 1", false);
                Goal goal2 = new Goal(2, "Goal 2", false);



                model.addGoal(goal1);
                model.addGoal(goal2);
                model.changeCompleteStatus(1);
                activity.advanceDate();








            });

            // Simulate moving to the started state (above will then be called).
            scenario.moveToState(Lifecycle.State.STARTED);
        }



    }
}