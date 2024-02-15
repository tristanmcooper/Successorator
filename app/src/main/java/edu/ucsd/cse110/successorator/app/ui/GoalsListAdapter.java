package edu.ucsd.cse110.successorator.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.app.databinding.ListTaskItemBinding;

public class GoalsListAdapter extends ArrayList<String> {
    Consumer<Integer> onDeleteClick;

    public GoalsListAdapter(Context context, List<String> flashcards, Consumer<Integer> onDeleteClick) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(flashcards));
        this.onDeleteClick = onDeleteClick;
    }

    //FIXING CURRENTLY
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the flashcard for this position.
        var flashcard = getItem(position);
        assert flashcard != null;

        // Check if a view is being reused...
        ListTaskItemBinding binding;
        if (convertView != null) {
            // if so, bind to it
            binding = ListTaskItemBinding.bind(convertView);
        } else {
            // otherwise inflate a new view from our layout XML.
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListTaskItemBinding.inflate(layoutInflater, parent, false);
        }

        // Populate the view with the task's data.
        binding.taskName.setText(flashcard.front());

        /*
        binding.cardDeleteButton.setOnClickListener(v-> {
            var id =flashcard.id();
            assert id != null;
            onDeleteClick.accept(id);
        });
        */
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
        var flashcard = getItem(position);
        assert flashcard != null;

        var id = flashcard.id();
        assert id != null;

        return id;
    }
}
}
