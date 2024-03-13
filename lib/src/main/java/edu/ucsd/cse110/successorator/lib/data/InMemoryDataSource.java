package edu.ucsd.cse110.successorator.lib.data;


// Import Java data structures
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private Map<Integer, Goal> goals
            = new HashMap<>();
    private Map<Integer, SimpleSubject<Goal>> goalSubjects
            = new HashMap<>();
    private SimpleSubject<List<Goal>> allGoalsSubjects
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

    public SimpleSubject<List<Goal>> getCompleted(Boolean completed) {
        SimpleSubject<List<Goal>> ret = new SimpleSubject<>();
        List<Goal> retList = new ArrayList<>();
        for (Map.Entry<Integer, SimpleSubject<Goal>> entry : goalSubjects.entrySet()) {
            if (entry.getValue().getValue().completed() == completed) {
                retList.add(entry.getValue().getValue());
            }
        }
        ret.setValue(retList);
        return ret;
    }

    public SimpleSubject<List<Goal>> getCompleted(Boolean completed, String context) {
        SimpleSubject<List<Goal>> ret = new SimpleSubject<>();
        List<Goal> retList = new ArrayList<>();
        for (Map.Entry<Integer, SimpleSubject<Goal>> entry : goalSubjects.entrySet()) {
            Goal g = entry.getValue().getValue();
            if (g.completed() == completed && g.contextType().equals(context)) {
                retList.add(g);
            }
        }
        ret.setValue(retList);
        return ret;
    }

    public SimpleSubject<List<Goal>> getRecurring() {
        SimpleSubject<List<Goal>> ret = new SimpleSubject<>();
        List<Goal> retList = new ArrayList<>();
        for (Map.Entry<Integer, SimpleSubject<Goal>> entry : goalSubjects.entrySet()) {
            if (!entry.getValue().getValue().repType().equals("Once")) {
                retList.add(entry.getValue().getValue());
            }
        }
        ret.setValue(retList);
        return ret;
    }

    public SimpleSubject<List<Goal>> getRecurring(String context) {
        SimpleSubject<List<Goal>> ret = new SimpleSubject<>();
        List<Goal> retList = new ArrayList<>();
        for (Map.Entry<Integer, SimpleSubject<Goal>> entry : goalSubjects.entrySet()) {
            Goal g = entry.getValue().getValue();
            if (!g.repType().equals("Once") && g.contextType().equals(context)) {
                retList.add(g);
            }
        }
        ret.setValue(retList);
        return ret;
    }

    public void changeCompleted(int id) {
        Goal g = goalSubjects.get(id).getValue();
        g.changeCompleted();
        SimpleSubject<Goal> updatedGoal = new SimpleSubject<>();
        updatedGoal.setValue(g);
        goalSubjects.put(g.id(), updatedGoal);

        g = goals.get(id);
        g.changeCompleted();
        goals.put(g.id(), g);
        allGoalsSubjects.setValue(getGoals());
    }

    public void deleteCompleted() {
        for (Map.Entry<Integer, SimpleSubject<Goal>> entry : goalSubjects.entrySet()) {
            Goal g = entry.getValue().getValue();
            if (g.completed()) {
                goalSubjects.remove(entry.getValue());
            }
        }
        allGoalsSubjects.setValue(getGoals());
    }

    public void putGoal(Goal goal) {
        goals.put(goal.id(), goal);
        if (goalSubjects.containsKey(goal.id())) {
            goalSubjects.get(goal.id()).setValue(goal);
        }
        allGoalsSubjects.setValue(getGoals());
    }

    public void putGoals(List<Goal> goals) {
        goals.forEach(goal -> {
            this.goals.put(goal.id(), goal);
            SimpleSubject<Goal> goalSubject = new SimpleSubject<>();
            goalSubject.setValue(goal);
            goalSubjects.put(goal.id(), goalSubject);
        });
        allGoalsSubjects.setValue(getGoals());
    }

    public void clear() {
        goals.clear();
        goalSubjects.clear();
        allGoalsSubjects.setValue(getGoals());
    }

    public void changeToTodayComplete(int id) {
        Goal g = goals.get(id);
        LocalDateTime date = LocalDateTime.parse(g.date());
        Goal copyOfGoal = new Goal(g.id(), g.description(), true, date.minusDays(1).toString(), g.repType(), g.contextType());
        putGoal(copyOfGoal);
    }
}