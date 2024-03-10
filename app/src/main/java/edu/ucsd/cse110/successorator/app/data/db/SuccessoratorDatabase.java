package edu.ucsd.cse110.successorator.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {GoalEntity.class}, version = 1)
public abstract class SuccessoratorDatabase extends RoomDatabase {
    public abstract GoalDao goalDao();
    // Migration code
}
