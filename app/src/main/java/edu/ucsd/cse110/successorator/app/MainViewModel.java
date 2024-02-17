package edu.ucsd.cse110.successorator.app;
import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.app.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;

public class MainViewModel extends ViewModel{
    private static final String LOG_TAG = "MainViewModel";
    private final RoomGoalRepository goalRepository;
    private SimpleSubject<List<Goal>> orderedGoals;
    private MainActivity mainActivity;
    private final SimpleSubject<String> displayedText;
    private SuccessoratorApplication app;

    //basically grabs the database.
    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                        assert  app != null;
                        return new MainViewModel((RoomGoalRepository) app.getGoalRepository());
                    });


    public MainViewModel(RoomGoalRepository goalRepository) {
        this.goalRepository = goalRepository;
        this.orderedGoals = new SimpleSubject<>();
        this.displayedText = new SimpleSubject<>();

        goalRepository.findAll().registerObserver(goals -> {
            if (goals == null) return;

            var newOrderedGoals = goals.stream()
                    .sorted(Comparator.comparingInt(Goal::id))
                    .collect(Collectors.toList());
            orderedGoals.setValue(newOrderedGoals);
        });
    }
    //Probably just for testing, might be violating SRP idk
    public void addGoal(Goal goal){
        goalRepository.add(goal);
    }

    public int getCount(){ return goalRepository.count(); }

    public SimpleSubject<List<Goal>> getOrderedGoals(){
        return orderedGoals;
    }

    public SimpleSubject<String> getDisplayedText() {
        return displayedText;
    }
}