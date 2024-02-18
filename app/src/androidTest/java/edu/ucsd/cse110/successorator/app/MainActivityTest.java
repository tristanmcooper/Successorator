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
    /*@Test
    public void displaysHelloWorld() {
        try (var scenario = ActivityScenario.launch(MainActivity.class)) {

            // Observe the scenario's lifecycle to wait until the activity is created.
            scenario.onActivity(activity -> {
                var rootView = activity.findViewById(R.id.root);
                var binding = ActivityMainBinding.bind(rootView);

                var expected = activity.getString(R.string.hello_world);
                var actual = binding.placeholderText.getText();

                assertEquals(expected, actual);
            });

            // Simulate moving to the started state (above will then be called).
            scenario.moveToState(Lifecycle.State.STARTED);
        }
    }*/

    @Test
    
    public void markGoalAsComplete(){
        //Make a mainViewModel with a fresh repository
        var database = Room.databaseBuilder(
                        getApplicationContext(),
                        SuccessoratorDatabase.class,
                        "successorator-database"
                )
                .allowMainThreadQueries()
                .build();


        var dataSource = new RoomGoalRepository(database.goalDao());
        this.model = new MainViewModel(dataSource);

        //Add two goals to the repository
        Goal goal1 = new Goal(1, "Goal 1", false);
        Goal goal2 = new Goal(2, "Goal 2", false);
        model.addGoal(goal1);
        model.addGoal(goal2);
        //Rahul you would check here whether the size of the database updates
        // And whether the size of completeGoals (in model) updates

        //call ChangeCompleteStatus on one of them
        model.changeCompleteStatus(1);
        //Check size of incomplete goals and complete goals
        assertEquals(1, model.getCompleteGoals().getValue().size());
        assertEquals(1, model.getIncompleteGoals().getValue().size());
        //Check if the right goals are in the right lists
        assertEquals(true, dataSource.find(1).getValue().completed());
        assertEquals(false, dataSource.find(2).getValue().completed());

        assertEquals("Goal 2", model.getCompleteGoals().getValue().get(0).description());
        assertEquals("Goal 1", model.getCompleteGoals().getValue().get(0).description());
    }
}