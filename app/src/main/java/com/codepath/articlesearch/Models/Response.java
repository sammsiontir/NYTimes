package com.codepath.articlesearch.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Response {

    @SerializedName("docs")
    public ArrayList<Article> articles;

    public class Article {
        @SerializedName("web_url")
        public String webUrl;

        @SerializedName("headline")
        public Headline headline;

        @SerializedName("multimedia")
        public ArrayList<Multimedia> multimedias;
    }


    public class Headline {

        @SerializedName("main")
        public String main;
    }

    public class Multimedia {
        @SerializedName("url")
        public String url;

        @SerializedName("width")
        public int width;

        @SerializedName("height")
        public int height;
    }

}
