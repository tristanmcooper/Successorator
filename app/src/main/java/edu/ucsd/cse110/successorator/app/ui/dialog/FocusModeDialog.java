package edu.ucsd.cse110.successorator.app.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDateTime;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.databinding.DialogFocusModeBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

//dialog pop up thing
public class FocusModeDialog extends DialogFragment{
    private DialogFocusModeBinding view;
    private MainViewModel activityModel;

    FocusModeDialog() {
        //Required empty public constructor
    }

    public static FocusModeDialog newInstance() {
        var fragment = new FocusModeDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = DialogFocusModeBinding.inflate(getLayoutInflater()); //change this

        return new AlertDialog.Builder(getActivity())
                .setTitle("Focus Mode")
                .setMessage("Select a focus.")
                .setView(view.getRoot())
                .setPositiveButton("Confirm", this::onPositiveButtonClick)
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

    // Edit this!!!! To-do!!! Select the focus? change the focus code here.
    private void onPositiveButtonClick(DialogInterface dialog, int which) {

        //view.
        RadioGroup focusOnGroup = view.focusModeRadioGroup;
        int selectedFocusTypeId = focusOnGroup.getCheckedRadioButtonId();
        RadioButton selectedFocusIdButton = view.getRoot().findViewById(selectedFocusTypeId);
        String selectedFocusString = selectedFocusIdButton.getText().toString();

        // Dismiss the dialog
            dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}
