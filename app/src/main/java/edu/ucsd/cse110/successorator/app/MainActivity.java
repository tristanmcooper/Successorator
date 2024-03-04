package edu.ucsd.cse110.successorator.app;

import android.annotation.SuppressLint;
import android.os.Bundle;


import android.os.Handler;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import androidx.appcompat.widget.AppCompatImageButton;


import edu.ucsd.cse110.successorator.app.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.app.ui.GoalListAdapter;
import edu.ucsd.cse110.successorator.app.ui.TodayListFragment;
import edu.ucsd.cse110.successorator.app.ui.TomorrowListFragment;
import edu.ucsd.cse110.successorator.app.ui.PendingListFragment;
import edu.ucsd.cse110.successorator.app.ui.RecurringListFragment;
import edu.ucsd.cse110.successorator.app.ui.dialog.AddGoalDialog;
import edu.ucsd.cse110.successorator.app.ui.dialog.TomorrowAddGoalDialog;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ActivityMainBinding view;

    private MainViewModel model; // won't need later when we do fragments

    private TextView textViewDate;
    private Handler handler;// won't need later when we do fragments

    private AppCompatImageButton buttonAdvanceDate;

    private Runnable dateUpdater;
    private LocalDateTime currentDateTime;
    private GoalListAdapter adapter;
    private String prevDate;
    private boolean advButtonClicked = false;
    private int fragmentType = 0;

    //sets up the initial main activity view xml
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle(R.string.app_title);
        this.view = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
        //connect to the model(MainViewModel)
        var modelOwner = this;
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.model = modelProvider.get(MainViewModel.class);
        //grab view by inflating xml layout file
        textViewDate = findViewById(R.id.dateTextView);
        handler = new Handler();
        buttonAdvanceDate = findViewById(R.id.button_advance_date);

        // Initial update
        currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy", Locale.getDefault());
        prevDate = currentDateTime.format(formatter);
        updateDate();

        dateUpdater = new Runnable() {
            @Override
            public void run() {
                updateDate();
                handler.postDelayed(this, 100); // Update every second
            }
        };
        handler.postDelayed(dateUpdater, 100);

        buttonAdvanceDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advanceDate();
            }
        });

        //connect the add goal button to the addGoalDialogFragment onClick
        view.addGoalButton.setOnClickListener(v -> {
            if(fragmentType == 0){
                var dialogFragment = AddGoalDialog.newInstance();
                dialogFragment.show(getSupportFragmentManager(), "AddGoalFragment");
            }
            else if(fragmentType == 1){
                var dialogFragment = TomorrowAddGoalDialog.newInstance();
                dialogFragment.show(getSupportFragmentManager(), "TomorrowAddGoalFragment");
            }
        });



        //setup the adapter for the list, so it can update it at the beginning
        this.adapter = new GoalListAdapter(getApplicationContext(), List.of(), null);

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
            adapter.addAll(new ArrayList<>(goals));
            adapter.notifyDataSetChanged();
        });

        //show the GoalListFragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, TodayListFragment.newInstance())
                .commit();

        setTitle("Today's Goals");

        //set the current view this main activity that we just set up
        setContentView(view.getRoot());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        var itemId = item.getItemId();

        // can't use switch here, need to use if statements
        if (itemId == R.id.today_view) {
            swapFragments(0);
            fragmentType = 0;
        } else if (itemId == R.id.tomorrow_view) {
            swapFragments(1);
            fragmentType = 1;
        } else if (itemId == R.id.pending_view) {
            swapFragments(2);
            fragmentType = 3;
        } else if (itemId == R.id.recurring_view) {
            swapFragments(3);
            fragmentType = 4;
        }

        return super.onOptionsItemSelected(item);
    }

    // Method to update the date
    private void updateDate() {
        if(advButtonClicked==false){
            currentDateTime = LocalDateTime.now(); // Update currentDateTime
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy", Locale.getDefault());
        String currentDate = currentDateTime.format(formatter);
        textViewDate.setText(currentDate);

        if(prevDate != null && !(prevDate.equals(currentDate)) && currentDateTime.getHour() >= 2){
            model.deleteCompleted();
            prevDate = currentDate;
        }

    }


    // Method to advance the date manually
    public void advanceDate() {
        currentDateTime = currentDateTime.plusDays(1);
        advButtonClicked = true;
        updateDate();
    }

    @Override
    public void onResume(){
        updateDate();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // Remove callbacks to prevent memory leaks
        handler.removeCallbacks(dateUpdater);
        super.onDestroy();
    }

    private void swapFragments(int fragmentNum) {
        // fragmentNum: 0 = today, 1 = tomorrow, 2 = pending, 3 = recurring
        switch (fragmentNum) {
            case 0:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, TodayListFragment.newInstance())
                        .commit();
                setTitle("Today's Goals");
                return;
            case 1:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, TomorrowListFragment.newInstance())
                        .commit();
                setTitle("Tomorrow's Goals");
                return;
            case 2:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, PendingListFragment.newInstance())
                        .commit();
                setTitle("Pending Goals");
                return;
            case 3:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, RecurringListFragment.newInstance())
                        .commit();
                setTitle("Recurring Goals");
                return;
            default:
                return;
        }
    }
    public MainViewModel getMainViewModel(){
        return this.model;
    }
}