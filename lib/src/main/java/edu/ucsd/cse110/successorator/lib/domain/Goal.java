package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 *
 */
public class Goal {
    private final @NonNull Integer id;
    private final @NonNull String description;
    private boolean completed;

    public Goal(
            @NonNull Integer id,
            @NonNull String description,
            boolean completed
    ) {
        this.id = id;
        this.description = description;
        this.completed = completed;
    }

    public Integer id() {
        return id;
    }

    public @NonNull String description() {
        return description;
    }

    public boolean completed() { return completed; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return Objects.equals(id, goal.id)
                && Objects.equals(description, goal.description)
                && Objects.equals(completed, goal.completed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, completed);
    }
}




