
package edu.ucsd.cse110.successorator.app.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import org.xml.sax.helpers.AttributeListImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.R;
import edu.ucsd.cse110.successorator.app.databinding.ListGoalItemBinding;
import edu.ucsd.cse110.successorator.app.databinding.RecurringListGoalItemBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class GoalListAdapter extends ArrayAdapter<Goal> {
    Consumer<Integer> onDeleteClick;
    int fragmentType;

    Context context;

    MainViewModel activityModel;

    public GoalListAdapter(Context context, List<Goal> goals, Consumer<Integer> onDeleteClick, int fragmentType) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(goals));
        this.onDeleteClick = onDeleteClick;
        this.fragmentType = fragmentType;
        this.context = context;
    }

    public GoalListAdapter(Context context, List<Goal> goals, Consumer<Integer> onDeleteClick, int fragmentType, MainViewModel model) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(goals));
        this.onDeleteClick = onDeleteClick;
        this.fragmentType = fragmentType;
        this.context = context;
        this.activityModel = model;
    }

    //get the view for each goal item
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the goal for this position.
        var goal = getItem(position);
        assert goal != null;

        if (fragmentType == 3) {
            // Check if a view is being reused...
            RecurringListGoalItemBinding binding;
            if (convertView != null) {
                // if so, bind to it
                binding = RecurringListGoalItemBinding.bind(convertView);
            } else {
                // otherwise inflate a new view from our layout XML.
                var layoutInflater = LayoutInflater.from(getContext());
                binding = RecurringListGoalItemBinding.inflate(layoutInflater, parent, false);
            }

            // Set contextIcon
            binding.contextIcon.setText(goal.getContextType());
            switch (goal.getContextType()){
                case "H":
                    binding.contextIcon.setBackgroundColor(Color.parseColor("#E9C46A"));
                    break;
                case "W":
                    binding.contextIcon.setBackgroundColor(Color.parseColor("#5D8AA8"));
                    break;
                case "S":
                    binding.contextIcon.setBackgroundColor(Color.parseColor("#CDB4DB"));
                    break;
                case "E":
                    binding.contextIcon.setBackgroundColor(Color.parseColor("#8CBF99"));
                    break;
            }

            // Set description
            binding.goalDescription.setText(goal.description());

            String recurrenceType = goal.repType();
            LocalDateTime date = LocalDateTime.parse(goal.date());
            String dayOfWeek = date.getDayOfWeek().toString().toLowerCase();
            dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1);

            switch (recurrenceType) {
                case "Daily":
                    LocalDateTime tmrDate = LocalDateTime.now().withHour(1).withMinute(59).withSecond(0).withNano(0).plusDays(1);
                    if (date.isAfter(tmrDate)) {
                        String month = date.getMonth().toString().toLowerCase();
                        month = month.substring(0, 1).toUpperCase() + month.substring(1);
                        String dailyFormat = "Daily starting on %s %d, %d";
                        binding.goalRecurrence.setText(String.format(dailyFormat, month, date.getDayOfMonth(), date.getYear()));
                    } else {
                        binding.goalRecurrence.setText("Daily");
                    }
                    break;
                case "Weekly":
                    String weeklyFormat = "Weekly on %s";
                    binding.goalRecurrence.setText(String.format(weeklyFormat, dayOfWeek));
                    break;
                case "Monthly":
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

                    binding.goalRecurrence.setText(String.format(monthlyFormat, dayOfWeekOccurrenceString, dayOfWeek));
                    break;
                case "Yearly":
                    String yearlyFormat = "Yearly on %s %d";
                    String month = date.getMonth().toString().toLowerCase();
                    month = month.substring(0, 1).toUpperCase() + month.substring(1);
                    binding.goalRecurrence.setText(String.format(yearlyFormat, month, date.getDayOfMonth()));
                    break;
                default:
                    binding.goalRecurrence.setText("None");
            }


                binding.goalDescription.setOnLongClickListener(v-> {
                    Log.d("GoalListAdapter", "Long press detected");
                    PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.inflate(R.menu.recurring_long_press_context_menu);

                    popupMenu.setOnMenuItemClickListener(item -> {
                        String title = item.getTitle().toString(); // Get the title of the clicked item
                        switch (title) {
                            case "Delete":
                                activityModel.removeSpecificGoal(goal.id());
                                activityModel.removeAllCreatedBy(goal.id());
                                return true;

                            default:
                                return false;
                        }
                    });
                    popupMenu.show();
                    return true; // Consume the long click event
                });

            return binding.getRoot();

        } else {
            // Check if a view is being reused...
            ListGoalItemBinding binding;
            if (convertView != null) {
                // if so, bind to it
                binding = ListGoalItemBinding.bind(convertView);
            } else {
                // otherwise inflate a new view from our layout XML.
                var layoutInflater = LayoutInflater.from(getContext());
                binding = ListGoalItemBinding.inflate(layoutInflater, parent, false);
            }

            //populate the view with the task's context type
            binding.contextIcon.setText(goal.getContextType());
            switch (goal.getContextType()){
                case "H":
                    binding.contextIcon.setBackgroundColor(Color.parseColor("#E9C46A"));
                    break;
                case "W":
                    binding.contextIcon.setBackgroundColor(Color.parseColor("#5D8AA8"));
                    break;
                case "S":
                    binding.contextIcon.setBackgroundColor(Color.parseColor("#CDB4DB"));
                    break;
                case "E":
                    binding.contextIcon.setBackgroundColor(Color.parseColor("#8CBF99"));
                    break;
            }

            // Populate the view with the task's description.
            if (!goal.completed()) {
                binding.goalDescription.setText(goal.description());
            } else {
                binding.goalDescription.setText(goal.description());
                binding.goalDescription.setPaintFlags(binding.goalDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            if (fragmentType == 0 || fragmentType == 1){
                // Don't want this functionality for pending or recurring view
                binding.goalDescription.setOnClickListener(v -> {
                    var id = goal.id();
                    assert id != null;
                    onDeleteClick.accept(id);
                });
            }
            if (fragmentType == 2){
                binding.goalDescription.setOnLongClickListener(v-> {
                    Log.d("GoalListAdapter", "Long press detected");
                    PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.inflate(R.menu.pending_long_press_context_menu);

                    popupMenu.setOnMenuItemClickListener(item -> {
                        String title = item.getTitle().toString(); // Get the title of the clicked item
                        switch (title) {
                            case "Today":
                                // Move to today view
                                activityModel.changeToTodayView(goal.id(), false);
                                return true;
                            case "Tomorrow":
                                activityModel.changeToTomorrowView(goal.id(), false);
                                return true;
                            case "Finished":
                                activityModel.changeToTodayView(goal.id(), true);
                                return true;
                            case "Delete":
                                activityModel.removeSpecificGoal(goal.id());
                                return true;

                            default:
                                return false;
                        }
                    });


                    popupMenu.show();
                    return true; // Consume the long click event
                });
            }


            return binding.getRoot();
        }
    }

    // The below methods aren't strictly necessary, usually.
    // But get in the habit of defining them because they never hurt
    // (as long as you have IDs for each item) and sometimes you need them.

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        var goal = getItem(position);
        assert goal != null;

        var id = goal.id();
        assert id != null;

        return id;
    }

    public int getItemCount() {
        return super.getCount();
    }
}

