package edu.ucsd.cse110.successorator.app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.successorator.app.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.app.ui.GoalListAdapter;
import edu.ucsd.cse110.successorator.app.ui.GoalListFragment;
import edu.ucsd.cse110.successorator.app.ui.dialog.AddGoalFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding view;
    private GoalListAdapter adapter;
    private MainViewModel model;

    //sets up the initial main activity view xml
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_title);
        
        //grab view by inflating xml layout file
        this.view = ActivityMainBinding.inflate(getLayoutInflater());

        //set up the date
        String date = new SimpleDateFormat("EEEE, M/dd", Locale.getDefault()).format(new Date());
        TextView dateTextView = view.dateTextView;
        if (dateTextView != null) {
            dateTextView.setText(date);
        }

        //connect the add goal button to the addGoalDialogFragment onClick
        view.addGoalButton.setOnClickListener(v -> {
            var dialogFragment = AddGoalFragment.newInstance();
            dialogFragment.show(getSupportFragmentManager(), "AddGoalFragment");
        });

        //connect to the model(MainViewModel)
        var modelOwner = this;
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.model = modelProvider.get(MainViewModel.class);

        //setup the adapter for the list, so it can update it at the beginning
        this.adapter = new GoalListAdapter(getApplicationContext(), List.of(), null);

        //check for changes in the database thorugh getOrderedGoals
        model.getIncompleteGoals().registerObserver(goals -> {
            if (goals == null) {
                view.defaultGoals.setVisibility(View.VISIBLE);
                return;
            }
            if (goals.size() == 0) {
                view.defaultGoals.setVisibility(View.VISIBLE);
            } else {
                view.defaultGoals.setVisibility(View.INVISIBLE);
            }
            adapter.clear();
            adapter.addAll(new ArrayList<>(goals)); // remember the mutable copy here!
            adapter.notifyDataSetChanged();
        });
        
        //show the GoalListFragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, GoalListFragment.newInstance())
                .commit();

        //set the current view this main activity that we just set up
        setContentView(view.getRoot());
    }
}