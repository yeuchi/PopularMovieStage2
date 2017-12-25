package com.ctyeung.popularmoviestage2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import java.util.List;

import com.ctyeung.popularmoviestage2.data.MovieContract;
import com.ctyeung.popularmoviestage2.data.MovieFactory;
import com.ctyeung.popularmoviestage2.data.SharedPrefUtility;
import com.ctyeung.popularmoviestage2.utilities.JSONhelper;
import com.ctyeung.popularmoviestage2.data.Movie;
import java.io.IOException;
import java.net.URL;
import android.content.res.Configuration;

import com.ctyeung.popularmoviestage2.utilities.MovieHelper;
import com.ctyeung.popularmoviestage2.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements MovieGridAdapter.ListItemClickListener
{
    private MovieGridAdapter mAdapter;
    private RecyclerView mNumbersList;
    private Toast mToast;
    private MovieGridAdapter.ListItemClickListener mListener;
    private List<Movie> movies;
    private ProgressBar mLoadingIndicator;
    private TextView tvNetworkErrorDisplay;
    private JSONObject json;
    private String trailerString = null;
    private String reviewString = null;
    private String mSortMethod = MovieHelper.SORT_POPULAR;
    private int scrollY=0;

    private SharedPrefUtility sharedPrefUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNumbersList = (RecyclerView) findViewById(R.id.rv_movie);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_display_progress);
        tvNetworkErrorDisplay = (TextView) findViewById(R.id.tv_network_error_display);


        GridLayoutManager layoutManager = new GridLayoutManager(this, NumberColumns());
        mNumbersList.setLayoutManager(layoutManager);
        mListener = this;


        sharedPrefUtility = new SharedPrefUtility(getApplicationContext());
        requestMovies(sharedPrefUtility.getSortMethod());

        // scroll to expected position
        scrollY = sharedPrefUtility.getScrollPos(SharedPrefUtility.MAIN_SCROLL);
        mNumbersList.smoothScrollToPosition(scrollY);

        // set scroll event listener
        mNumbersList.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                scrollY += dy;
                sharedPrefUtility.setScroll(SharedPrefUtility.MAIN_SCROLL, scrollY);
            }
        });
    }

    private int NumberColumns()
    {
        int orientation = getResources().getConfiguration().orientation;

        switch (orientation)
        {
            case Configuration.ORIENTATION_LANDSCAPE:
                return 3;

            default:
            case Configuration.ORIENTATION_PORTRAIT:
                return 2;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {

            case R.id.sort_popular:
                mSortMethod = MovieHelper.SORT_POPULAR;
                break;

            case R.id.sort_top_rated:
                mSortMethod = MovieHelper.SORT_TOP_RATED;
                break;

            case R.id.sort_favorite:
                mSortMethod = MovieHelper.SORT_FAVORITE;
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        scrollY = 0;
        sharedPrefUtility.setScroll(SharedPrefUtility.MAIN_SCROLL, scrollY);
        sharedPrefUtility.setSortMethod(mSortMethod);
        requestMovies(mSortMethod);
        return true;
    }

    protected void loadMovieFavorites()
    {
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                "title DESC");

        if(cursor.getCount() > 0)
        {
            movies.clear();
            cursor.moveToFirst();
            int i=0;
            while (cursor.isAfterLast() == false)
            {
                String value = cursor.getString(2);
                JSONObject json = JSONhelper.parseJson(value);
                Movie movie = new Movie((json));
                movies.add(i++, movie);
                cursor.moveToNext();
            }
            populateMovieGrid();
        }
        else
        {
            Toast.makeText(getBaseContext(), getString(R.string.no_favorite), Toast.LENGTH_LONG).show();
        }
    }

    /*
     * load movie thumbs for main page
     */
    protected void requestMovies(String sortMethod)
    {
        switch(sortMethod)
        {
            case MovieHelper.SORT_FAVORITE:
                loadMovieFavorites();
                break;

            default:
                URL url = NetworkUtils.buildMainPageUrl(sortMethod);
                GithubQueryTask task = new GithubQueryTask(GithubQueryTask.METHOD_THUMBS);
                task.execute(url);
                break;
        }
    }

    /*
     * Refactor this class with VideoQueryTask later !!!!!
     */
    public class GithubQueryTask extends AsyncTask<URL, Void, String>
    {
        public static final String METHOD_THUMBS = "THUMBS";
        public static final String METHOD_TRAILERS = "TRAILERS";
        public static final String METHOD_REVIEWS = "REVIEWS";
        private String mMethod;
        private Movie mSelectedMovie;

        public GithubQueryTask(String method)
        {
            this.mMethod = method;
            this.mSelectedMovie = null;
        }

        public GithubQueryTask(Movie selectedMovie,
                               String method)
        {
            this.mMethod = method;
            this.mSelectedMovie = selectedMovie;
        }

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

            switch(mMethod)
            {
                case METHOD_THUMBS:
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    tvNetworkErrorDisplay.setVisibility(View.INVISIBLE);
                    break;

                case METHOD_TRAILERS:
                case METHOD_REVIEWS:
                    break;

            }
        }

        protected void onPostExecute(String str)
        {
            switch (mMethod) {
                case METHOD_THUMBS:
                    handleMovieData(str);
                    break;

                case METHOD_TRAILERS:
                case METHOD_REVIEWS:
                    handleAdvocateData(str);
                    break;
            }

        }

        private void handleAdvocateData(String str)
        {
            boolean typeVideo = MovieHelper.isVideo(str);

            if(null != mSelectedMovie)
            {
                if(typeVideo)
                {
                    trailerString = str;
                }
                else
                {
                    reviewString = str;
                }

                // launch detail page when we have all content (selection, trailers, reviews json)
                if(null!=trailerString &&
                        null!=reviewString)
                    launchDetailActivity(mSelectedMovie);
            }
            else
            {
                // display info explaining 'no selection, trailer or review' available
                tvNetworkErrorDisplay.setVisibility(View.VISIBLE);
            }
        }

        private void handleMovieData(String str)
        {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            JSONObject json = JSONhelper.parseJson(str);

            if(null != json)
            {
                JSONArray jsonArray = JSONhelper.getJsonArray(json, "results");
                movies = MovieFactory.CreateMovies(jsonArray);

                if(null!=movies &&
                        movies.size()>0)
                {
                    populateMovieGrid();
                    return;
                }
            }

            // display error if no data is available
            tvNetworkErrorDisplay.setVisibility(View.VISIBLE);
        }
    }

    private void populateMovieGrid()
    {
        mAdapter = new MovieGridAdapter(movies, mListener);
        mNumbersList.setAdapter(mAdapter);
        mNumbersList.setHasFixedSize(true);
    }

    @Override
    public void onListItemClick(int clickItemIndex)
    {
        if(mToast!=null)
            mToast.cancel();

        // launch detail activity
        Movie selectedMovie = movies.get(clickItemIndex);
        String id = selectedMovie.getId();

        requestTrailers(selectedMovie, id);
        requestReviews(selectedMovie, id);
    }

    private void requestTrailers(Movie selectedMovie,
                                 String id)
    {
        URL url = NetworkUtils.buildVideoUrl(id);
        GithubQueryTask task = new GithubQueryTask(selectedMovie, GithubQueryTask.METHOD_TRAILERS);
        task.execute(url);
    }

    private void requestReviews(Movie selectedMovie,
                                String id)
    {
        URL url = NetworkUtils.buildReviewUrl(id);
        GithubQueryTask task = new GithubQueryTask(selectedMovie, GithubQueryTask.METHOD_REVIEWS);
        task.execute(url);
    }

    private void launchDetailActivity(Movie selectedMovie)
    {
        // start page at top
        sharedPrefUtility.setScroll(SharedPrefUtility.DETAIL_SCROLL, 0);

        Intent intent = new Intent(this, DetailActivity.class);
        String mergeString = selectedMovie.getJSONString() + "_sep_" +
                            trailerString + "_sep_" +
                            reviewString;
        intent.putExtra(Intent.EXTRA_TEXT, mergeString);
        startActivity(intent);
/*
        // toast
        String toastmessage = "Item #" + clickItemIndex + "clicked";
        mToast = Toast.makeText(this, toastmessage, Toast.LENGTH_LONG);
        mToast.show();
        */
    }

}
