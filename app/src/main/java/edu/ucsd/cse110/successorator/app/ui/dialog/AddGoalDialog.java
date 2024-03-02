package edu.ucsd.cse110.successorator.app.ui.dialog;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;


import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.databinding.DialogAddGoalsBinding;

import edu.ucsd.cse110.successorator.lib.domain.Goal;

//dialog pop up thing
public class AddGoalDialog extends DialogFragment{
    private DialogAddGoalsBinding view;
    private MainViewModel activityModel;

    AddGoalDialog() {
        //Required empty public constructor
    }

    public static AddGoalDialog newInstance() {
        var fragment = new AddGoalDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = DialogAddGoalsBinding.inflate(getLayoutInflater());

        return new AlertDialog.Builder(getActivity())
                .setTitle("New Goal")
                .setMessage("Please provide the new card text.")
                .setView(view.getRoot())
                .setPositiveButton("Create", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner,modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        // Get user input from the EditText
        String description = view.editText.getText().toString();
        int currCount = activityModel.getCount();

        if (description.length() == 0) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Error")
                    .setMessage("The Goal name can't be empty")
                    .setNegativeButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        // Create the new Goal object with user input as the description
        Goal newGoal = new Goal(currCount+1, description, false);

        // Add the new goal to your model
        activityModel.addGoal(newGoal);

        // Dismiss the dialog
            dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}
