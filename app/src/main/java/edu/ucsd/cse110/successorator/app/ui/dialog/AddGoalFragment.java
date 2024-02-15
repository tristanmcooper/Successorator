package edu.ucsd.cse110.successorator.app.ui.dialog;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;


import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.databinding.FragmentDialogAddGoalsBinding;

//dialog pop up thing
public class AddGoalFragment extends DialogFragment{
    private FragmentDialogAddGoalsBinding view;
    private MainViewModel activityModel;

    AddGoalFragment() {
        //Required empty public constructor
    }

    public static AddGoalFragment newInstance() {
        var fragment = new AddGoalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = FragmentDialogAddGoalsBinding.inflate(getLayoutInflater());

        return new AlertDialog.Builder(getActivity())
                .setTitle("NewCard")
                .setMessage("Please provide the new card text.")
                .setView(view.getRoot())
                .setPositiveButton("Create", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner,modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }
    private void onPositiveButtonClick(DialogInterface dialog, int which) {

        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {

        dialog.cancel();
    }
}
