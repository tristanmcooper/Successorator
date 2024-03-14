package edu.ucsd.cse110.successorator.app;
import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.app.data.db.GoalEntity;
import edu.ucsd.cse110.successorator.app.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;

public class MainViewModel extends ViewModel{
    private final RoomGoalRepository goalRepository;
    private SimpleSubject<List<Goal>> incompleteGoals;
    private SimpleSubject<List<Goal>> completeGoals;
    private SimpleSubject<List<Goal>> recurringGoals;
    private LocalDateTime currentDate;
    //For testing US1
    private ArrayList<Goal> displayedTodayGoals;
    private ArrayList<Goal> displayedTomorrowGoals;
    private String contextType = "N/A";
    private int idCount = 0;

    //basically grabs the database
    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                        assert  app != null;
                        return new MainViewModel((RoomGoalRepository) app.getGoalRepository());
                    });

    //when making a new mainviewmodel ^ called up there
    public MainViewModel(RoomGoalRepository goalRepository) {
         
        //creating simple subjects here so that we can observe them
        this.goalRepository = goalRepository;
        this.incompleteGoals = new SimpleSubject<>();
        this.completeGoals = new SimpleSubject<>();
        this.recurringGoals = new SimpleSubject<>();

        this.idCount = getCount();

        goalRepository.findIncomplete().registerObserver(goals -> {
            if (goals == null) return;
            var newIncompleteGoals = goals.stream()
                    .sorted(Comparator.comparingInt(Goal::id))
                    .collect(Collectors.toList());
            incompleteGoals.setValue(newIncompleteGoals);
        });

        goalRepository.findCompleted().registerObserver(goals -> {
            if (goals == null) return;
            var newCompleteGoals = goals.stream()
                    .sorted(Comparator.comparingInt(Goal::id))
                    .collect(Collectors.toList());
            completeGoals.setValue(newCompleteGoals);
        });

        goalRepository.findRecurring().registerObserver(goals -> {
            if (goals == null) return;

            var newRecurringGoals = goals.stream()
                    .filter(goal -> !goal.date().isEmpty())
                    .sorted(Comparator.comparing(goal -> LocalDateTime.parse(goal.date())))
                    .collect(Collectors.toList());
            recurringGoals.setValue(newRecurringGoals);
        });
    }

    //Probably just for testing, might be violating SRP idk
    public void addGoal(Goal goal){
        this.idCount += 1;
        goalRepository.add(goal);
    }
    public void setContextFilterType(String contextType){
        this.contextType = contextType;
        refreshDatabase();
    }

    public void removeSpecificGoal(int id){goalRepository.remove(id);}


    //the getters so that other classes can watch when the db changes so they can upd UI
    public int getCount(){ 
        return goalRepository.count(); 
    }

    public int getMaxId() {
        return this.idCount;
    }

    public Goal find(int id) {
        return goalRepository.findNonLive(id);
    }

    public void changeCompleteStatus(int id){
        goalRepository.changeCompleted(id);
    }
    public SimpleSubject<List<Goal>> getIncompleteGoals(){
        return incompleteGoals;
    }

    public SimpleSubject<List<Goal>> getCompleteGoals(){
        return completeGoals;
    }

    public SimpleSubject<List<Goal>> getRecurringGoals(){
        return recurringGoals;
    }

    public void deleteCompleted(){
        goalRepository.deleteCompleted();
    }
    public void updateModelCurrentDate(LocalDateTime datetime){
            this.currentDate = datetime;
            //notify
    }

    public LocalDateTime getCurrentDate(){
        return this.currentDate;
    }

    public void changeToTodayView(int id, boolean isComplete){
        goalRepository.changeToTodayView(id, isComplete, currentDate);
    }

    public void changeToTomorrowView(int id, boolean isComplete){
        goalRepository.changeToTodayView(id, isComplete, currentDate.plusDays(1));
    }
    public void refreshDatabase(){
        Log.d("Database refresh", "refreshing database");
        addGoal(new Goal(-1,
                "",
                false,
                LocalDateTime.now().withHour(2).withMinute(0).withSecond(0).withNano(0).toString(),
                "Once",
                "Work"));
        removeSpecificGoal(-1);
    }

    public RoomGoalRepository getRepo(){ return this.goalRepository;}

    //For testing us1
    public void setDisplayedTodayGoals(ArrayList<Goal> temp) {
        displayedTodayGoals = temp;
    }

    public ArrayList<Goal> getDisplayedTodayGoals(){
        return displayedTodayGoals;
    }

    public void setDisplayedTomorrowGoals(ArrayList<Goal> temp) {
        displayedTomorrowGoals = temp;
    }

    public ArrayList<Goal> getDisplayedTomorrowGoals(){
        return displayedTomorrowGoals;
    }

    public String getContext() {
        return this.contextType;
    }
}