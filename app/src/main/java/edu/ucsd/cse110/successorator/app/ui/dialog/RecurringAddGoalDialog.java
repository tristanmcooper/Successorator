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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
    private LocalDateTime selectedDate;
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

        selectedDate = activityModel.getCurrentDate();
        selectedDateLong = selectedDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        // Get user input from the EditText
        String description = view.editText.getText().toString();
        int currCount = activityModel.getMaxId();

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
        Goal newGoal = new Goal(currCount+1, description, false, this.selectedDate.toString(), repType, contextType,null);

        // Add the new goal to your model
        activityModel.addGoal(newGoal);

        // Add 'instances' of recurring goals as needed
        createInstances(currCount+1);

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
        /*
         * MaterialDatePicker from: https://github.com/material-components/material-components-android/blob/master/docs/components/DatePicker.md
         */

        // Makes only dates from today forward selectable.
        Long dayBeforeStartDate = selectedDate.minusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        var constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(dayBeforeStartDate));

        // Create new datePicker instance
        MaterialDatePicker<Long> datePicker;

        if (this.selectedDateLong != null) {
            datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Starting Date")
                    .setSelection(this.selectedDateLong)
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build();
        } else {
            datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Starting Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setCalendarConstraints(constraintsBuilder.build())
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

    private void createInstances(int id) {
        Goal recurringGoal = activityModel.find(id);
        int currCount = activityModel.getMaxId();
        switch (recurringGoal.repType()) {
            case "Daily":
                // Create instance for today and tomorrow
                Goal todayGoal = new Goal(currCount+1, recurringGoal.description(), false, selectedDate.toString(), "Once", recurringGoal.getContextType(), recurringGoal.id());
                activityModel.addGoal(todayGoal);
                Goal tmrGoal = new Goal(currCount+2, recurringGoal.description(), false, selectedDate.plusDays(1).toString(), "Once", recurringGoal.getContextType(), recurringGoal.id());
                activityModel.addGoal(tmrGoal);
                break;
            case "Weekly":
                Goal weeklyGoal = new Goal(currCount+1, recurringGoal.description(), false, selectedDate.plusDays(7).toString(), "Once", recurringGoal.getContextType(), recurringGoal.id());
                activityModel.addGoal(weeklyGoal);
                break;
            case "Monthly":
                // If 5th some day doesn't exist, create 1st that day next month
                Goal monthlyGoal;
                int weekNum = this.selectedDate.getDayOfMonth() / 7;
                if (this.selectedDate.getDayOfMonth() % 7 != 0) {
                    weekNum += 1;
                }

                LocalDateTime nextRecurrenceDate;
                if (weekNum == 5) {
                    nextRecurrenceDate = selectedDate.plusDays(35);
                } else {
                    LocalDateTime firstDayOfNextMonth = LocalDateTime.of(selectedDate.getYear(), selectedDate.getMonthValue() + 1, 1, 2, 0, 0);
                    String dayOfFirst = firstDayOfNextMonth.getDayOfWeek().toString();

                    // Offset to find date of first occurrence of day in next month
                    HashMap<String, Integer> valueOfWeekDays = new HashMap<>() {{
                        put("MONDAY", 1);
                        put("TUESDAY", 2);
                        put("WEDNESDAY", 3);
                        put("THURSDAY", 4);
                        put("FRIDAY", 5);
                        put("SATURDAY", 6);
                        put("SUNDAY", 7);
                    }};

                    nextRecurrenceDate = firstDayOfNextMonth.plusDays(
                            ((weekNum - 1) * 7) //-1 because dayOfFirst is the first 'day-of-week' in the next month
                            + Math.abs(valueOfWeekDays.get(selectedDate.getDayOfWeek().toString())
                            - valueOfWeekDays.get(dayOfFirst))
                    ); // use HashMap and difference in days to find date of the first 'day-of-week'
                }

                monthlyGoal = new Goal(currCount+1, recurringGoal.description(), false, nextRecurrenceDate.toString(), "Once", recurringGoal.getContextType(),recurringGoal.id());
                activityModel.addGoal(monthlyGoal);
                break;
            case "Yearly":
                // If Feb 29 is selected, selected year is leap year, next instance is March 1 next year
                Goal yearlyGoal;
                if (selectedDate.getMonthValue() == 2 && selectedDate.getDayOfMonth() == 29) {
                    yearlyGoal = new Goal(currCount+1, recurringGoal.description(), false, selectedDate.plusYears(1).plusDays(1).toString(), "Once", recurringGoal.getContextType(), recurringGoal.id());
                } else {
                    yearlyGoal = new Goal(currCount+1, recurringGoal.description(), false, selectedDate.plusYears(1).toString(), "Once", recurringGoal.getContextType(), recurringGoal.id());
                }
                activityModel.addGoal(yearlyGoal);
                break;
            default:

        }
    }
}
