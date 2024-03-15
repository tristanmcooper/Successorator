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

        currentDate = LocalDateTime.now().withHour(2).withMinute(0).withSecond(0).withNano(0);
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

        activityModel.getIncompleteGoals().removeAllObservers();
        activityModel.getIncompleteGoals().registerObserver(goals -> {
            System.out.println("Today Incomplete Goals Observer");
            System.out.println(currentDate);
            if (goals == null) return;

            var todaysGoals = new ArrayList<Goal>();
            for(Goal g : goals){
                LocalDateTime goalDate = null;
                if (!g.date().equals("")){
                    goalDate = LocalDateTime.parse(g.date(), formatter);
                }
                if (goalDate != null
                        && (goalDate.isBefore(currentDate) || goalDate.equals(currentDate))
                        && (activityModel.getContext().equals("N/A") || g.getContextType().equals(activityModel.getContext()))
                        && !(g.repType().equals("Daily") || g.repType().equals("Weekly") || g.repType().equals("Monthly") || g.repType().equals("Yearly"))) {
                    todaysGoals.add(g);
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

        activityModel.getCompleteGoals().removeAllObservers();
        activityModel.getCompleteGoals().registerObserver(goals -> {
            System.out.println("Today Complete Goals Observer");
            if (goals == null) return;

            var todaysGoals = new ArrayList<Goal>();
            for(Goal g : goals){
                LocalDateTime goalDate = null;
                if (!g.date().equals("")){
                    goalDate = LocalDateTime.parse(g.date(), formatter);
                }
                if (goalDate != null
                        && (goalDate.isBefore(currentDate) || goalDate.equals(currentDate))
                        && (activityModel.getContext().equals("N/A") || g.getContextType().equals(activityModel.getContext()))
                        && !(g.repType().equals("Daily") || g.repType().equals("Weekly") || g.repType().equals("Monthly") || g.repType().equals("Yearly"))) {
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

    @Override
    public void onStop() {
        super.onStop();
        activityModel.getIncompleteGoals().removeAllObservers();
        activityModel.getCompleteGoals().removeAllObservers();
    }

    public void updateDate(LocalDateTime date){
        this.currentDate = date;
    }
    public LocalDateTime getDate(){
        return this.currentDate;
    }
}