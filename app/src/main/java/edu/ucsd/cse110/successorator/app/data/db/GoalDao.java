package edu.ucsd.cse110.successorator.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;

import java.util.List;

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
    @Query("SELECT * FROM goals WHERE completed = :completed")
    List<GoalEntity> completed(boolean completed);

    // Return number of goals in database
    @Query("SELECT COUNT(*) FROM goals")
    int count();

    // Delete a goal from database
    @Query("DELETE FROM goals WHERE id=:id")
    void delete(int id);

}
