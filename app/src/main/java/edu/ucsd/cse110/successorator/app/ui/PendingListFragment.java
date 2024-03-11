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
import edu.ucsd.cse110.successorator.app.databinding.FragmentPendingListBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class PendingListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentPendingListBinding view;
    private GoalListAdapter incompleteAdapter;
    private GoalListAdapter completeAdapter;

    public PendingListFragment() {
        // Required empty public constructor
    }

    public static PendingListFragment newInstance() {
        PendingListFragment fragment = new PendingListFragment();
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

        // Initialize the Adapter (with an empty list for now) for incomplete tasks
        this.incompleteAdapter = new GoalListAdapter(requireContext(), List.of(), id -> {
            activityModel.changeCompleteStatus(id);
        }, 2);
        activityModel.getIncompleteGoals().registerObserver(goals -> {
            if (goals == null) return;

            incompleteAdapter.clear();
            incompleteAdapter.addAll(new ArrayList<>(goals)); // remember the mutable copy here!
            incompleteAdapter.notifyDataSetChanged();

            // for each goal set onLongPressListener
        });

        // Initialize the adapter for completed tasks
        this.completeAdapter = new GoalListAdapter(requireContext(), List.of(), id -> {
            activityModel.changeCompleteStatus(id);
        }, 2);
        activityModel.getCompleteGoals().registerObserver(goals -> {
            if (goals == null) return;

            completeAdapter.clear();
            completeAdapter.addAll(new ArrayList<>(goals));
            completeAdapter.notifyDataSetChanged();

            // for each goal set onLongPressListener
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentPendingListBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.uncompletedGoalList.setAdapter(incompleteAdapter);
        view.completedGoalList.setAdapter(completeAdapter);

        return view.getRoot();
    }
}