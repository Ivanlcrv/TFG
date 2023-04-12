package com.example.app.ui.account;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DatePickerAccountFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

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
        AccountFragment fragment = AccountFragment.getInstance();
        fragment.processDatePickerResult(year, month, day);
    }
}

