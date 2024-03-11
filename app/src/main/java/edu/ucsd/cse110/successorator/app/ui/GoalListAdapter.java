
package edu.ucsd.cse110.successorator.app.ui;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import org.xml.sax.helpers.AttributeListImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.app.databinding.ListGoalItemBinding;
import edu.ucsd.cse110.successorator.app.databinding.RecurringListGoalItemBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class GoalListAdapter extends ArrayAdapter<Goal> {
    Consumer<Integer> onDeleteClick;
    int fragmentType;

    public GoalListAdapter(Context context, List<Goal> goals, Consumer<Integer> onDeleteClick, int fragmentType) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(goals));
        this.onDeleteClick = onDeleteClick;
        this.fragmentType = fragmentType;
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

            // Set description
            binding.goalDescription.setText(goal.description());

            String recurrenceType = goal.repType();
            LocalDateTime date = LocalDateTime.parse(goal.date());
            String dayOfWeek = date.getDayOfWeek().toString().toLowerCase();
            dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1);

            switch (recurrenceType) {
                case "Daily":
                    binding.goalRecurrence.setText("Daily");
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

            // Populate the view with the task's description.
            if (!goal.completed()) {
                binding.goalDescription.setText(goal.description());
            } else {
                binding.goalDescription.setText(goal.description());
                binding.goalDescription.setPaintFlags(binding.goalDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            // Don't want this functionality for recurring goals
            binding.goalDescription.setOnClickListener(v -> {
                var id = goal.id();
                assert id != null;
                onDeleteClick.accept(id);
            });

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

