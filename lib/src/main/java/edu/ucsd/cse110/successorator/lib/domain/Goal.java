package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 */
public class Goal {
    private final @Nullable Integer id;
    private final @Nullable String title;

    // add `completed` bool member later?
    // add `date` member?

    public Goal(
            @Nullable Integer id,
            @Nullable String goal
    ) {
        this.id = id;
        this.title = goal;
    }

    @Nullable
    public Integer id() {
        return id;
    }

    @Nullable
    public String title() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return Objects.equals(id, goal.id) && Objects.equals(title, goal.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}




