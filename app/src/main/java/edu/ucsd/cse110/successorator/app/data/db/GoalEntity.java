package edu.ucsd.cse110.successorator.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.successorator.lib.domain.Goal;

@Entity(tableName = "goals")
public class GoalEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "completed")
    public boolean completed;

    GoalEntity(@NonNull int id, @NonNull String description, @NonNull boolean completed) {
        this.id = id;
        this.description = description;
        this.completed = completed;
    }

    public static GoalEntity fromGoal(@NonNull Goal goal) {
        var goalEntity = new GoalEntity(goal.id(), goal.description(), goal.completed());
        return goalEntity;
    }

    public @NonNull Goal toGoal() {
        return new Goal(id, description, completed);
    }
}
