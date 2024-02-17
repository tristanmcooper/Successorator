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
import edu.ucsd.cse110.successorator.lib.domain.Goal;
//import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding view;
    private GoalListAdapter adapter;
    private MainViewModel model; // won't need later when we do fragments
    private DisplayUpdater displayUpdater;

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


        this.adapter = new GoalListAdapter(getApplicationContext(), List.of());
        this.displayUpdater = new DisplayUpdater(this);


        model.getOrderedGoals().registerObserver(goals -> {
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

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, GoalListFragment.newInstance())
                .commit();

        setContentView(view.getRoot());
    }
//    public void reloadGoalsListView(ArrayList<Goal> value){
//        if (value != null){
//            adapter.clear();
//            adapter.addAll(value);
//            view.goalList.setAdapter(adapter);
//        }
//    }

}
    /*
        //resources from the strings file
        Resources res = getResources();
        view.defaultGoals.setText(res.getString(R.string.default_goals));
        //model.getDisplayedText().observe(text -> view.defaultGoals.setText(text));
    }
}
*/