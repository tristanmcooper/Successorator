package edu.ucsd.cse110.successorator.app;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.util.RepositoryObserver;

public class DisplayUpdater extends RepositoryObserver {
    private MainActivity mainActivity;

    public DisplayUpdater(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public void onChanged(ArrayList<Goal> value) {
//        mainActivity.reloadGoalsListView(value);
    }
}
