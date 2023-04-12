package com.example.app.ui.register;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DatePickerRegisterFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), this, 2000, 0, 1);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        RegisterActivity activity = (RegisterActivity) getActivity();
        assert activity != null;
        activity.processDatePickerResult(year, month, day);
    }
}
