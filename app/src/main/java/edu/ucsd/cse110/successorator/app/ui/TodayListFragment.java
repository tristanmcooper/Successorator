package edu.ucsd.cse110.successorator.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.sql.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.databinding.FragmentTodayListBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;

public class TodayListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentTodayListBinding view;
    private GoalListAdapter incompleteAdapter;
    private GoalListAdapter completeAdapter;
    private LocalDateTime currentDate;

    public TodayListFragment() {
        // Required empty public constructor
    }

    public static TodayListFragment newInstance() {
        TodayListFragment fragment = new TodayListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDate = LocalDateTime.now();
        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
        // Initialize the Adapter (with an empty list for now) for incomplete tasks
        this.incompleteAdapter = new GoalListAdapter(requireContext(), List.of(), id -> {
            activityModel.changeCompleteStatus(id);
        });
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
        activityModel.getIncompleteGoals().registerObserver(goals -> {
            if (goals == null) return;

            var todaysGoals = new ArrayList<Goal>();
            for(Goal g : goals){
                LocalDateTime goalDate = LocalDateTime.parse(g.date(), formatter);
                if(goalDate.getDayOfYear()<=currentDate.getDayOfYear()){
                    todaysGoals.add(g);
                }
            }
/*
            if (todaysGoals.size() == 0) {
                view.defaultGoals.setVisibility(View.VISIBLE);
            }
            else {
                view.defaultGoals.setVisibility(View.INVISIBLE);
            }
 */
            incompleteAdapter.clear();
            incompleteAdapter.addAll(todaysGoals); // remember the mutable copy here!
            incompleteAdapter.notifyDataSetChanged();
        });

        // Initialize the adapter for completed tasks
        this.completeAdapter = new GoalListAdapter(requireContext(), List.of(), id -> {
            activityModel.changeCompleteStatus(id);
        });
        activityModel.getCompleteGoals().registerObserver(goals -> {
            if (goals == null) return;
            var todaysGoals = new ArrayList<Goal>();
            for(Goal g : goals){
                LocalDateTime goalDate = LocalDateTime.parse(g.date(), formatter);
                if(goalDate.getDayOfYear()==currentDate.getDayOfYear()){
                    todaysGoals.add(g);
                }
            }
            completeAdapter.clear();
            completeAdapter.addAll(todaysGoals);
            completeAdapter.notifyDataSetChanged();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentTodayListBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.uncompletedGoalList.setAdapter(incompleteAdapter);
        view.completedGoalList.setAdapter(completeAdapter);

        return view.getRoot();
    }
    public void updateDate(LocalDateTime date){
        this.currentDate = date;
    }
    public LocalDateTime getDate(){
        return this.currentDate;
    }
}