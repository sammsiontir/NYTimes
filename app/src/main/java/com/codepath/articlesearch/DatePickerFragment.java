package com.codepath.articlesearch;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.codepath.articlesearch.Models.Query;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener mActivity;
    Query query;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // if the Activity does not implement this interface, it will crash
        mActivity = (DatePickerDialog.OnDateSetListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        query = getArguments().getParcelable("query");

        final Calendar date = query.beginDate;
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of TimePickerDialog and return it
        // mActivity is the callback interface instance
        return new DatePickerDialog(getActivity(), mActivity, year, month, day);
    }

}
