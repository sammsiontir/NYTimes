package com.codepath.articlesearch.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import com.codepath.articlesearch.Models.Query;
import com.codepath.articlesearch.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FilterActivity extends AppCompatActivity {
    @Bind(R.id.tvDate) TextView tvDate;
    @Bind(R.id.swSort) Switch swSort;
    @Bind(R.id.cbArts) CheckBox cbArts;
    @Bind(R.id.cbFashion) CheckBox cbFashion;
    @Bind(R.id.cbSports) CheckBox cbSport;

    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Bind with views
        ButterKnife.bind(this);
        // Get current filter setting
        query = getIntent().getParcelableExtra("query");
        Log.d("DEBUG", query.toString());
        // Display current filter setting
        setFilterDisplay();
    }

    private void setFilterDisplay() {
        // begin date
        if(!query.begin_date.isEmpty()) tvDate.setText(query.begin_date);

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
        Intent result = new Intent();
        result.putExtra("query", query);
        setResult(RESULT_OK, result);

        // finish
        this.finish();
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