package edu.ucsd.cse110.successorator.app.ui.dialog;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import java.time.LocalDateTime;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;


import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.databinding.DialogAddGoalsBinding;

import edu.ucsd.cse110.successorator.app.databinding.DialogTodayTomorrowAddGoalsBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

//dialog pop up thing
public class TomorrowAddGoalDialog extends DialogFragment{
    private @NonNull DialogTodayTomorrowAddGoalsBinding view;
    private MainViewModel activityModel;

    TomorrowAddGoalDialog() {
        //Required empty public constructor
    }

    public static TomorrowAddGoalDialog newInstance() {
        var fragment = new TomorrowAddGoalDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = DialogTodayTomorrowAddGoalsBinding.inflate(getLayoutInflater());

        LocalDateTime date = LocalDateTime.now();
        String dayOfWeek = date.getDayOfWeek().toString().toLowerCase();
        dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1);

        // Set weekly option text
        String weeklyFormat = "Weekly on %s";
        view.weekly.setText(String.format(weeklyFormat, dayOfWeek));

        // Set monthly option text
        String monthlyFormat = "Monthly on the %s %s";
        int dayOfWeekOccurrenceNum = date.getDayOfMonth() / 7;
        if (date.getDayOfMonth() % 7 != 0) {
            dayOfWeekOccurrenceNum += 1;
        }
        String dayOfWeekOccurrenceString;
        switch (dayOfWeekOccurrenceNum) {
            case 1:
                dayOfWeekOccurrenceString = "First";
                break;
            case 2:
                dayOfWeekOccurrenceString = "Second";
                break;
            case 3:
                dayOfWeekOccurrenceString = "Third";
                break;
            case 4:
                dayOfWeekOccurrenceString = "Fourth";
                break;
            case 5:
                dayOfWeekOccurrenceString = "Fifth";
                break;
            default:
                dayOfWeekOccurrenceString = "None";
        }
        view.monthly.setText(String.format(monthlyFormat, dayOfWeekOccurrenceString, dayOfWeek));

        // Set yearly option text
        String yearlyFormat = "Yearly on %s %d";
        String month = date.getMonth().toString().toLowerCase();
        month = month.substring(0, 1).toUpperCase() + month.substring(1);
        view.yearly.setText(String.format(yearlyFormat, month, date.getDayOfMonth()));

        return new AlertDialog.Builder(getActivity())
                .setTitle("New Goal for Tomorrow")
                .setMessage("Please provide the goal description.")
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

        ;

        // Change each recurrence option back to default before storing string to database
        view.weekly.setText("Weekly");
        view.monthly.setText("Monthly");
        view.yearly.setText("Yearly");

        // Get selected recurrence frequency
        LocalDateTime curtime = activityModel.getCurrentDate();
        curtime = curtime.plusDays(1);
        RadioGroup repTypeGroup = view.repTypeRadio;
        int selectedRepTypeId = repTypeGroup.getCheckedRadioButtonId();
        RadioButton selectedRepTypeRadioButton = view.getRoot().findViewById(selectedRepTypeId);
        String repType = selectedRepTypeRadioButton.getText().toString();

        // Get selected context
        RadioGroup contextTypeGroup = view.contextRadioGroup;
        int selectedContextTypeId = contextTypeGroup.getCheckedRadioButtonId();
        System.out.println(selectedContextTypeId);
        RadioButton selectedContextTypeRadioButton = view.getRoot().findViewById(selectedContextTypeId);
        String contextType = selectedContextTypeRadioButton.getText().toString();

        // Create new goal
        Goal newGoal = new Goal(currCount+1, description, false,curtime.toString(), repType, contextType);

        // Add the new goal to your model
        activityModel.addGoal(newGoal);

        // Dismiss the dialog
        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}
