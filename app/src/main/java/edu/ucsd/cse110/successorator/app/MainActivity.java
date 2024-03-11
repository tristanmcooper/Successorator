package edu.ucsd.cse110.successorator.app;

import android.annotation.SuppressLint;
import android.os.Bundle;


import android.os.Handler;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;

import android.widget.ImageButton;
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
import edu.ucsd.cse110.successorator.app.ui.dialog.FocusModeDialog;
import edu.ucsd.cse110.successorator.app.ui.dialog.RecurringAddGoalDialog;
import edu.ucsd.cse110.successorator.app.ui.dialog.TomorrowAddGoalDialog;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

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
    private LocalDateTime prevDate;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, M/d", Locale.getDefault());
        prevDate = currentDateTime;
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
            if (fragmentType == 0) {
                var dialogFragment = AddGoalDialog.newInstance();
                dialogFragment.show(getSupportFragmentManager(), "AddGoalFragment");
            } else if (fragmentType == 1) {
                var dialogFragment = TomorrowAddGoalDialog.newInstance();
                dialogFragment.show(getSupportFragmentManager(), "TomorrowAddGoalFragment");
            } else if (fragmentType == 3) {
                var dialogFragment = RecurringAddGoalDialog.newInstance();
                dialogFragment.show(getSupportFragmentManager(), "RecurringAddGoalFragment");
            }
        });

        // Find the ImageButton
        ImageButton focusModeButton = findViewById(R.id.focus_mode);

        focusModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFocusModeDialog();
            }
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
            refreshDatabase();
        } else if (itemId == R.id.tomorrow_view) {
            swapFragments(1);
            fragmentType = 1;
            refreshDatabase();
        } else if (itemId == R.id.pending_view) {
            swapFragments(2);
            fragmentType = 2;
        } else if (itemId == R.id.recurring_view) {
            swapFragments(3);
            fragmentType = 3;
        }

        return super.onOptionsItemSelected(item);
    }

    // Method to update the date
    public void updateDate() {
        if(advButtonClicked==false){
            currentDateTime = LocalDateTime.now(); // Update currentDateTime
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, M/d", Locale.getDefault());
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if(fragmentType==0 && fragment instanceof TodayListFragment){
            TodayListFragment todayfrag = (TodayListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (todayfrag != null) {
                todayfrag.updateDate(currentDateTime);
                LocalDateTime temp = todayfrag.getDate();
                String currentDate = temp.format(formatter);
                textViewDate.setText(currentDate);
            }
        }
        else if(fragmentType==1 && fragment instanceof TomorrowListFragment){
            TomorrowListFragment tmrwfrag = (TomorrowListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (tmrwfrag != null) {
                tmrwfrag.updateDate(currentDateTime);
                LocalDateTime temp = tmrwfrag.getDate();
                String currentDate = temp.format(formatter);
                textViewDate.setText(currentDate);
            }
        }
        else{
            String currentDate = currentDateTime.format(formatter);
            textViewDate.setText(currentDate);
        }


        if(prevDate != null && prevDate.getDayOfYear()!=currentDateTime.getDayOfYear()){
            model.deleteCompleted();
            //model.updateTomorrow();
            prevDate = currentDateTime;
            model.updateModelCurrentDate(currentDateTime);
            if(fragmentType==0){
                TodayListFragment todayfrag = (TodayListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (todayfrag != null) {
                    todayfrag.updateDate(currentDateTime);
                }
            }
            if(fragmentType==1){
                TomorrowListFragment tmrwfrag = (TomorrowListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (tmrwfrag != null) {
                    tmrwfrag.updateDate(currentDateTime);
                }
            }
            refreshDatabase();
        }

        model.updateModelCurrentDate(currentDateTime);
    }


    // Method to advance the date manually
    public void advanceDate() {
        currentDateTime = currentDateTime.plusDays(1);
        advButtonClicked = true;
        updateDate();
    }
    public void refreshDatabase(){
        model.addGoal(new Goal(-1,
                "",
                false,
                LocalDateTime.now().toString(),
                "Once",
                "Work"));
        model.removeSpecificGoal(-1);
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
                updateDate();
                return;
            case 1:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, TomorrowListFragment.newInstance())
                        .commit();
                setTitle("Tomorrow's Goals");
                updateDate();
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

    public TodayListFragment getTodayFrag(){
        TodayListFragment todayfrag = (TodayListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        return todayfrag;
    }
    public TomorrowListFragment getTTmrwFrag(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(fragment instanceof TomorrowListFragment){
            TomorrowListFragment getTTmrwFrag = (TomorrowListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            return getTTmrwFrag;
        }
        return null;
    }
    public MainViewModel getMainViewModel(){
        return this.model;
    }
    public void switchToTomorrowFrag(){
        swapFragments(1);
    }
    public void switchToTodayFrag(){
        swapFragments(0);
    }

    private void switchToFocusModeDialog() {
        String context = model.getContext();
        FocusModeDialog dialogFragment = FocusModeDialog.newInstance(context);
        dialogFragment.show(getSupportFragmentManager(), "FocusModeDialogFragment");
    }

    public void updateBackgroundColor(String context) {
        switch (context) {
            case "H":
                this.getWindow().getDecorView().setBackgroundResource(R.color.orange);
                break;
            case "W":
                this.getWindow().getDecorView().setBackgroundResource(R.color.blue);
                break;
            case "S":
                this.getWindow().getDecorView().setBackgroundResource(R.color.purple);
                break;
            case "E":
                this.getWindow().getDecorView().setBackgroundResource(R.color.green);
                break;
            default:
                this.getWindow().getDecorView().setBackgroundResource(R.color.white);
        }
    }
}