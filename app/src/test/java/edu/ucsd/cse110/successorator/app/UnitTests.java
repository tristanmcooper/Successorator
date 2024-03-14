package edu.ucsd.cse110.successorator.app;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTests {
    private MainViewModel viewModel;
    private LocalDateTime today;

    @Before
    public void setup() {
        this.today = LocalDateTime.now().withHour(2).withMinute(0).withSecond(0).withNano(0);
        final List<Goal> DEFAULT_GOALS = List.of(
                new Goal(1, "Goal 1", false, this.today.toString(), "Daily", "H"),
                new Goal(2, "Goal 2", false, this.today.toString(), "Weekly", "W"),
                new Goal(3, "Goal 3", false, this.today.toString(), "Monthly", "S"),
                new Goal(4, "Goal 4", false, this.today.toString(), "Yearly", "E"),
                new Goal(5, "Goal 5", false, this.today.toString(), "Once", "H")
        );
        InMemoryDataSource data = new InMemoryDataSource();
        data.putGoals(DEFAULT_GOALS);
        GoalRepository goalRepo = new SimpleGoalRepository(data);

        this.viewModel = new MainViewModel(goalRepo);
    }

    @Test
    public void addGoal() {
        Goal newGoal = new Goal(6, "Goal 6", false, today.toString(), "Daily", "E");
        List<Goal> expected = List.of(
                new Goal(1, "Goal 1", false, today.toString(), "Daily", "H"),
                new Goal(2, "Goal 2", false, today.toString(), "Weekly", "W"),
                new Goal(3, "Goal 3", false, today.toString(), "Monthly", "S"),
                new Goal(4, "Goal 4", false, today.toString(), "Yearly", "E"),
                new Goal(5, "Goal 5", false, today.toString(), "Once", "H"),
                new Goal(6, "Goal 6", false, today.toString(), "Daily", "E")
        );

        viewModel.addGoal(newGoal);
        assertEquals(expected, viewModel.getRepo().findAll().getValue());
    }
    @Test
    public void changeCompleteStatus() {
        viewModel.changeCompleteStatus(1);
        assertEquals(true, viewModel.getRepo().find(1).getValue().completed());
    }

    @Test
    public void changeToTodayViewComplete() {
        LocalDateTime tomorrow = LocalDateTime.now().withHour(2).withMinute(0).withSecond(0).withNano(0).plusDays(1);
        viewModel.addGoal(new Goal(6, "Tomorrow's Goal", false, tomorrow.toString(), "Once", "W"));
        viewModel.changeToTodayViewComplete(6);
        assertEquals(true, viewModel.getRepo().find(6).getValue().completed());
        assertEquals(today.toString(), viewModel.getRepo().find(6).getValue().date());
    }

    @Test
    public void deleteCompleted() {
        List<Goal> expected = List.of(
                new Goal(1, "Goal 1", false, this.today.toString(), "Daily", "H"),
                new Goal(4, "Goal 4", false, this.today.toString(), "Yearly", "E"),
                new Goal(5, "Goal 5", false, this.today.toString(), "Once", "H")
        );

        viewModel.getRepo().changeCompleted(2);
        viewModel.getRepo().changeCompleted(3);
        viewModel.deleteCompleted();
        assertEquals(expected, viewModel.getRepo().findAll().getValue());
    }

    @Test
    public void getCompletedGoals() {}

    @Test
    public void getIncompleteGoals() {}

    @Test
    public void getRecurringGoals() {}

    @Test
    public void getCount() {}

    @Test
    public void getDisplayedTodayGoals() {}

    @Test
    public void getDisplayedTomorrowGoals() {}

    @Test
    public void removeSpecificGoal() { assertTrue(true); }

    @Test
    public void getContext() { assertTrue(true); }
}