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

public class DetailActivity extends AppCompatActivity implements com.ctyeung.popularmoviestage2.ListAdapter.ListItemClickListener {

    private ListAdapter mAdapter;
    private RecyclerView mReviewList;
    private RecyclerView mTrailerList;
    private Toast mtoast;
    private ListAdapter.ListItemClickListener listener;

    private TextView tvTitle;
    private ImageView ivPoster;
    private TextView tvPlot;
    private TextView tv_rating;
    private TextView tv_release_date;
    private Button btnFavorite;

    private JSONArray trailerJsonArray;
    private JSONArray reviewJsonArray;

    protected String _id;
    private JSONObject _json;
    private String _title;
    private boolean _isFavorite = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = (TextView)findViewById(R.id.tv_original_title);
        ivPoster = (ImageView)findViewById(R.id.iv_poster_image);
        tvPlot = (TextView)findViewById(R.id.tv_plot);
        tv_rating = (TextView)findViewById(R.id.tv_rating);
        tv_release_date = (TextView)findViewById(R.id.tv_release_date);

        GridLayoutManager reviewManager = new GridLayoutManager(this, 1);
        mReviewList = (RecyclerView) findViewById(R.id.rv_reviews);
        mReviewList.setLayoutManager(reviewManager);

        GridLayoutManager trailerManager = new GridLayoutManager(this, 1);
        mTrailerList = (RecyclerView) findViewById(R.id.rv_trailers);
        mTrailerList.setLayoutManager(trailerManager);

        listener = this;
        initializeElements();
    }

    protected void initializeElements()
    {
        String str = this.getIntent().getStringExtra(Intent.EXTRA_TEXT);
        this._json = parseJson(str);

        this._id = parseValueByKey(this._json, MovieHelper.KEY_ID);

        String voteAverage = parseValueByKey(this._json, MovieHelper.KEY_VOTE_AVERAGE);
        tv_rating.setText("Vote Average: " + voteAverage);

        String date = parseValueByKey(this._json, MovieHelper.KEY_RELEASE_DATE);
        tv_release_date.setText("Date: " + date);

        String plot = parseValueByKey(this._json, MovieHelper.KEY_PLOT);
        tvPlot.setText(plot);

        this._title = parseValueByKey(this._json, MovieHelper.KEY_ORIGINAL_TITLE);
        tvTitle.setText("Title: " + this._title);

        // query db -- isFavorite if exists
        String[] columns = {"title"};
        String[] args = {this._title};
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                                        columns,
                                        "title=?",
                                        args,
                                        "title DESC");

        this._isFavorite = (0==cursor.getCount())? false : true;

        // label button pending on query result
        final Button button = (Button) findViewById(R.id.btnFavorite);
        button.setOnClickListener(buttonListener);
        setBtnFavoriteText();

        Context context = getApplicationContext();

        String url = MovieHelper.BASE_POSTER_URL +
                    MovieHelper.getSizeByIndex(MovieHelper.INDEX_DETAIL) +
                    parseValueByKey(this._json, MovieHelper.KEY_POSTER_PATH);

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

    private void setBtnFavoriteText()
    {
        final Button button = (Button) findViewById(R.id.btnFavorite);
        int stringIndex = (_isFavorite)?
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
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, _title);
            contentValues.put(MovieContract.MovieEntry.COLUMN_JSON_DETAIL, _json.toString());

            Uri uri = null;

            if(_isFavorite)
            {
                // delete
                getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, _title, null);
            }
            else
            {
                // Insert the content values via a ContentResolver
                uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
            }

            _isFavorite = !_isFavorite;
            setBtnFavoriteText();

            if(uri != null)
                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    };

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
        URL url = NetworkUtils.buildVideoUrl(this._id);
        new DetailActivity.GithubQueryTask().execute(url);
    }

    protected void requestReviews()
    {
        URL url = NetworkUtils.buildReviewUrl(this._id);
        new DetailActivity.GithubQueryTask().execute(url);
    }

    @Override
    public void onListItemClick(int clickItemIndex,
                                boolean isVideo)
    {
        if(mtoast!=null)
            mtoast.cancel();

        // launch detail activity

        JSONArray jsonArray = (isVideo)? trailerJsonArray : reviewJsonArray;
        JSONObject json = JSONhelper.parseJsonFromArray(jsonArray, clickItemIndex);

        // launch web
        String url = (isVideo)?
                MovieHelper.BASE_YOUTUBE_URL + JSONhelper.parseValueByKey(json, MovieHelper.KEY_TRAILER):
                JSONhelper.parseValueByKey(json, MovieHelper.KEY_REVIEW_URL);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);

        // toast
        String toastmessage = "Item #" + clickItemIndex + "clicked";
        mtoast = Toast.makeText(this, toastmessage, Toast.LENGTH_LONG);
        mtoast.show();
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
            boolean typeVideo = MovieHelper.isVideo(str);

            if(null != json)
            {
                JSONArray jsonArray = JSONhelper.getJsonArray(json, "results");
                int size = jsonArray.length();

                mAdapter = new com.ctyeung.popularmoviestage2.ListAdapter(size, listener, jsonArray, typeVideo);

                if(typeVideo)
                {
                    trailerJsonArray = jsonArray;
                    mTrailerList.setAdapter(mAdapter);
                    mTrailerList.setHasFixedSize(true);
                }
                else
                {
                    reviewJsonArray = jsonArray;
                    mReviewList.setAdapter(mAdapter);
                    mReviewList.setHasFixedSize(true);
                }
            }
            else
            {
                // display info explaining 'no trailer or review' available
            }
        }
    }
}
