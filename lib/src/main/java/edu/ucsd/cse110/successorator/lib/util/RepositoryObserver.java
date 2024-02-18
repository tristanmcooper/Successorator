package edu.ucsd.cse110.successorator.lib.util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import edu.ucsd.cse110.successorator.lib.domain.Goal;

public abstract class RepositoryObserver implements Observer<ArrayList<Goal>>{
    public abstract void onChanged(ArrayList<Goal> value);
}
