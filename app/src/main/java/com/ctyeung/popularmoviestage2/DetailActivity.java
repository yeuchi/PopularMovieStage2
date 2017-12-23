package com.ctyeung.popularmoviestage2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.database.Cursor;
import android.view.ViewGroup;

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
import android.widget.LinearLayout;

public class DetailActivity extends AppCompatActivity
{
    private Toast toast;
    private ScrollView scrollView;
    private TextView tvTitle;
    private ImageView ivPoster;
    private TextView tvPlot;
    private TextView tvRating;
    private TextView tvReleaseDate;

    protected String id;
    private JSONObject json;
    private String title;
    private boolean isFavorite = false;

    private JSONArray mTrailerJsonArray;
    private JSONArray mReviewJsonArray;

    private DetailActivity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        scrollView = (ScrollView)findViewById(R.id.sv_detail);

        tvTitle = (TextView)findViewById(R.id.tv_original_title);
        ivPoster = (ImageView)findViewById(R.id.iv_poster_image);
        tvPlot = (TextView)findViewById(R.id.tv_plot);
        tvRating = (TextView)findViewById(R.id.tv_rating);
        tvReleaseDate = (TextView)findViewById(R.id.tv_release_date);

        thisActivity = this;
        initializeElements();
    }

    protected void initializeElements()
    {
        String str = this.getIntent().getStringExtra(Intent.EXTRA_TEXT);
        this.json = JSONhelper.parseJson(str);
        this.id = JSONhelper.parseValueByKey(this.json, MovieHelper.KEY_ID);

        String voteAverage = JSONhelper.parseValueByKey(this.json, MovieHelper.KEY_VOTE_AVERAGE);
        tvRating.setText("Vote Average: " + voteAverage);

        String date = JSONhelper.parseValueByKey(this.json, MovieHelper.KEY_RELEASE_DATE);
        tvReleaseDate.setText("Date: " + date);

        String plot = JSONhelper.parseValueByKey(this.json, MovieHelper.KEY_PLOT);
        tvPlot.setText(plot);

        this.title = JSONhelper.parseValueByKey(this.json, MovieHelper.KEY_ORIGINAL_TITLE);
        tvTitle.setText("Title: " + this.title);

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

        final Button btnTrailers = (Button)findViewById(R.id.btn_trailers);
        btnTrailers.setOnClickListener(trailersClickListener);

        final Button btnReviews = (Button)findViewById(R.id.btn_reviews);
        btnReviews.setOnClickListener(reviewsClickListener);

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

        requestVideos();
        requestReviews();
    }

    private void setBtnFavoriteText()
    {
        final Button button = (Button) findViewById(R.id.btnFavorite);
        int stringIndex = (isFavorite)?
                R.string.remove_favorite:
                R.string.mark_as_favorite;

        button.setText(getString(stringIndex));
    }

    /*
     * https://asishinwp.wordpress.com/2013/04/15/save-scrollview-position-resume-scrollview-from-that-position/
     */
  /*  public static int scrollX = 0;
    public static int scrollY = -1;

    @Override
    protected void onPause()
    {
        super.onPause();
        scrollX = scrollView.getScrollX();
        scrollY = scrollView.getScrollY();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
//this is important. scrollTo doesn't work in main thread.
        scrollView.post(new Runnable()
        {
            @Override
            public void run()
            {
                scrollView.scrollTo(scrollX, scrollY);
            }
        });
    }*/
/*
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putIntArray("SCROLL_POSITION",
                new int[]{ scrollView.getScrollX(), scrollView.getScrollY()});
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("SCROLL_POSITION");
        if(position != null)
            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.scrollTo(position[0], position[1]);
                }
            });
    }*/

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

    private View.OnClickListener trailersClickListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            if(toast!=null)
                toast.cancel();

            // launch detail activity
            Intent intent = new Intent(thisActivity, AdvocateActivity.class);
            String extraText = "{\"advocates\": "+ mTrailerJsonArray.toString() + "}";
            intent.putExtra(Intent.EXTRA_TEXT, extraText);
            startActivity(intent);

            // toast
            String toastmessage = "Trailers clicked";
            toast = Toast.makeText(thisActivity, toastmessage, Toast.LENGTH_LONG);
            toast.show();
        }
    };

    private View.OnClickListener reviewsClickListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            if(toast!=null)
                toast.cancel();

            // launch detail activity
            Intent intent = new Intent(thisActivity, AdvocateActivity.class);
            String extraText = "{\"advocates\": "+ mReviewJsonArray.toString() + "}";
            intent.putExtra(Intent.EXTRA_TEXT, extraText);
            startActivity(intent);

            // toast
            String toastmessage = "Reviews clicked";
            toast = Toast.makeText(thisActivity, toastmessage, Toast.LENGTH_LONG);
            toast.show();
        }
    };

    protected void requestVideos()
    {
        URL url = NetworkUtils.buildVideoUrl(this.id);
        new DetailActivity.GithubQueryTask().execute(url);
    }

    protected void requestReviews()
    {
        URL url = NetworkUtils.buildReviewUrl(this.id);
        new DetailActivity.GithubQueryTask().execute(url);
    }

    public class GithubQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return githubSearchResults;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(String str) {
            // *** parse str type for video or review
            JSONObject json = JSONhelper.parseJson(str);
            boolean typeVideo = MovieHelper.isVideo(str);

            if (null != json) {
                JSONArray jsonArray = JSONhelper.getJsonArray(json, "results");
                int size = jsonArray.length();
                Button button;
                String advocateString = String.valueOf(size) + " ";

                if (typeVideo) {
                    mTrailerJsonArray = jsonArray;
                    button = (Button) findViewById(R.id.btn_trailers);
                    advocateString += getString(R.string.trailer_header);
                } else {
                    mReviewJsonArray = jsonArray;
                    button = (Button) findViewById(R.id.btn_reviews);
                    advocateString += getString(R.string.review_header);
                }
                button.setText(advocateString);


            } else {
                // display info explaining 'no trailer or review' available
            }
        }
    }
}
