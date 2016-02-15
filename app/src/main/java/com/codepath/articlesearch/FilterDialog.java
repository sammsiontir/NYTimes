package com.codepath.articlesearch;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;

import com.codepath.articlesearch.Models.Query;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FilterDialog extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener {
    @Bind(R.id.tvDate) TextView tvDate;
    @Bind(R.id.swSort) Switch swSort;
    @Bind(R.id.cbArts) CheckBox cbArts;
    @Bind(R.id.cbFashion) CheckBox cbFashion;
    @Bind(R.id.cbSports) CheckBox cbSport;

    private Query query;
    private final SimpleDateFormat FORMAT_DISPLAY = new SimpleDateFormat("yyyy-MM-dd");

    public interface FilterDialogListener {
        void onFinishFilterDialog(Query updatedQuery);
        void onFinishEditDialog(CheckBox )
    }


    public FilterDialog() {
    }

    public static FilterDialog newInstance(Query query) {
        FilterDialog frag = new FilterDialog();
        Bundle args = new Bundle();
        args.putString("title", "Advanced Filter");
        args.putParcelable("query", query);
        frag.setArguments(args);
        return frag;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        ButterKnife.bind(getDialog());
        // Get passing data
        String title = getArguments().getString("title", "Enter Name");
        query = getArguments().getParcelable("query");
        Log.d("DEBUG", query.toString());
        getDialog().setTitle(title);

        // bind views
        ButterKnife.bind(this, view);

        // Display passing data
        setFilterDisplay();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        query.onDateSet(year, monthOfYear,dayOfMonth);
        displayBeginDate();
    }

    public void displayBeginDate() {
        String date = FORMAT_DISPLAY.format(query.beginDate.getTime());
        Log.d("DEBUG", date);
        tvDate.setText(date);
    }

    /*
    public void editDate(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("query", query);
        datePickerFragment.setArguments(bundle);
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }
    */

    private void setFilterDisplay() {
        // begin date
        displayBeginDate();

        // desks
        // clear all
        cbArts.setChecked(false);
        cbFashion.setChecked(false);
        cbSport.setChecked(false);
        // set value
        for(int i = 0; i < query.desks.size(); i++) {
            switch (query.desks.get(i)) {
                case Query.ARTS:
                    cbArts.setChecked(true);
                    break;
                case Query.FASHION_AND_STYLE:
                    cbFashion.setChecked(true);
                    break;
                case Query.SPORTS:
                    cbSport.setChecked(true);
                    break;
                default:
                    break;
            }
        }
        // sort
        swSort.setChecked(query.sort.equals(Query.OLDEST));
        updateSwitchText();
    }

    public String getSwSort() {
        return (swSort.isChecked())? Query.OLDEST: Query.NEWEST;
    }

    public void update() {
        // begin date
        // updated when select from calendar

        // desks
        query.desks.clear();
        if(cbArts.isChecked()) query.desks.add(Query.ARTS);
        if(cbFashion.isChecked()) query.desks.add(Query.FASHION_AND_STYLE);
        if(cbSport.isChecked()) query.desks.add(Query.SPORTS);
        // sort
        query.sort = getSwSort();
    }

    public void btnSave(View view) {
        // update query
        update();

        // pass query back
        FilterDialogListener listener = (FilterDialogListener) getActivity();
        listener.onFinishFilterDialog(query);
        dismiss();
    }

    public void switchSort(View view) {
        updateSwitchText();
    }

    public void updateSwitchText() {
        if(swSort.isChecked()) {
            swSort.setText("OLDEST");
        }
        else {
            swSort.setText("NEWEST");
        }
    }

}
