package com.ctyeung.popularmoviestage2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.net.Uri;
import android.database.Cursor;

import com.ctyeung.popularmoviestage2.utilities.JSONhelper;
import com.ctyeung.popularmoviestage2.utilities.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ctyeung.popularmoviestage2.utilities.MovieHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import android.widget.Button;

import com.ctyeung.popularmoviestage2.data.MovieContract;
import com.ctyeung.popularmoviestage2.data.MovieDbHelper;

public class DetailActivity extends AppCompatActivity
        implements com.ctyeung.popularmoviestage2.ListAdapter.ListItemClickListener {

    private ListAdapter mTrailerAdapter;
    private ListAdapter mReviewAdapter;
    private RecyclerView mTrailerList;
    private RecyclerView mReviewList;
    private Toast _toast;
    private ListAdapter.ListItemClickListener mListener;

    private TextView tvTitle;
    private ImageView ivPoster;
    private TextView tvPlot;
    private TextView tvRating;
    private TextView tvReleaseDate;

    private JSONArray mTrailerJsonArray;
    private JSONArray mReviewJsonArray;

    private String id;
    private JSONObject json;
    private String title;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = (TextView)findViewById(R.id.tv_original_title);
        ivPoster = (ImageView)findViewById(R.id.iv_poster_image);
        tvPlot = (TextView)findViewById(R.id.tv_plot);
        tvRating = (TextView)findViewById(R.id.tv_rating);
        tvReleaseDate = (TextView)findViewById(R.id.tv_release_date);

        GridLayoutManager reviewManager = new GridLayoutManager(this, 1);
        mReviewList = (RecyclerView) findViewById(R.id.rv_reviews);
        mReviewList.setLayoutManager(reviewManager);

        GridLayoutManager trailerManager = new GridLayoutManager(this, 1);
        mTrailerList = (RecyclerView) findViewById(R.id.rv_trailers);
        mTrailerList.setLayoutManager(trailerManager);

        mListener = this;
        parseJSONContent();
        initializeElements();
        initializeAdvocateList();
        setScrollPosition();
    }

    private void setScrollPosition()
    {
        /*
         * default at top ... unless we have a previous position
         */
        final ScrollView scrollView = (ScrollView) findViewById(R.id.sv_scroller);
        scrollView.post(new Runnable()
        {
            public void run()
            {
                scrollView.fullScroll(View.FOCUS_UP);
            }
        });
    }

    private void parseJSONContent()
    {
        String str = this.getIntent().getStringExtra(Intent.EXTRA_TEXT);
        String[] list = str.split("_sep_");

        this.json = JSONhelper.parseJson(list[0]);
        mTrailerJsonArray = JSONhelper.getJsonArray(list[1], "results");
        mReviewJsonArray = JSONhelper.getJsonArray(list[2], "results");
    }

    private void initializeAdvocateList()
    {
        int size = 0;

        if(null!=mTrailerJsonArray)
        {
            size = mTrailerJsonArray.length();
            mTrailerAdapter = new com.ctyeung.popularmoviestage2.ListAdapter(size, mListener, mTrailerJsonArray, true);
            mTrailerList.setAdapter(mTrailerAdapter);
            mTrailerList.getLayoutParams().height = size * 180;
            mTrailerList.setHasFixedSize(true);

            TextView header = (TextView)findViewById(R.id.tv_trailer_header);
            header.setText(String.valueOf(size) + " " + getString(R.string.trailer_header));
        }

        if(null!=mReviewJsonArray)
        {
            size = mReviewJsonArray.length();
            mReviewAdapter = new com.ctyeung.popularmoviestage2.ListAdapter(size, mListener, mReviewJsonArray, false);
            mReviewList.setAdapter(mReviewAdapter);
            mReviewList.setHasFixedSize(true);

            TextView header = (TextView)findViewById(R.id.tv_review_header);
            header.setText(String.valueOf(size) + " " + getString(R.string.review_header));
        }
    }

    private void initializeElements()
    {
        this.id = JSONhelper.parseValueByKey(this.json, MovieHelper.KEY_ID);

        String voteAverage = JSONhelper.parseValueByKey(this.json, MovieHelper.KEY_VOTE_AVERAGE);
        tvRating.setText(getString(R.string.vote_average) + voteAverage);

        String date = JSONhelper.parseValueByKey(this.json, MovieHelper.KEY_RELEASE_DATE);
        tvReleaseDate.setText(getString(R.string.date) + date);

        String plot = JSONhelper.parseValueByKey(this.json, MovieHelper.KEY_PLOT);
        tvPlot.setText(plot);

        this.title = JSONhelper.parseValueByKey(this.json, MovieHelper.KEY_ORIGINAL_TITLE);
        tvTitle.setText(getString(R.string.title) + this.title);

        // query db -- isFavorite if exists
        String[] columns = {"title"};
        String[] args = {this.title};
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                columns,
                "title=?",
                args,
                "title DESC");

        this.isFavorite = (0==cursor.getCount())? false : true;

        // label button pending on query result
        final Button button = (Button) findViewById(R.id.btnFavorite);
        button.setOnClickListener(buttonListener);
        setBtnFavoriteText();

        Context context = getApplicationContext();

        String url = MovieHelper.BASE_POSTER_URL +
                MovieHelper.getSizeByIndex(MovieHelper.INDEX_DETAIL) +
                JSONhelper.parseValueByKey(this.json, MovieHelper.KEY_POSTER_PATH);

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
    }

    private void setBtnFavoriteText()
    {
        final Button button = (Button) findViewById(R.id.btnFavorite);
        int stringIndex = (isFavorite)?
                R.string.remove_favorite:
                R.string.mark_as_favorite;

        button.setText(getString(stringIndex));
    }

    private View.OnClickListener buttonListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            // Perform action on click -- favorite movie selected !
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            contentValues.put(MovieContract.MovieEntry.COLUMN_JSON_DETAIL, json.toString());

            Uri uri = null;

            if(isFavorite)
            {
                // delete
                getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, title, null);
            }
            else
            {
                // Insert the content values via a ContentResolver
                uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
            }

            isFavorite = !isFavorite;
            setBtnFavoriteText();

            if(uri != null)
                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onListItemClick(int clickItemIndex,
                                boolean isVideo)
    {
        if(_toast!=null)
            _toast.cancel();

        // launch detail activity
        JSONArray jsonArray = (isVideo)? mTrailerJsonArray : mReviewJsonArray;
        JSONObject json = JSONhelper.parseJsonFromArray(jsonArray, clickItemIndex);

        // launch web
        String url = (isVideo)?
                MovieHelper.BASE_YOUTUBE_URL + JSONhelper.parseValueByKey(json, MovieHelper.KEY_TRAILER):
                JSONhelper.parseValueByKey(json, MovieHelper.KEY_REVIEW_URL);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);

        // toast
        String toastmessage = getString(R.string.item_no)+" " + clickItemIndex + " "+ getString(R.string.clicked);
        _toast = Toast.makeText(this, toastmessage, Toast.LENGTH_LONG);
        _toast.show();
    }
}
