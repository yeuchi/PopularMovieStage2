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

    private ListAdapter _adapter;
    private RecyclerView _reviewList;
    private RecyclerView _trailerList;
    private Toast _toast;
    private ListAdapter.ListItemClickListener _listener;

    private TextView _tvTitle;
    private ImageView _ivPoster;
    private TextView _tvPlot;
    private TextView _tvRating;
    private TextView _tvReleaseDate;
    private Button _btnFavorite;

    private JSONArray _trailerJsonArray;
    private JSONArray _reviewJsonArray;

    protected String _id;
    private JSONObject _json;
    private String _title;
    private boolean _isFavorite = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        _tvTitle = (TextView)findViewById(R.id.tv_original_title);
        _ivPoster = (ImageView)findViewById(R.id.iv_poster_image);
        _tvPlot = (TextView)findViewById(R.id.tv_plot);
        _tvRating = (TextView)findViewById(R.id.tv_rating);
        _tvReleaseDate = (TextView)findViewById(R.id.tv_release_date);

        GridLayoutManager reviewManager = new GridLayoutManager(this, 1);
        _reviewList = (RecyclerView) findViewById(R.id.rv_reviews);
        _reviewList.setLayoutManager(reviewManager);

        GridLayoutManager trailerManager = new GridLayoutManager(this, 1);
        _trailerList = (RecyclerView) findViewById(R.id.rv_trailers);
        _trailerList.setLayoutManager(trailerManager);

        _listener = this;
        initializeElements();
    }

    protected void initializeElements()
    {
        String str = this.getIntent().getStringExtra(Intent.EXTRA_TEXT);
        this._json = parseJson(str);

        this._id = parseValueByKey(this._json, MovieHelper.KEY_ID);

        String voteAverage = parseValueByKey(this._json, MovieHelper.KEY_VOTE_AVERAGE);
        _tvRating.setText("Vote Average: " + voteAverage);

        String date = parseValueByKey(this._json, MovieHelper.KEY_RELEASE_DATE);
        _tvReleaseDate.setText("Date: " + date);

        String plot = parseValueByKey(this._json, MovieHelper.KEY_PLOT);
        _tvPlot.setText(plot);

        this._title = parseValueByKey(this._json, MovieHelper.KEY_ORIGINAL_TITLE);
        _tvTitle.setText("Title: " + this._title);

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
                .into(_ivPoster, new Callback() {
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
        if(_toast!=null)
            _toast.cancel();

        // launch detail activity

        JSONArray jsonArray = (isVideo)? _trailerJsonArray : _reviewJsonArray;
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
        _toast = Toast.makeText(this, toastmessage, Toast.LENGTH_LONG);
        _toast.show();
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

                _adapter = new com.ctyeung.popularmoviestage2.ListAdapter(size, _listener, jsonArray, typeVideo);

                if(typeVideo)
                {
                    _trailerJsonArray = jsonArray;
                    _trailerList.setAdapter(_adapter);
                    _trailerList.setHasFixedSize(true);
                }
                else
                {
                    _reviewJsonArray = jsonArray;
                    _reviewList.setAdapter(_adapter);
                    _reviewList.setHasFixedSize(true);
                }
            }
            else
            {
                // display info explaining 'no trailer or review' available
            }
        }
    }
}
