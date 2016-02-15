package com.codepath.articlesearch.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.loopj.android.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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

    // Calendar format
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final int OLDEST_YEAR = 1851;
    private static final int OLDEST_MONTH = 01;
    private static final int OLDEST_DAY = 01;


    // Basic Search
    public int page;
    public String query;

    // Advanced Search
    public String sort;        // newest | oldest
    public Calendar beginDate;
    public String begin_date;  // YYYYMMDD
    public String news_desk;   // &fq=news_desk:("Sports" "Foreign")
    public ArrayList<String> desks;

    public Query() {
        this.page = 0;
        this.query = "";
        this.sort = "newest";
        onDateSet(OLDEST_YEAR, OLDEST_MONTH, OLDEST_DAY);
        this.news_desk = "";
        this.desks = new ArrayList<>();
    }

    public void onDateSet(int year, int monthOfYear, int dayOfMonth) {
        this.beginDate = Calendar.getInstance();
        // store the values selected into a Calendar instance
        this.beginDate.set(Calendar.YEAR, year);
        this.beginDate.set(Calendar.MONTH, monthOfYear);
        this.beginDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        transformDate();
    }

    public void transformDate() {
        this.begin_date = FORMAT.format(this.beginDate.getTime());
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
        dest.writeSerializable(this.beginDate);
        dest.writeString(this.begin_date);
        dest.writeString(this.news_desk);
        dest.writeStringList(this.desks);
    }

    protected Query(Parcel in) {
        this.page = in.readInt();
        this.query = in.readString();
        this.sort = in.readString();
        this.beginDate = (Calendar) in.readSerializable();
        this.begin_date = in.readString();
        this.news_desk = in.readString();
        this.desks = in.createStringArrayList();
    }

    public static final Creator<Query> CREATOR = new Creator<Query>() {
        public Query createFromParcel(Parcel source) {
            return new Query(source);
        }

        public Query[] newArray(int size) {
            return new Query[size];
        }
    };
}
