package edu.ucsd.cse110.successorator.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    @Override
    public Goal findNonLive(int id) {
        var goalEntity = goalDao.find(id);
        return goalEntity.toGoal();
    }

    @Override
    public Subject<List<Goal>> findCompleted() {
        var entitiesLiveData = goalDao.findCompleted();
        var goalsLiveData = Transformations.map(entitiesLiveData, entities -> {
            return entities.stream()
                    .map(GoalEntity::toGoal)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(goalsLiveData);
    }

    public Subject<List<Goal>> findIncomplete() {
        var entitiesLiveData = goalDao.findIncomplete();
        var goalsLiveData = Transformations.map(entitiesLiveData, entities -> {
            return entities.stream()
                    .map(GoalEntity::toGoal)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(goalsLiveData);
    }

    public Subject<List<Goal>> findCompleted(Boolean completed, String contextType) {
        var entitiesLiveData = goalDao.findCompleted(completed,contextType);
        var goalsLiveData = Transformations.map(entitiesLiveData, entities -> {
            return entities.stream()
                    .map(GoalEntity::toGoal)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(goalsLiveData);
    }

    @Override
    public Subject<List<Goal>> findRecurring() {
        var entitiesLiveData = goalDao.findRecurring();
        var goalsLiveData = Transformations.map(entitiesLiveData, entities -> {
            return entities.stream()
                    .map(GoalEntity::toGoal)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(goalsLiveData);
    }

    public Subject<List<Goal>> findRecurring(String contextType) {
        var entitiesLiveData = goalDao.findRecurring(contextType);
        var goalsLiveData = Transformations.map(entitiesLiveData, entities -> {
            return entities.stream()
                    .map(GoalEntity::toGoal)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(goalsLiveData);
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
        var goalEntities = goalDao.findAll();
        ArrayList<Goal> goals = new ArrayList<>();
        for (GoalEntity entity : goalEntities) {
            goals.add(entity.toGoal());
        }
        this.setValue(goals);
        this.notifyObservers();
    }


    @Override
    public void changeCompleted(int id) {
        var goal = goalDao.find(id).toGoal();
        goalDao.insert(new GoalEntity(goal.id(), goal.description(), !goal.completed(), goal.date(), goal.repType(), goal.getContextType(), goal.getCreatedById()));
        this.notifyObservers();
    }

    @Override
    public int count() {
        return goalDao.count();
    }

    @Override
    public void deleteCompleted() {
        goalDao.deleteCompleted();
        List<Goal> goals = goalDao.findAll().stream()
                .map(GoalEntity::toGoal)
                .collect(Collectors.toList());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
        LocalDateTime currDate = LocalDateTime.now().withHour(2).withMinute(0).withSecond(0).withNano(0);
        for (Goal g : goals) {
            LocalDateTime goalDate = null;
            if (g.date() != "") {
                goalDate = LocalDateTime.parse(g.date());
            }
            if (g.completed() && currDate.isAfter(goalDate)) {
                goalDao.deleteGoal(g.id());
            }
        }
    }

    @Override
    public void clear() {
        goalDao.clear();
    }

    @Override
    public void remove(int id) {
        goalDao.delete(id);
    }

    @Override
    public void generateTomorrow() {
        LiveData<List<GoalEntity>> list = goalDao.makeTomorrow("Daily");
    }

    @Override
    public void changeToTodayView(int id, boolean isComplete, LocalDateTime currentDate) {
        Goal temp = goalDao.find(id).toGoal();
        Goal copy = new Goal(temp.id(), temp.description(), isComplete, currentDate.withHour(2).withMinute(0).withSecond(0).withNano(0).toString(), temp.repType(), temp.getContextType(),temp.getCreatedById());
        remove(id);
        add(copy);
    }

    @Override
    public Subject<List<Goal>> findAllCreatedById(int id){
        var entitiesLiveData = goalDao.findAllCreatedById(id);
        var goalsLiveData = Transformations.map(entitiesLiveData, entities -> {
            return entities.stream()
                    .map(GoalEntity::toGoal)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(goalsLiveData);

    }

}