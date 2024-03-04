package edu.ucsd.cse110.successorator.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.successorator.lib.domain.Goal;

@Entity(tableName = "goals")
public class GoalEntity {
    // Use ID as primary key
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "completed")
    public boolean completed;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "repType")
    public int repType;
    /*
    0 - one time goal
    1 - Daily goal
    2 - Weekly goal
    3 - Monthly goal
    4 - Yearly goal
     */


    // Constructor for GoalEntity
    GoalEntity(@NonNull int id, @NonNull String description, @NonNull boolean completed, String date, int repType) {
        this.id = id;
        this.description = description;
        this.completed = completed;
        this.date = date; //Can be null if created from pending view
        this.repType = repType;
    }

    // Change Goal object into GoalEntity object
    public static GoalEntity fromGoal(@NonNull Goal goal) {
        var goalEntity = new GoalEntity(goal.id(), goal.description(), goal.completed(), goal.date(), goal.repType());
        return goalEntity;
    }

    // Change GoalEntity object into Goal object
    public @NonNull Goal toGoal() {
        return new Goal(id, description, completed, date, 1);
    }
}
