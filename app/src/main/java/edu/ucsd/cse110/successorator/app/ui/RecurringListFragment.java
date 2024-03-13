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
import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.databinding.FragmentRecurringListBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class RecurringListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentRecurringListBinding view;
    private GoalListAdapter goalsAdapter;

    public RecurringListFragment() {
        // Required empty public constructor
    }

    public static RecurringListFragment newInstance() {
        RecurringListFragment fragment = new RecurringListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentRecurringListBinding.inflate(inflater, container, false);

        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        // Initialize the Adapter (with an empty list for now) for incomplete tasks
        this.goalsAdapter = new GoalListAdapter(requireContext(), List.of(), id -> {
            activityModel.changeCompleteStatus(id);
        }, 3, activityModel);
        activityModel.getRecurringGoals().registerObserver(goals -> {
            if (goals == null) return;

            var recurringGoals = new ArrayList<Goal>();
            for(Goal g : goals){
                if((activityModel.getContext().equals("N/A") || g.getContextType().equals(activityModel.getContext()))){
                    recurringGoals.add(g);
                }
            }
            goalsAdapter.clear();
            goalsAdapter.addAll(recurringGoals); // remember the mutable copy here!
            goalsAdapter.notifyDataSetChanged();
        });
        // Set the adapter on the ListView
        view.uncompletedGoalList.setAdapter(goalsAdapter);

        return view.getRoot();
    }
}