package edu.ucsd.cse110.successorator.app.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.data.db.GoalEntity;
import edu.ucsd.cse110.successorator.app.databinding.DialogFocusModeBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

//dialog pop up thing
public class FocusModeDialog extends DialogFragment {
    private DialogFocusModeBinding view;
    private MainViewModel activityModel;

    FocusModeDialog() {
        //Required empty public constructor
    }

    public static FocusModeDialog newInstance() {
        var fragment = new FocusModeDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = DialogFocusModeBinding.inflate(getLayoutInflater()); //change this

        return new AlertDialog.Builder(getActivity())
                .setTitle("Focus Mode")
                .setMessage("Select a focus.")
                .setView(view.getRoot())
                .setPositiveButton("Confirm", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }
    private List<Goal> filterGoalsByFocusType(char selectedFocusString, List<Goal> allGoals) {
        List<Goal> filteredGoals = new ArrayList<>();

        for (Goal goal : allGoals) {
            // Assuming your Goal class has a method to get the context type (replace with your actual method)
            String goalFocusType = goal.getContextType();

            if (selectedFocusString == goalFocusType.charAt(0)) {
                filteredGoals.add(goal);
            }
        }

        return filteredGoals;
    }

    // Edit this!!!! To-do!!! Select the focus? change the focus code here.
    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        RadioGroup focusOnGroup = view.focusModeRadioGroup;
        int selectedFocusTypeId = focusOnGroup.getCheckedRadioButtonId();
        RadioButton selectedFocusIdButton = view.getRoot().findViewById(selectedFocusTypeId);
        char selectedFocusChar = selectedFocusIdButton.getText().toString().charAt(0);

        // Dismiss the dialog
        LiveData<List<GoalEntity>> filteredGoalsLiveData = activityModel.getRepo().getGoalsByContextType(String.valueOf(selectedFocusChar));

        // Observe the LiveData for changes

        filteredGoalsLiveData.observe(this, new Observer<List<GoalEntity>>() {
            @Override
            public void onChanged(List<GoalEntity> filteredGoals) {
                // Do something with the filtered goals
                for (GoalEntity goal : filteredGoals) {
                    Log.d("FilteredGoal", "Goal: " + goal.toString());
                }
                // Dismiss the dialog
                dialog.dismiss();

                // Remove the observer to avoid unnecessary updates
                filteredGoalsLiveData.removeObserver(this);
            }
        });

    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }


}
