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
    private String context;

    FocusModeDialog(String context) {
        this.context = context;
    }

    public static FocusModeDialog newInstance(String context) {
        var fragment = new FocusModeDialog(context);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = DialogFocusModeBinding.inflate(getLayoutInflater()); //change this

        switch (context) {
            case "H":
                view.homeRadioButton.setChecked(true);
                break;
            case "S":
                view.schoolRadioButton.setChecked(true);
                break;
            case "W":
                view.workRadioButton.setChecked(true);
                break;
            case "E":
                view.errandRadioButton.setChecked(true);
                break;
            default:
                view.noneRadioButton.setChecked(true);
        }

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
        String selectedFocusString = selectedFocusIdButton.getText().toString();
        if (!selectedFocusString.equals("N/A")) {
            activityModel.setFilterContext(selectedFocusString.substring(0, 1));
        } else {
            activityModel.setFilterContext(selectedFocusString);
        }
        // Dismiss the dialog
        //LiveData<List<GoalEntity>> filteredGoalsLiveData = activityModel.getRepo().getGoalsByContextType(String.valueOf(selectedFocusChar));

        // Observe the LiveData for changes
        dialog.dismiss();

    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }


}
