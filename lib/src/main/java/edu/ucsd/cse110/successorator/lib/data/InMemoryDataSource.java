package edu.ucsd.cse110.successorator.lib.data;


// Import Java data structures
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import domain and util classes (once we make them)
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject; // to be replaced with SimpleSubject later?

/*
 * Will replace later in a SuccessoratorApplication.java file, but should have set up for now
 */
public class InMemoryDataSource {

    private final Map<Integer, Goal> goals
            = new HashMap<>();
    private final Map<Integer, SimpleSubject<Goal>> goalSubjects
            = new HashMap<>();
    private final SimpleSubject<List<Goal>> allGoalsSubjects
            = new SimpleSubject<>();

    public InMemoryDataSource() {
    }

    public List<Goal> getGoals() {
        return List.copyOf(goals.values());
    }

    public Goal getGoal(int id) {
        return goals.get(id);
    }

    public SimpleSubject<Goal> getGoalSubject(int id) {
        if (!goalSubjects.containsKey(id)) {
            var subject = new SimpleSubject<Goal>();
            subject.setValue(getGoal(id));
            goalSubjects.put(id, subject);
        }
        return goalSubjects.get(id);
    }

    public SimpleSubject<List<Goal>> getAllGoalsSubjects() {
        return allGoalsSubjects;
    }

    public void putGoal(Goal goal) {
        goals.put(goal.id(), goal);
        if (goalSubjects.containsKey(goal.id())) {
            goalSubjects.get(goal.id()).setValue(goal);
        }
        allGoalsSubjects.setValue(getGoals());
    }

    public void putGoals(List<Goal> goals) {
        goals.forEach(goal -> goals.add(goal.id(), goal));
        allGoalsSubjects.setValue(getGoals());
    }

    public final static List<Goal> DEFAULT_GOALS = List.of(
            new Goal(0, "Goal 0: Get Lettuce", false, LocalDateTime.now().toString(), "", ""),
            new Goal(1, "Goal 1: Get Tomato", false, LocalDateTime.now().toString(), "", ""),
            new Goal(2, "Goal 2: Finish Project PLEASE", false, LocalDateTime.now().toString(), "", "")
    );

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        for (Goal goal : DEFAULT_GOALS) {
            data.putGoal(goal);
        }
        return data;
    }
}