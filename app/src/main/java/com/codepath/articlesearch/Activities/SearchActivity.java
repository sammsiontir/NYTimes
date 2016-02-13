package com.codepath.articlesearch.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.articlesearch.ArticleArrayAdapter;
import com.codepath.articlesearch.R;
import com.codepath.articlesearch.Response;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    private static final String KEY = "3220dd55c1e4a045bfb5df5b56a65b37:13:74337724";
    private static final String BASEURL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    private int pageNumber;

    private ArrayList<Response.Article> articles;
    private ArticleArrayAdapter aAdapter;

    @Bind(R.id.gvResults) RecyclerView gvResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Initialize private data members
        articles = new ArrayList<>();

        // Bind views in the layout
        ButterKnife.bind(this);

        // Create adapter passing in the sample user data
        aAdapter = new ArticleArrayAdapter(articles);
        // Attach the adapter to the recyclerview to populate items
        gvResults.setAdapter(aAdapter);
        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        // Set layout manager to position the items
        gvResults.setLayoutManager(gridLayoutManager);

        aAdapter.setOnItemClickListener(new ArticleArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Response.Article selectedArticle = articles.get(position);
                // create an intent to display article
                Intent i = new Intent(getApplicationContext(), ArticleDetailActivity.class);
                // get the article to display
                Response.Article article = articles.get(position);
                // pass the article into intent
                i.putExtra("url", article.webUrl);
                // launch the activity
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                articleSearch(query);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void articleSearch(String keyWrods) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api-key", KEY);
        params.put("page", pageNumber);
        params.put("q", keyWrods);

        client.get(BASEURL, params, new JsonHttpResponseHandler() {
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
                aAdapter.notifyDataSetChanged();
            }
        });

        pageNumber++;
    }
}
