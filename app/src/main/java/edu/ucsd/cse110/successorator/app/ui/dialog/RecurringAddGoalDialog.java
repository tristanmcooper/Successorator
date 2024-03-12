package edu.ucsd.cse110.successorator.app.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.databinding.DialogRecurringAddGoalsBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

//dialog pop up thing
public class RecurringAddGoalDialog extends DialogFragment{
    private @NonNull DialogRecurringAddGoalsBinding view;
    private MainViewModel activityModel;
    private Long selectedDateLong;
    private LocalDateTime selectedDate = LocalDateTime.now().withHour(2).withMinute(0).withSecond(0).withNano(0);
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(("E, M/d"), Locale.getDefault());

    RecurringAddGoalDialog() {
        //Required empty public constructor
    }

    public static RecurringAddGoalDialog newInstance() {
        var fragment = new RecurringAddGoalDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = DialogRecurringAddGoalsBinding.inflate(getLayoutInflater());

        view.datePicker.setOnClickListener(v -> {
            showDatePicker();
        });

        updateDisplayDate();
        updateRecurrenceOptions();

        return new AlertDialog.Builder(getActivity())
                .setTitle("New Recurring Goal")
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

        view.weekly.setText("Weekly");
        view.monthly.setText("Monthly");
        view.yearly.setText("Yearly");

        // Get recurrence type
        RadioGroup repTypeGroup = view.repTypeRadio;
        int selectedRepTypeId = repTypeGroup.getCheckedRadioButtonId();
        RadioButton selectedRepTypeRadioButton = view.getRoot().findViewById(selectedRepTypeId);
        String repType = selectedRepTypeRadioButton.getText().toString();

        // Get context type
        RadioGroup contextTypeGroup = view.contextRadioGroup;
        int selectedContextTypeId = contextTypeGroup.getCheckedRadioButtonId();
        RadioButton selectedContextTypeRadioButton = view.getRoot().findViewById(selectedContextTypeId);
        String contextType = selectedContextTypeRadioButton.getText().toString();

        // Create new goal based on user input
        Goal newGoal = new Goal(currCount+1, description, false, this.selectedDate.toString(), repType, contextType);

        // Add the new goal to your model
        activityModel.addGoal(newGoal);

        // Dismiss the dialog
        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }

    private void updateDisplayDate() {
        view.datePicker.setText(this.selectedDate.format(this.dateTimeFormatter));
    }

    private void updateRecurrenceOptions() {
        String dayOfWeek = this.selectedDate.getDayOfWeek().toString().toLowerCase();
        dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1);

        // Set weekly option text
        String weeklyFormat = "Weekly on %s";
        view.weekly.setText(String.format(weeklyFormat, dayOfWeek));

        // Set monthly option text
        String monthlyFormat = "Monthly on the %s %s";
        int dayOfWeekOccurrenceNum = this.selectedDate.getDayOfMonth() / 7;
        if (this.selectedDate.getDayOfMonth() % 7 != 0) {
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
        String month = this.selectedDate.getMonth().toString().toLowerCase();
        month = month.substring(0, 1).toUpperCase() + month.substring(1);
        view.yearly.setText(String.format(yearlyFormat, month, this.selectedDate.getDayOfMonth()));
    }

    private void showDatePicker() {
        // Create new datePicker instance
        MaterialDatePicker<Long> datePicker;

        if (this.selectedDateLong != null) {
            datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Starting Date")
                    .setSelection(this.selectedDateLong)
                    .build();
        } else {
            datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Starting Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();
        }

        // Set up positiveClick behavior
        datePicker.addOnPositiveButtonClickListener(v -> {
            this.selectedDateLong = datePicker.getSelection();
            LocalDateTime selectedDate = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(datePicker.getSelection()),
                    TimeZone.getTimeZone("Pacific").toZoneId()).plusHours(2);
            this.selectedDate = selectedDate;
            updateDisplayDate();
            updateRecurrenceOptions();
            datePicker.dismiss();
        });

        // Set up negativeClick behavior
        datePicker.addOnNegativeButtonClickListener(v -> {
            datePicker.dismiss();
        });

        // Set up cancel behavior
        datePicker.addOnCancelListener(v -> {
            datePicker.dismiss();
        });

        // Display
        datePicker.show(getActivity().getSupportFragmentManager(), "datePicker");
    }
}
