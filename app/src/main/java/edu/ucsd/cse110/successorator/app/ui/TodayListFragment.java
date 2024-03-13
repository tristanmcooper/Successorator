package edu.ucsd.cse110.successorator.app.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.databinding.FragmentTodayListBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

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



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentTodayListBinding binding = FragmentTodayListBinding.inflate(inflater, container, false);
        this.view = binding;

        currentDate = LocalDateTime.now();
        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
        // Initialize the Adapter (with an empty list for now) for incomplete tasks
        this.incompleteAdapter = new GoalListAdapter(requireContext(), List.of(), id -> {
            activityModel.changeCompleteStatus(id);
        }, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.getDefault());

        activityModel.getIncompleteGoals().registerObserver(goals -> {
            if (goals == null) return;

            var todaysGoals = new ArrayList<Goal>();
            for(Goal g : goals){
                LocalDateTime goalDate = null;
                if (!g.date().equals("")){
                    goalDate = LocalDateTime.parse(g.date(), formatter);
                }
                if(!(goalDate ==null) && goalDate.getDayOfYear()<=currentDate.getDayOfYear()){
                    todaysGoals.add(g);
                    Log.d("TodayListFrag", "is context here: " + g.getContextType());

                }
            }

            if (view.defaultGoals != null) {
                // Set defaultGoals visibility
                if (todaysGoals.size() == 0) {
                    view.defaultGoals.setVisibility(View.VISIBLE);
                } else {
                    view.defaultGoals.setVisibility(View.INVISIBLE);
                }
            } else {
                System.out.println("defaultGoals view is null");
            }

            incompleteAdapter.clear();
            incompleteAdapter.addAll(todaysGoals); // remember the mutable copy here!
            activityModel.setDisplayedTodayGoals(todaysGoals);
            incompleteAdapter.notifyDataSetChanged();
        });

        // Initialize the adapter for completed tasks
        this.completeAdapter = new GoalListAdapter(requireContext(), List.of(), id -> {
            activityModel.changeCompleteStatus(id);
        }, 0);
        activityModel.getCompleteGoals().registerObserver(goals -> {
            if (goals == null) return;
            var todaysGoals = new ArrayList<Goal>();
            for(Goal g : goals){
                LocalDateTime goalDate = null;
                if (!g.date().equals("")){
                    goalDate = LocalDateTime.parse(g.date(), formatter);
                }
                if(!(goalDate==null) && goalDate.getDayOfYear()<=currentDate.getDayOfYear()){
                    todaysGoals.add(g);
                }
            }
            completeAdapter.clear();
            completeAdapter.addAll(todaysGoals);
            completeAdapter.notifyDataSetChanged();
        });

        // Set the adapter on the ListView
        view.uncompletedGoalList.setAdapter(incompleteAdapter);
        view.completedGoalList.setAdapter(completeAdapter);
        return binding.getRoot();
    }

    public void updateDate(LocalDateTime date){
        this.currentDate = date;
    }
    public LocalDateTime getDate(){
        return this.currentDate;
    }
}