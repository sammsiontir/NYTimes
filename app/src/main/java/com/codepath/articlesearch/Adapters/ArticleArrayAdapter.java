package com.codepath.articlesearch.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.articlesearch.Models.Response;
import com.codepath.articlesearch.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArticleArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private static final String NYTIMESBASEURL = "http://www.nytimes.com/";
    private ArrayList<Response.Article> articles;
    private View resultView;
    private final int FULL = 0;
    private final int CONCISE = 1;

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

    public static class ViewHolderConcise extends RecyclerView.ViewHolder {
        @Bind(R.id.tvTitleConcise) TextView tvTitleConcise;
        public ViewHolderConcise(View view) {
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        RecyclerView.ViewHolder viewHolder;
        // Inflate the custom layout
        if (viewType == FULL) {
            resultView = inflater.inflate(R.layout.item_article_result, parent, false);
            viewHolder = new ViewHolder(resultView);
        }
        else {
            resultView = inflater.inflate(R.layout.item_article_result_concise, parent, false);
            viewHolder = new ViewHolderConcise(resultView);
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Response.Article article = articles.get(position);
        if (viewHolder.getItemViewType() == FULL) {
            ViewHolder vFull = (ViewHolder) viewHolder;
            Glide.with(resultView.getContext())
                    .load(NYTIMESBASEURL + article.multimedias.get(0).url)
                    .fitCenter()
                    .into(vFull.ivCoverImage);
            vFull.tvTitle.setText(article.headline.main);
        }
        else {
            ViewHolderConcise vConcise = (ViewHolderConcise) viewHolder;
            vConcise.tvTitleConcise.setText(article.headline.main);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (articles.get(position).multimedias.isEmpty()) {
            return CONCISE;
        }
        return FULL;
    }
}
