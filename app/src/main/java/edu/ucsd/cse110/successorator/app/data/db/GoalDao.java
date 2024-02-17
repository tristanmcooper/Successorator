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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(GoalEntity goal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<GoalEntity> goals);

    @Query("SELECT * FROM goals")
    List<GoalEntity> findAll();

    @Query("SELECT * FROM goals WHERE id = :id")
    GoalEntity find(int id);

    @Query ("SELECT * FROM goals")
    LiveData<List<GoalEntity>> findAllAsLiveData();

    @Query("SELECT * FROM goals WHERE id = :id")
    LiveData<GoalEntity> findAsLiveData(int id);

    @Query("SELECT * FROM goals WHERE completed = :completed")
    List<GoalEntity> completed(boolean completed);

    @Query("SELECT COUNT(*) FROM goals")
    int count();

    @Query("DELETE FROM goals WHERE id=:id")
    void delete(int id);

}
