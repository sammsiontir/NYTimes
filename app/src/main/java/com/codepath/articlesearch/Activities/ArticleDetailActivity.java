package com.codepath.articlesearch.Activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codepath.articlesearch.R;

public class ArticleDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String url = getIntent().getStringExtra("url");
        WebView wvArticle = (WebView) findViewById(R.id.wvArticle);
        final MaterialDialog prDialog = new MaterialDialog.Builder(this)
                .title("Loading website")
                .content("Please wait")
                .progress(true, 0).show();

        wvArticle.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                prDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                prDialog.dismiss();
            }
        });
        wvArticle.loadUrl(url);

    }

}
