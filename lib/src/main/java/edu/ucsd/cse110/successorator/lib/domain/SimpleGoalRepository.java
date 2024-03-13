package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.util.Subject;
public class SimpleGoalRepository implements GoalRepository {
    private final InMemoryDataSource dataSource;

    public SimpleGoalRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Subject<Goal> find(int id) {
        return dataSource.getGoalSubject(id);
    }

    @Override
    public Subject<List<Goal>> findAll() {
        return dataSource.getAllGoalsSubjects();
    }

    @Override
    public Subject<List<Goal>> findCompleted(Boolean completed) {
        return dataSource.getCompleted(completed);
    }

    @Override
    public Subject<List<Goal>> findCompleted(Boolean completed, String context) {
        return dataSource.getCompleted(completed, context);
    }

    @Override
    public Subject<List<Goal>> findRecurring() {
        return dataSource.getRecurring();
    }

    @Override
    public Subject<List<Goal>> findRecurring(String context) {
        return dataSource.getRecurring(context);
    }

    @Override
    public void changeCompleted(int id) {
        dataSource.changeCompleted(id);
    }

    @Override
    public void deleteCompleted() {
        dataSource.deleteCompleted();
    }

    @Override
    public void save(Goal goal) {
        dataSource.putGoal(goal);
    }

    @Override
    public void save(List<Goal> goals) {
        dataSource.putGoals(goals);
    }

    @Override
    public void add(Goal goal) {
        dataSource.putGoal(goal);
    }

    @Override
    public void clear() {
        dataSource.clear();
    }

    @Override
    public void remove (int id) {return;}

    @Override
    public int count(){return 0;}

    @Override
    public void changeToTodayViewComplete(int id) {
        dataSource.changeToTodayComplete(id);
    }
}