package edu.ucsd.cse110.successorator.app;

import android.app.Application;

import androidx.room.Room;

import java.time.LocalDateTime;
import java.util.List;

import edu.ucsd.cse110.successorator.app.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.app.data.db.SuccessoratorDatabase;
import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;

public class SuccessoratorApplication extends Application {
    private InMemoryDataSource dataSource;
    private GoalRepository goalRepository;

    //@Override
    public void onCreate() {
        super.onCreate();

        // Build database
        var database = Room.databaseBuilder(
                getApplicationContext(),
                SuccessoratorDatabase.class,
                "successorator-database"
            )
            .allowMainThreadQueries()
            .build();

        this.goalRepository = new RoomGoalRepository(database.goalDao());

        // Populate with some initial data
        var sharedPreferences = getSharedPreferences("successorator", MODE_PRIVATE);
        var isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        LocalDateTime currDateTime = LocalDateTime.now().withHour(2).withMinute(0).withSecond(0).withNano(0);
        // Default goals for testing purposes
        List<Goal> DEFAULT_GOALS = List.of(
            new Goal(1, "Goal 1", false, currDateTime.toString(), "Once", "H", null),
            new Goal(2, "Goal 2", false, currDateTime.toString(), "Once", "W", null),
            new Goal(3, "Goal 3", false, currDateTime.toString(), "Once", "S", null),
            new Goal(4, "Goal 4", false, LocalDateTime.of(2024, 2, 29, 2, 0, 0).toString(), "Yearly", "E", null),
            new Goal(5, "Goal 4", false, LocalDateTime.of(2025, 3, 1, 2, 0, 0).toString(), "Once", "E", 4)
        );

        // Populate database with default values
        if (isFirstRun && database.goalDao().count() == 0) {


            sharedPreferences.edit()
                .putBoolean("isFirstRun", false)
                .apply();
        }
    }

    public GoalRepository getGoalRepository() {
        return goalRepository;
    }
}







