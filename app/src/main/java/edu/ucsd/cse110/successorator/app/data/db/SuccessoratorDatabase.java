package edu.ucsd.cse110.successorator.app.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database
public abstract class SuccessoratorDatabase extends RoomDatabase {
    public abstract GoalDao goalDao();
}
