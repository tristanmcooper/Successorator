package edu.ucsd.cse110.successorator.app;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.successorator.app.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.app.ui.GoalsListAdapter;
import edu.ucsd.cse110.successorator.app.ui.dialog.AddGoalFragment;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
//import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ActivityMainBinding view;
    private GoalsListAdapter adapter;
    private MainViewModel model; // won't need later when we do fragments

    private DisplayUpdater displayUpdater;

    private SuccessoratorApplication appSec = new SuccessoratorApplication();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_title);

        this.view = ActivityMainBinding.inflate(getLayoutInflater());
        String date = new SimpleDateFormat("EEEE, M/dd", Locale.getDefault()).format(new Date());
        TextView dateTextView = view.dateTextView;
        if (dateTextView != null) {
            dateTextView.setText(date);
        }

        view.addGoalButton.setOnClickListener(v -> {
            var dialogFragment = AddGoalFragment.newInstance();
            dialogFragment.show(getSupportFragmentManager(), "AddGoalFragment");
        });

        var modelOwner = this;
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.model = modelProvider.get(MainViewModel.class);


        this.adapter = new GoalsListAdapter(MainActivity.this, List.of());
        this.displayUpdater = new DisplayUpdater(MainActivity.this);


        model.getOrderedGoals().registerObserver(goals -> {
            if (goals == null) return;
            adapter.clear();
            adapter.addAll(new ArrayList<>(goals)); // remember the mutable copy here!
            adapter.notifyDataSetChanged();
        });

        setContentView(view.getRoot());
    }
    public void reloadGoalsListView(ArrayList<Goal> value){
        if (value != null){
            adapter.clear();
            adapter.addAll(value);
            view.taskList.setAdapter(adapter);
        }
    }
}
    /*
        //resources from the strings file
        Resources res = getResources();
        view.defaultGoals.setText(res.getString(R.string.default_goals));
        //model.getDisplayedText().observe(text -> view.defaultGoals.setText(text));
    }
}
*/