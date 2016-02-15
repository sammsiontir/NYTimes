package com.codepath.articlesearch.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;

import com.codepath.articlesearch.Activities.SearchActivity;
import com.codepath.articlesearch.Models.Query;
import com.codepath.articlesearch.R;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FilterDialog extends DialogFragment {
    @Bind(R.id.etDate) EditText etDate;
    @Bind(R.id.swSort) Switch swSort;
    @Bind(R.id.cbArts) CheckBox cbArts;
    @Bind(R.id.cbFashion) CheckBox cbFashion;
    @Bind(R.id.cbSports) CheckBox cbSport;
    @Bind(R.id.btnSave) Button btnSave;

    public Query query;
    private final SimpleDateFormat FORMAT_DISPLAY = new SimpleDateFormat("yyyy-MM-dd");

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
        query = ((SearchActivity)getActivity()).getQuery();
        Log.d("DEBUG", query.toString());
        getDialog().setTitle("Filter");

        // bind views
        ButterKnife.bind(this, view);

        // Display passing data
        setFilterDisplay();

        // Save
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
                ((SearchActivity) getActivity()).setQuery(query);
                //((SearchActivity) getActivity()).clearResults();
                //((SearchActivity) getActivity()).articleSearch();
                dismiss();
            }
        });

        // swSort
        swSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSort(v);
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setTargetFragment(FilterDialog.this, 300);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void displayBeginDate() {
        String date = FORMAT_DISPLAY.format(query.beginDate.getTime());
        Log.d("DEBUG", date);
        etDate.setText(date);
    }

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
