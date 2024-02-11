package edu.ucsd.cse110.successorator.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
@Entity(tableName = "goals")
public class GoalEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "completed")
    public boolean completed = false;

    GoalEntity(@NonNull String description) {
        this.description = description;
    }
}
