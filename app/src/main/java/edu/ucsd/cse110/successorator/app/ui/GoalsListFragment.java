package edu.ucsd.cse110.successorator.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;



public class GoalsListFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        this.view = FragmentAddGoalsBinding.inflate(inflater, container, false);
        view.cardList.setAdapter(adapter);

        view.createCardBUtton.setOnClickListener(v -> {
            var dialogFragment = CreateCardDialogFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "CreateCardDialogFragment");
        });
        return view.getRoot();
    }

}
