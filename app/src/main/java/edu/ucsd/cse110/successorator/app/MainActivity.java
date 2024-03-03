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
import edu.ucsd.cse110.successorator.app.ui.dialog.TodayTomorrowAddGoalDialog;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding view;

    private MainViewModel model; // won't need later when we do fragments

    private TextView textViewDate;
    private Handler handler;// won't need later when we do fragments

    private AppCompatImageButton buttonAdvanceDate;

    private Runnable dateUpdater;
    private Calendar currentCalendar;
    private GoalListAdapter adapter;
    private String prevDate;
    private boolean dateForwarded;

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
        currentCalendar = Calendar.getInstance();
        updateDate();

        dateUpdater = new Runnable() {
            @Override
            public void run() {
                updateDate();
                // Schedule the next update
                handler.postDelayed(this, 60000);
            }
        };

        // Schedule periodic updates (e.g., every minute)
        handler.postDelayed(dateUpdater, 60000);

        // Button click listener for manual date advancement
        buttonAdvanceDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advanceDate();
            }
        });

        //default page is today's view, so connect the add goal button to
        //dialog for adding today goals
        view.addGoalButton.setOnClickListener(v -> {
            var dialogFragment = TodayTomorrowAddGoalDialog.newInstance();
            dialogFragment.show(getSupportFragmentManager(), "AddGoalDialog");
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

        dateForwarded = false;
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
        } else if (itemId == R.id.tomorrow_view) {
            swapFragments(1);
        } else if (itemId == R.id.pending_view) {
            swapFragments(2);
        } else if (itemId == R.id.recurring_view) {
            swapFragments(3);
        }

        return super.onOptionsItemSelected(item);
    }

    // Method to update the date
    private void updateDate() {
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(currentCalendar.getTime());
        textViewDate.setText(currentDate);

        if(prevDate != null && !(prevDate.equals(currentDate))){
            model.deleteCompleted();
        }
        prevDate = currentDate;
    }

    // Method to advance the date manually
    public void advanceDate() {
        // Advance the date by one day
        currentCalendar.add(Calendar.DAY_OF_MONTH, 1);
        updateDate(); // Update the TextView with the new date
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
                // Load fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, TodayListFragment.newInstance())
                        .commit();

                // Connect corresponding addGoalDialog
                view.addGoalButton.setOnClickListener(v -> {
                    var dialogFragment = TodayTomorrowAddGoalDialog.newInstance();
                    dialogFragment.show(getSupportFragmentManager(), "AddGoalDialog");
                });

                // TODO: set date display to today's date
                updateDate();
                setTitle("Today's Goals");
                return;
            case 1:
                // Load fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, TomorrowListFragment.newInstance())
                        .commit();

                // Connect corresponding addGoalDialog
                view.addGoalButton.setOnClickListener(v -> {
                    var dialogFragment = AddGoalDialog.newInstance();
                    dialogFragment.show(getSupportFragmentManager(), "AddGoalDialog");
                });

                // TODO: set date display to tomorrow's date
                setTitle("Tomorrow's Goals");
                return;
            case 2:
                // Load fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, PendingListFragment.newInstance())
                        .commit();

                // Connect corresponding addGoalDialog
                view.addGoalButton.setOnClickListener(v -> {
                    var dialogFragment = AddGoalDialog.newInstance();
                    dialogFragment.show(getSupportFragmentManager(), "AddGoalDialog");
                });

                // Remove date display
                textViewDate.setText(null);

                // Change menu bar display
                setTitle("Pending Goals");
                return;
            case 3:
                // Load fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, RecurringListFragment.newInstance())
                        .commit();

                // Connect corresponding addGoalDialog
                view.addGoalButton.setOnClickListener(v -> {
                    var dialogFragment = AddGoalDialog.newInstance();
                    dialogFragment.show(getSupportFragmentManager(), "AddGoalDialog");
                });

                // Remove date display
                textViewDate.setText(null);

                // Change menu bar display
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