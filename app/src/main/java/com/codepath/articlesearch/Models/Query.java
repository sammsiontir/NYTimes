package com.codepath.articlesearch.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

/**
 * Created by chengfu_lin on 2/14/16.
 */
public class Query implements Parcelable {
    // Params keywords
    private static final String APIKEY = "api-key";
    private static final String PAGE = "page";
    private static final String BEGIN_DATE = "begin_date";
    private static final String NEWS_DESK = "fq";
    private static final String SORT = "sort";
    private static final String QUERY = "q";
    private static final String KEY = "24c318449f40e9695d3ff025f7cf7ba1:11:54196591";

    // News desk keywords
    public static final String NEWEST = "newest";
    public static final String OLDEST = "oldest";
    public static final String ARTS   = "Arts";
    public static final String FASHION_AND_STYLE = "Fashion & Style";
    public static final String SPORTS = "Sports";


    // Basic Search
    public int page;
    public String query;

    // Advanced Search
    public String sort;        // newest | oldest
    public String begin_date;  // YYYYMMDD
    public String news_desk;   // &fq=news_desk:("Sports" "Foreign")
    public ArrayList<String> desks;

    public void setNewsDesk() {
        news_desk = "news_desk:(\"Sports\" \"Arts\")";
    }

    public Query() {
        this.page = 0;
        this.query = "";
        this.sort = "newest";
        this.begin_date = "";
        this.news_desk = "";
        this.desks = new ArrayList<>();
    }

    // return Parameter for AsyncHttpClient
    public RequestParams getParams() {
        RequestParams params = new RequestParams();

        params.put(APIKEY, this.KEY);
        params.put(PAGE, this.page);
        params.put(QUERY, this.query);

        if(!begin_date.isEmpty()) {
            params.put(BEGIN_DATE, this.begin_date);
        }
        if(!desks.isEmpty()) {
            collectDesk();
            params.put(NEWS_DESK, this.news_desk);
        }
        params.put(SORT, this.sort);
        return params;
    }

    public void clear() {
        this.page = 0;
        this.query = "";
    }

    public void collectDesk() {
        // format: news_desk:("Sports" "Foreign")
        news_desk = "news_desk:(";
        for(int i = 0; i < desks.size(); i++) {
            news_desk = news_desk + desks.get(i);
            if(i+1 < desks.size()) {
                news_desk = news_desk + " ";
            }
        }
        news_desk = news_desk + ")";

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.page);
        dest.writeString(this.query);
        dest.writeString(this.sort);
        dest.writeString(this.begin_date);
        dest.writeString(this.news_desk);
        dest.writeStringList(this.desks);
    }

    protected Query(Parcel in) {
        this.page = in.readInt();
        this.query = in.readString();
        this.sort = in.readString();
        this.begin_date = in.readString();
        this.news_desk = in.readString();
        this.desks = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Query> CREATOR = new Parcelable.Creator<Query>() {
        public Query createFromParcel(Parcel source) {
            return new Query(source);
        }

        public Query[] newArray(int size) {
            return new Query[size];
        }
    };
}
