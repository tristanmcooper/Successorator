package edu.ucsd.cse110.successorator.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.databinding.FragmentRecurringListBinding;

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
        }, 3);
        activityModel.getRecurringGoals().registerObserver(goals -> {
            if (goals == null) return;

            goalsAdapter.clear();
            goalsAdapter.addAll(new ArrayList<>(goals)); // remember the mutable copy here!
            goalsAdapter.notifyDataSetChanged();
        });
        // Set the adapter on the ListView
        view.uncompletedGoalList.setAdapter(goalsAdapter);

        return view.getRoot();
    }
}