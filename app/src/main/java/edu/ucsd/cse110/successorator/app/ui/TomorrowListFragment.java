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
import edu.ucsd.cse110.successorator.app.databinding.FragmentTomorrowListBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class TomorrowListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentTomorrowListBinding view;
    private GoalListAdapter incompleteAdapter;
    private GoalListAdapter completeAdapter;
    private LocalDateTime currentDate;

    public TomorrowListFragment() {
        // Required empty public constructor
    }

    public static TomorrowListFragment newInstance() {
        TomorrowListFragment fragment = new TomorrowListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentTomorrowListBinding binding = FragmentTomorrowListBinding.inflate(inflater, container, false);
        this.view = binding;

        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
        currentDate = activityModel.getCurrentDate().plusDays(1);

        // Initialize the Adapter (with an empty list for now) for incomplete tasks
        this.incompleteAdapter = new GoalListAdapter(requireContext(), List.of(), id -> {
            activityModel.changeCompleteStatus(id);
        }, 1);
        this.completeAdapter = new GoalListAdapter(requireContext(), List.of(), id -> {
            activityModel.changeCompleteStatus(id);
        }, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.getDefault());

        activityModel.getIncompleteGoals().removeAllObservers();
        activityModel.getIncompleteGoals().registerObserver(goals -> {
            System.out.println("Tomorrow Incomplete Goals Observer");
            if (goals == null) return;

            var tmrwGoals = new ArrayList<Goal>();
            for(Goal g : goals){
                LocalDateTime goalDate = null;
                if(!g.date().equals("")){
                    goalDate = LocalDateTime.parse(g.date(), formatter);
                }

                if (goalDate != null
                        && goalDate.equals(currentDate.withHour(2).withMinute(0).withSecond(0).withNano(0))
                        && (activityModel.getContext().equals("N/A") || g.getContextType().equals(activityModel.getContext()))
                        && !(g.repType().equals("Daily") || g.repType().equals("Weekly") || g.repType().equals("Monthly") || g.repType().equals("Yearly"))) {
                    tmrwGoals.add(g);
                    Log.d("TomorrowListFrag", "id: " + g.id() + ", description: " + g.description() + ", repType: " + g.repType() + ", contextType: " + g.getContextType());
                }
            }

            if (view.defaultGoals != null) {
                // Set defaultGoals visibility
                if (tmrwGoals.size() == 0) {
                    view.defaultGoals.setVisibility(View.VISIBLE);
                } else {
                    view.defaultGoals.setVisibility(View.INVISIBLE);
                }
            } else {
                System.out.println("defaultGoals view is null");
            }

            incompleteAdapter.clear();
            incompleteAdapter.addAll(tmrwGoals); // remember the mutable copy here!
            activityModel.setDisplayedTomorrowGoals(tmrwGoals);
            incompleteAdapter.notifyDataSetChanged();
        });

        activityModel.getCompleteGoals().removeAllObservers();
        activityModel.getCompleteGoals().registerObserver(goals -> {
            System.out.println("Tomorrow Complete Goals Observer");
            if (goals == null) return;

            var tmrwGoals = new ArrayList<Goal>();
            for(Goal g : goals){
                LocalDateTime goalDate = null;
                if(!g.date().equals("")){
                    goalDate = LocalDateTime.parse(g.date(), formatter);
                }

                if (goalDate != null
                        && goalDate.equals(currentDate.withHour(2).withMinute(0).withSecond(0).withNano(0))
                        && (activityModel.getContext().equals("N/A") || g.getContextType().equals(activityModel.getContext()))
                        && !(g.repType().equals("Daily") || g.repType().equals("Weekly") || g.repType().equals("Monthly") || g.repType().equals("Yearly"))) {
                    tmrwGoals.add(g);
                    Log.d("TomorrowListFrag", "id: " + g.id() + ", description: " + g.description() + ", repType: " + g.repType() + ", contextType: " + g.getContextType());
                }
            }

            completeAdapter.clear();
            completeAdapter.addAll(tmrwGoals); // remember the mutable copy here!
            activityModel.setDisplayedTomorrowGoals(tmrwGoals);
            completeAdapter.notifyDataSetChanged();
        });

        // Set the adapter on the ListView
        view.uncompletedGoalList.setAdapter(incompleteAdapter);
        view.completedGoalList.setAdapter(completeAdapter);

        return view.getRoot();
    }

    @Override
    public void onStop() {
        super.onStop();
        activityModel.getIncompleteGoals().removeAllObservers();
        activityModel.getCompleteGoals().removeAllObservers();
    }

    public void updateDate(LocalDateTime date){
        this.currentDate = date.plusDays(1);
    }
    public LocalDateTime getDate(){
        return this.currentDate;
    }
}