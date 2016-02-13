package com.codepath.articlesearch.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.articlesearch.Models.Response;
import com.codepath.articlesearch.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArticleArrayAdapter extends RecyclerView.Adapter<ArticleArrayAdapter.ViewHolder>  {
    private static final String NYTIMESBASEURL = "http://www.nytimes.com/";
    private ArrayList<Response.Article> articles;
    private View resultView;

    // Define listener member variable
    private static OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivCoverImage) ImageView ivCoverImage;
        @Bind(R.id.tvTitle) TextView tvTitle;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }

    public ArticleArrayAdapter(ArrayList<Response.Article> articles) {
        this.articles = articles;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        resultView = inflater.inflate(R.layout.item_article_result, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(resultView);
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Response.Article article = articles.get(position);

        viewHolder.tvTitle.setText(article.headline.main);
        if(!article.multimedias.isEmpty()) {
            Picasso.with(resultView.getContext())
                    .load(NYTIMESBASEURL + article.multimedias.get(0).url)
                    .fit()
                    .into(viewHolder.ivCoverImage);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}
