package edu.ucsd.cse110.successorator.app.data.db;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
                if (g.getCreatedById() != null) {
                    createNextInstance(g.getCreatedById(), g);
                }
                goalDao.deleteGoal(g.id());
            }
        }
    }

    public void createNextInstance(int parentId, Goal currGoal) {
        Goal parent = goalDao.find(parentId).toGoal();
        LocalDateTime currGoalDate = LocalDateTime.parse(currGoal.date());
        LocalDateTime parentDate = LocalDateTime.parse(parent.date());

        int maxId = getMaxId();
        switch (parent.repType()) {
            case "Daily":
                Goal newDailyGoal = new Goal(maxId + 1, currGoal.description(), false, LocalDateTime.parse(currGoal.date()).plusDays(2).toString(), "Once", currGoal.getContextType(), parentId);
                add(newDailyGoal);
                break;
            case "Weekly":
                Goal newWeeklyGoal = new Goal(maxId + 1, currGoal.description(), false, LocalDateTime.parse(currGoal.date()).plusDays(7).toString(), "Once", currGoal.getContextType(), parentId);
                add(newWeeklyGoal);
                break;
            case "Monthly":
                Goal newMonthlyGoal;
                // Get weekNum and day of week
                int weekNum = currGoalDate.getDayOfMonth() / 7;
                if (currGoalDate.getDayOfMonth() % 7 != 0) {
                    weekNum += 1;
                }
                LocalDateTime nextRecurrenceMonthDate;
                // If not the fifth 'day-of-week', nothing to worry about. Calculate as usual
                if (weekNum != 5) {
                    LocalDateTime firstDayOfNextMonth = LocalDateTime.of(currGoalDate.getYear(), currGoalDate.getMonthValue() + 1, 1, 2, 0, 0);
                    String dayOfFirst = firstDayOfNextMonth.getDayOfWeek().toString();

                    // Offset to find date of first occurrence of day in next month
                    HashMap<String, Integer> valueOfWeekDays = new HashMap<>() {{
                        put("MONDAY", 1);
                        put("TUESDAY", 2);
                        put("WEDNESDAY", 3);
                        put("THURSDAY", 4);
                        put("FRIDAY", 5);
                        put("SATURDAY", 6);
                        put("SUNDAY", 7);
                    }};

                    nextRecurrenceMonthDate = firstDayOfNextMonth.plusDays(
                            ((weekNum - 1) * 7) //-1 because dayOfFirst is the first 'day-of-week' in the next month
                                    + Math.abs(valueOfWeekDays.get(currGoalDate.getDayOfWeek().toString())
                                    - valueOfWeekDays.get(dayOfFirst))
                    );

                } else { // if fifth 'day-of-week'
                    // Currently is extended to first of next month
                    if (currGoalDate.getDayOfMonth() <= 7) {
                        // Does this month have the 5th occurrence?
                        if (currGoalDate.plusDays(28).getMonth() != currGoalDate.getMonth()) {
                            nextRecurrenceMonthDate = currGoalDate.plusDays(28);
                        } else { // Doesn't have 5th occurrence
                            nextRecurrenceMonthDate = currGoalDate.plusDays(35);
                        }
                    } else { // Currently not extended to next month
                        nextRecurrenceMonthDate = currGoalDate.plusDays(35);
                    }
                }

                newMonthlyGoal = new Goal(maxId + 1, currGoal.description(), false, nextRecurrenceMonthDate.toString(), "Once", currGoal.getContextType(), parentId);
                add(newMonthlyGoal);
                break;
            case "Yearly":
                Goal newYearlyGoal;

                // Check if selected date is Feb 29
                if (parentDate.getMonthValue() == 2 && parentDate.getDayOfMonth() == 29) {
                    // Is currentDate Feb 29?
                    if (currGoalDate.getMonthValue() == 2 && currGoalDate.getDayOfMonth() == 29) {
                        // If this one is leap year, next one is non leap year
                        newYearlyGoal = new Goal(maxId + 1, currGoal.description(), false, currGoalDate.plusYears(1).plusDays(1).toString(), "Once", currGoal.getContextType(), parentId);
                    } else { // Currently March 1
                        LocalDateTime nextRecurrenceYearDate;
                        // Is next year leap year?
                        try {
                            nextRecurrenceYearDate = LocalDateTime.of(currGoalDate.getYear() + 1, 2, 29, 2, 0, 0, 0);
                        } catch (Exception e) { // Exception thrown if Feb 29 not in next year, then next year is still March 1
                            nextRecurrenceYearDate = currGoalDate.plusYears(1);
                        }
                        newYearlyGoal = new Goal(maxId + 1, currGoal.description(), false, nextRecurrenceYearDate.toString(), "Once", currGoal.getContextType(), parentId);
                    }
                } else { // All other dates have no leap year issues
                    newYearlyGoal = new Goal(maxId + 1, currGoal.description(), false, currGoalDate.plusYears(1).toString(), "Once", currGoal.getContextType(), parentId);
                }
                add(newYearlyGoal);
                break;
            default:
                break;
        }
    }

    public int getMaxId() { return goalDao.getMaxId(); }

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
    
    public List<Goal> findAllCreatedById(int id){
        return goalDao.findAllCreatedById(id).stream()
                .map(GoalEntity::toGoal)
                .collect(Collectors.toList());
    }
}