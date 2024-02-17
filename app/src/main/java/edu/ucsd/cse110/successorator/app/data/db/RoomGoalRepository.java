package edu.ucsd.cse110.successorator.app.data.db;

import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.app.util.*;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.util.RepositorySubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class RoomGoalRepository extends RepositorySubject implements GoalRepository {
    private final GoalDao goalDao;
    public RoomGoalRepository(GoalDao goalDao) {
        this.goalDao = goalDao;
    }

    @Override
    public Subject<Goal> find(int id) {
        var entityLiveData = goalDao.findAsLiveData(id);
        var goalLiveData = Transformations.map(entityLiveData, GoalEntity::toGoal);
        return new LiveDataSubjectAdapter<>(goalLiveData);
    }

    @Override
    public Subject<List<Goal>> findAll() {
        var entitiesLiveData = goalDao.findAllAsLiveData();
        var goalsLiveData = Transformations.map(entitiesLiveData, entities -> {
            return entities.stream()
                    .map(GoalEntity::toGoal)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(goalsLiveData);
    }
    public ArrayList<Goal> tempFindAll(){
        var goalEntities = goalDao.findAll();
        ArrayList<Goal> ans = new ArrayList<>();
        for(GoalEntity entity: goalEntities){
            ans.add(entity.toGoal());
        }
        return ans;
    }

    @Override
    public void save(Goal goal) {
        goalDao.insert(GoalEntity.fromGoal(goal));
    }

    @Override
    public void save(List<Goal> goals) {
        var entities = goals.stream()
                .map(GoalEntity::fromGoal)
                .collect(Collectors.toList());
        goalDao.insert(entities);
    }

    @Override
    public void add(Goal goal) {
        goalDao.insert(GoalEntity.fromGoal(goal));
        this.setValue(tempFindAll());
        this.notifyObservers();
    }

    @Override public int count(){
        return goalDao.count();
    }

    @Override
    public void remove(int id) {
        goalDao.delete(id);
    }
}
