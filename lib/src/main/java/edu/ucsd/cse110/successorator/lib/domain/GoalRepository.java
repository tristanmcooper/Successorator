package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject; // change to Subject later?

public class GoalRepository {
    private final InMemoryDataSource dataSource;

    public GoalRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Integer count() {
        return dataSource.getGoals().size();
    }

    public SimpleSubject<Goal> find(int id) {
        return dataSource.getGoalSimpleSubject(id);
    }

    public SimpleSubject<List<Goal>> findAll() {
        return dataSource.getAllGoalsSimpleSubject();
    }

    public void save(Goal Goal) {
        dataSource.putGoal(Goal);
    }
}
