package edu.ucsd.cse110.successorator.lib.util;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class RepositorySubject implements Subject{
    private @Nullable ArrayList<Goal> value = null;
    private final List<Observer> observers = new java.util.ArrayList<>();

    @Override
    @Nullable
    public  ArrayList<Goal> getValue() {
        return value;
    }

    public void setValue(ArrayList<Goal> value) {
        this.value = value;
        notifyObservers();
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
        observer.onChanged(value);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers of the subject's new value. Used internally.
     */
    protected void notifyObservers() {
        for (var observer : observers) {
            observer.onChanged(value);
        }
    }
}
