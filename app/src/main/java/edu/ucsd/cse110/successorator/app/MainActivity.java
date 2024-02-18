package edu.ucsd.cse110.successorator.app;

import android.annotation.SuppressLint;
import android.os.Bundle;


import android.os.Handler;

import android.view.View;

import android.widget.Button;

import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.lifecycle.ViewModelProvider;


import edu.ucsd.cse110.successorator.app.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ActivityMainBinding view;

    private MainViewModel model; // won't need later when we do fragments

    private TextView textViewDate;
    private Handler handler;// won't need later when we do fragments

    private AppCompatImageButton buttonAdvanceDate;

    private Runnable dateUpdater;
    private Calendar currentCalendar;





    //sets up the initial main activity view xml
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_title);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        var dataSource = InMemoryDataSource.fromDefault();
        this.model = new MainViewModel(new GoalRepository(dataSource));
//        var modelOwner = this;
//        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
//        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
//        this.model = modelProvider.get(MainViewModel.class);

        this.view = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(R.layout.activity_main);
        //grab view by inflating xml layout file
        this.view = ActivityMainBinding.inflate(getLayoutInflater());


        textViewDate = findViewById(R.id.dateTextView);
        handler = new Handler();
        buttonAdvanceDate = findViewById(R.id.button_advance_date);
        // Initial update


        // Schedule periodic updates (e.g., every minute)





        currentCalendar = Calendar.getInstance();
        updateDate();
        // Initial update
        /*
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        TextView textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText(currentDate);

=======
        //set up the date
>>>>>>> 9aa69cf8a3a026c767941010bdbf229078107400
>>>>>>> Stashed changes
        String date = new SimpleDateFormat("EEEE, M/dd", Locale.getDefault()).format(new Date());
        TextView dateTextView = view.dateTextView;
        if (dateTextView != null) {
            dateTextView.setText(date);
        }
        setContentView(view.getRoot());
<<<<<<< Updated upstream
    }
}
=======

 */
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

    }


    // Method to update the date
    // Method to update the date
    private void updateDate() {
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(currentCalendar.getTime());
        textViewDate.setText(currentDate);
    }

    // Method to advance the date manually
    private void advanceDate() {
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
}
