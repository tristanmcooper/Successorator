package edu.ucsd.cse110.successorator.app;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import edu.ucsd.cse110.successorator.app.data.db.GoalDao;
import edu.ucsd.cse110.successorator.app.data.db.GoalDao_Impl;
import edu.ucsd.cse110.successorator.app.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.app.data.db.SuccessoratorDatabase;
import edu.ucsd.cse110.successorator.app.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.app.ui.GoalsListAdapter;
import edu.ucsd.cse110.successorator.app.ui.dialog.AddGoalFragment;
import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_title);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        var database = Room.databaseBuilder(
                        getApplicationContext(),
                        SuccessoratorDatabase.class,
                        "successorator-database"
                )
                .allowMainThreadQueries()
                .build();

        //var dataSource = InMemoryDataSource.fromDefault();
        var dataSource = new RoomGoalRepository(database.goalDao());
        this.model = new MainViewModel(dataSource);

//        var modelOwner = this;
//        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
//        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
//        this.model = modelProvider.get(MainViewModel.class);

        this.view = ActivityMainBinding.inflate(getLayoutInflater());
        //resources from the strings file
        Resources res = getResources();
        view.defaultGoals.setText(res.getString(R.string.default_goals));
        //model.getDisplayedText().observe(text -> view.defaultGoals.setText(text));
        this.adapter = new GoalsListAdapter(MainActivity.this, List.of());
        this.displayUpdater = new DisplayUpdater(MainActivity.this);


        /*
        model.getOrderedGoals().observe(goals -> {
            if (goals == null) return;
            adapter.clear();
            adapter.addAll(new ArrayList<>(goals)); // remember the mutable copy here!
            adapter.notifyDataSetChanged();
        });
        */
        adapter.addAll(model.getOrderedGoals());
        view.taskList.setAdapter(adapter);

        model.goalRepository.observe(displayUpdater);

        view.addButton3.setOnClickListener(v -> {
            var dialogFragment = AddGoalFragment.newInstance();
            dialogFragment.show(getSupportFragmentManager(), "AddGoalFragment");
        });

        String date = new SimpleDateFormat("EEEE, M/dd", Locale.getDefault()).format(new Date());
        TextView dateTextView = view.dateTextView;
        if (dateTextView != null) {
            dateTextView.setText(date);
        }
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
