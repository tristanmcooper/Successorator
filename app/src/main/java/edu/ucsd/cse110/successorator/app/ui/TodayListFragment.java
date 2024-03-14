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
import edu.ucsd.cse110.successorator.app.R;
import edu.ucsd.cse110.successorator.app.databinding.FragmentTodayListBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.util.Observer;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;

public class TodayListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentTodayListBinding view;
    private GoalListAdapter incompleteAdapter;
    private GoalListAdapter completeAdapter;
    private Observer<List<Goal>> incompleteOb;
    private Observer<List<Goal>> completeOb;
    private List<Goal> todaysGoals = new ArrayList<>();
    private LocalDateTime currentDate = LocalDateTime.now().withHour(2).withMinute(0).withSecond(0).withNano(0);

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

        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        // Initialize the Adapter (with an empty list for now)
        this.incompleteAdapter = new GoalListAdapter(requireContext(), List.of(), id -> {
            activityModel.changeCompleteStatus(id);
        }, 0);

        // Initialize the adapter for completed tasks
        this.completeAdapter = new GoalListAdapter(requireContext(), List.of(), id -> {
            activityModel.changeCompleteStatus(id);
        }, 0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.getDefault());

        this.incompleteOb = goals -> {
            if (goals == null) return;

            todaysGoals = new ArrayList<Goal>();
            for(Goal g : goals){
                LocalDateTime goalDate = null;
                if (!g.date().equals("")){
                    goalDate = LocalDateTime.parse(g.date(), formatter);
                }
                System.out.println(goalDate == null);
                System.out.println(currentDate == null);
                if(!(goalDate == null) && goalDate.getDayOfYear()<=currentDate.getDayOfYear() && (activityModel.getContext().equals("N/A") || g.getContextType().equals(activityModel.getContext()))){
                    todaysGoals.add(g);
                    Log.d("TodayListFrag, Incomplete", "id: " + g.id() + ", description: " + g.description() + ", repType: " + g.repType() + ", contextType: " + g.getContextType());
                }
            }

            incompleteAdapter.clear();
            incompleteAdapter.addAll(todaysGoals); // remember the mutable copy here!
            activityModel.setDisplayedTodayGoals((ArrayList<Goal>)todaysGoals);
            incompleteAdapter.notifyDataSetChanged();
        };

        this.completeOb = goals -> {
            if (goals == null) return;
            var todaysGoals = new ArrayList<Goal>();
            for(Goal g : goals){
                LocalDateTime goalDate = null;
                if (!g.date().equals("")){
                    goalDate = LocalDateTime.parse(g.date(), formatter);
                }
                if(!(goalDate==null) && goalDate.getDayOfYear()<=currentDate.getDayOfYear() && (activityModel.getContext().equals("N/A") || g.getContextType().equals(activityModel.getContext()))){
                    todaysGoals.add(g);
                }
                Log.d("TodayListFrag, complete", "id: " + g.id() + ", description: " + g.description() + ", repType: " + g.repType() + ", contextType: " + g.getContextType());
            }
            completeAdapter.clear();
            completeAdapter.addAll(todaysGoals);
            completeAdapter.notifyDataSetChanged();
        };

        activityModel.getIncompleteGoals().registerObserver(incompleteOb);
        activityModel.getCompleteGoals().registerObserver(completeOb);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.view  = FragmentTodayListBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.uncompletedGoalList.setAdapter(incompleteAdapter);
        view.completedGoalList.setAdapter(completeAdapter);
        return view.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (view.findViewById(R.id.default_goals) != null) {
            // Set defaultGoals visibility
            if (todaysGoals.isEmpty()) {
                view.findViewById(R.id.default_goals).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.default_goals).setVisibility(View.INVISIBLE);
            }
        } else {
            System.out.println("defaultGoals view is null");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        activityModel.getIncompleteGoals().removeObserver(incompleteOb);
        activityModel.getCompleteGoals().removeObserver(completeOb);
    }

    public void updateDate(LocalDateTime date){
        this.currentDate = date;
    }
    public LocalDateTime getDate(){
        return this.currentDate;
    }
}