package com.ctyeung.popularmoviestage2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctyeung.popularmoviestage2.utilities.JSONhelper;
import com.ctyeung.popularmoviestage2.utilities.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ctyeung.popularmoviestage2.utilities.MovieHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {


    private TextView tvTitle;
    private ImageView ivPoster;
    private TextView tvPlot;
    private TextView tv_rating;
    private TextView tv_release_date;
    private TextView tvTrailer;

    protected String id;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = (TextView)findViewById(R.id.tv_original_title);
        ivPoster = (ImageView)findViewById(R.id.iv_poster_image);
        tvPlot = (TextView)findViewById(R.id.tv_plot);
        tv_rating = (TextView)findViewById(R.id.tv_rating);
        tv_release_date = (TextView)findViewById(R.id.tv_release_date);
        tvTrailer = (TextView)findViewById(R.id.tv_trailer);

        initializeElements();
    }

    protected void initializeElements()
    {
        String str = this.getIntent().getStringExtra(Intent.EXTRA_TEXT);
        JSONObject json = parseJson(str);

        id = parseValueByKey(json, MovieHelper.KEY_ID);

        String voteAverage = parseValueByKey(json, MovieHelper.KEY_VOTE_AVERAGE);
        tv_rating.setText("Vote Average: " + voteAverage);

        String date = parseValueByKey(json, MovieHelper.KEY_RELEASE_DATE);
        tv_release_date.setText("Date: " + date);

        String plot = parseValueByKey(json, MovieHelper.KEY_PLOT);
        tvPlot.setText(plot);

        String title = parseValueByKey(json, MovieHelper.KEY_ORIGINAL_TITLE);
        tvTitle.setText("Title: " + title);

        Context context = getApplicationContext();

        String url = MovieHelper.BASE_POSTER_URL +
                    MovieHelper.getSizeByIndex(MovieHelper.INDEX_DETAIL) +
                    parseValueByKey(json, MovieHelper.KEY_POSTER_PATH);

        Picasso.with(context)
                //.load("http://i.imgur.com/DvpvklR.png")
                .load(url)
                .placeholder(R.drawable.placeholder)   // optional
                .error(R.drawable.placeholder)      // optional
                .into(ivPoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        //Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        //Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
                    }
                });

        requestVideos();
        requestReviews();
    }

    protected JSONObject parseJson(String str)
    {
        JSONObject json = null;

        try
        {
            json = new JSONObject(str);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return json;
    }

    protected  String parseValueByKey(JSONObject json, String key)
    {
        String str = null;

        try
        {
            str = json.getString(key);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return str;
    }

    protected void requestVideos()
    {
        URL url = NetworkUtils.buildVideoUrl(id);
        new DetailActivity.GithubQueryTask().execute(url);
    }

    protected void requestReviews()
    {
        URL url = NetworkUtils.buildReviewUrl(id);
        new DetailActivity.GithubQueryTask().execute(url);
    }

    public class GithubQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String githubSearchResults = null;
            try
            {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            return githubSearchResults;
        }

        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        protected void onPostExecute(String str)
        {
            // *** parse str type for video or review
            JSONObject json = JSONhelper.parseJson(str);
            if(null != json)
            {
                jsonArray = JSONhelper.getJsonArray(json, "results");
                int size = jsonArray.length();

                if(null!=jsonArray && size>0)
                {
                    // *** need to replace this with list
                    String trailer = parseValueByKey(json, MovieHelper.KEY_TRAILER);
                    tvTrailer.setText(trailer);
                }
            }
            else
            {
                // display none ?  or do nothing ?
            }
        }
    }
}
