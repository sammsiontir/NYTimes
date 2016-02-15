package com.codepath.articlesearch.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.articlesearch.Adapters.ArticleArrayAdapter;
import com.codepath.articlesearch.EndlessRecyclerViewScrollListener;
import com.codepath.articlesearch.FilterDialog;
import com.codepath.articlesearch.Models.Query;
import com.codepath.articlesearch.Models.Response;
import com.codepath.articlesearch.R;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterDialog.FilterDialogListener {
    private final int REQUEST_FILTER = 21;
    private static final String BASEURL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

    private Query query;

    public void setQuery(Query query) {
        this.query = query;
    }

    private ArrayList<Response.Article> articles;
    private ArticleArrayAdapter aAdapter;

    @Bind(R.id.rvResults) RecyclerView rvResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Initialize private data members
        articles = new ArrayList<>();
        query = new Query();

        // Bind views in the layout
        ButterKnife.bind(this);

        // Create adapter passing in the sample user data
        aAdapter = new ArticleArrayAdapter(articles);
        // Attach the adapter to the recyclerview to populate items
        rvResults.setAdapter(aAdapter);
        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        // Set layout manager to position the items
        rvResults.setLayoutManager(gridLayoutManager);

        // Set item click behavior
        aAdapter.setOnItemClickListener(new ArticleArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // create an intent to display article
                Intent i = new Intent(getApplicationContext(), ArticleDetailActivity.class);
                // get the article to display
                Response.Article selectedArticle = articles.get(position);
                // pass the article into intent
                i.putExtra("url", selectedArticle.webUrl);
                // launch the activity
                startActivity(i);
            }
        });

        // Set scrolling behavior
        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page);
            }
        });
    }

    private void customLoadMoreDataFromApi(int page) {
        // Send an API request to retrieve appropriate data using the offset value as a parameter.
        // Deserialize API response and then construct new objects to append to the adapter
        // Add the new objects to the data source for the adapter
        // For efficiency purposes, notify the adapter of only the elements that got changed
        // curSize will equal to the index of the first element inserted because the list is 0-indexed
        // all aboves are in articleSearch function
        articleSearch();
    }


    public void clearResults() {
        query.clear();
        articles.clear();
        aAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // For Search bar
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String keywords) {
                // clear previous results
                clearResults();
                // perform query here
                query.query = keywords;
                articleSearch();
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // clear previous results
                clearResults();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_advaced_search) {
            FragmentManager fm = getSupportFragmentManager();
            FilterDialog editNameDialog = FilterDialog.newInstance(query);
            editNameDialog.show(fm, "fragment_edit_name");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_FILTER is defined above
        Log.d("DEBUG", query.toString());
        if (resultCode == RESULT_OK && requestCode == REQUEST_FILTER) {
            query = data.getParcelableExtra("query");
            Log.d("DEBUG", query.toString());
        }
    }

    public void articleSearch() {
        checkInternetStatus();
        AsyncHttpClient client = new AsyncHttpClient();

        Log.d("DEBUG", query.toString());
        client.get(BASEURL, query.getParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Response JSONResponse = null;
                try {
                    String responseString = response.getJSONObject("response").toString();
                    JSONResponse = gson.fromJson(responseString, Response.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                articles.addAll(JSONResponse.articles);
                // notify the adapter
                int curSize = aAdapter.getItemCount();
                aAdapter.notifyItemRangeInserted(curSize, articles.size() - 1);
            }
        });

        query.page++;
    }

    public void checkInternetStatus() {
        if(!isNetworkAvailable()) {
            String warningMessage = "No connection! Please check you network setting!";
            Toast.makeText(this, warningMessage, Toast.LENGTH_LONG).show();
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onFinishFilterDialog(Query updatedQuery) {
        query = updatedQuery;
    }
}
