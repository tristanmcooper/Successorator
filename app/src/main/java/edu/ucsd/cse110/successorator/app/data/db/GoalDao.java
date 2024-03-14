package edu.ucsd.cse110.successorator.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;

@Dao
public interface GoalDao {
    // Inserting a goal
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(GoalEntity goal);

    // Inserting a list of goals
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<GoalEntity> goals);

    // Return all goals
    @Query("SELECT * FROM goals")
    List<GoalEntity> findAll();

    // Return specific goal with id
    @Query("SELECT * FROM goals WHERE id = :id")
    GoalEntity find(int id);

    // Return all goals as LiveData
    @Query ("SELECT * FROM goals")
    LiveData<List<GoalEntity>> findAllAsLiveData();

    // Return specific goal with id as LiveData
    @Query("SELECT * FROM goals WHERE id = :id")
    LiveData<GoalEntity> findAsLiveData(int id);

    // Return all completed/uncompleted goals
    @Query("SELECT * FROM goals WHERE completed = true")
    LiveData<List<GoalEntity>> findCompleted();

    @Query("SELECT * FROM goals WHERE completed = false")
    LiveData<List<GoalEntity>> findIncomplete();

    // Overload to filter by context
    @Query("SELECT * FROM goals WHERE contextType = :contextType AND completed= :completed")
    LiveData<List<GoalEntity>> findCompleted(boolean completed, String contextType);

    @Query("SELECT * FROM goals WHERE repType <> 'Once' AND reptype <> 'pending'")
    LiveData<List<GoalEntity>> findRecurring();

    // Overload to filter by context
    @Query("SELECT * FROM goals WHERE repType <> 'Once' AND contextType = :contextType")
    LiveData<List<GoalEntity>> findRecurring(String contextType);

    // Return number of goals in database
    @Query("SELECT COUNT(*) FROM goals")
    int count();

    @Query("DELETE FROM goals")
    void clear();

    // Delete a goal from database
    @Query("DELETE FROM goals WHERE id=:id")
    void delete(int id);

    @Query("DELETE FROM goals WHERE completed=true")
    void deleteComplete();

    @Query("DELETE FROM goals WHERE id= :idPass")
    void deleteGoal(int idPass);

    @Query("SELECT * FROM goals WHERE repType = :completed")
    LiveData<List<GoalEntity>> makeTomorrow(String completed);
}
