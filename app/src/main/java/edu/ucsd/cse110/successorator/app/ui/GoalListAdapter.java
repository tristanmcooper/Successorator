
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.app.databinding.ListGoalItemBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class GoalListAdapter extends ArrayAdapter<Goal> {
    Consumer<Integer> onDeleteClick;

    public GoalListAdapter(Context context, List<Goal> goals, Consumer<Integer> onDeleteClick) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(goals));
        this.onDeleteClick = onDeleteClick;
    }

    //get the view for each goal item
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the flashcard for this position.
        var goal = getItem(position);
        assert goal != null;

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

        if (!goal.completed()){
            binding.goalDescription.setText(goal.description());
        }
        else{
            binding.goalDescription.setText(goal.description());
            binding.goalDescription.setPaintFlags(binding.goalDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        binding.goalDescription.setText(goal.description());
        binding.goalDescription.setOnClickListener(v -> {
            var id = goal.id();
            assert id != null;
            onDeleteClick.accept(id);
        });

        return binding.getRoot();
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

