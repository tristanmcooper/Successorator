package edu.ucsd.cse110.successorator.app;
import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.app.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.app.ui.GoalListAdapter;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;

public class MainViewModel extends ViewModel{
    private static final String LOG_TAG = "MainViewModel";
    public RoomGoalRepository goalRepository;

    //private final SimpleSubject<List<Integer>> goalOrdering;
    //private final SimpleSubject<List<Goal>> orderedGoals;
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

        //try to just make the thing a fragment, so we can actually call the adapter??
        //LE OFF!!! requireContext from SEcards works because its from fragment extension. here it is nhot
        //var adapter = new GoalListAdapter(mainActivity.getApplicationContext(), List.of());
        //var displayUpdater = new DisplayUpdater(mainActivity);

        //this.goalOrdering = new SimpleSubject<>();
        //this.orderedGoals = new SimpleSubject<>();
        this.orderedGoals = new SimpleSubject<>();
        this.displayedText = new SimpleSubject<>();

        goalRepository.findAll().registerObserver(goals -> {
            if (goals == null) return;

            var newOrderedGoals = goals.stream()
                    .sorted(Comparator.comparingInt(Goal::id))
                    .collect(Collectors.toList());
            orderedGoals.setValue(newOrderedGoals);
        });


//        goalRepository.findAll().registerObserver( goals -> {
//            if (goals == null) return;
//            adapter.clear();
//            adapter.addAll(new ArrayList<>(goals)); // remember the mutable copy here!
//            adapter.notifyDataSetChanged();
//        });

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