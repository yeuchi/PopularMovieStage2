package com.ctyeung.popularmoviestage2;

import android.content.Intent;
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

import com.ctyeung.popularmoviestage2.data.MovieContract;
import com.ctyeung.popularmoviestage2.utilities.JSONhelper;
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
    private JSONArray mJsonArray;
    private ProgressBar mLoadingIndicator;
    private TextView tvNetworkErrorDisplay;

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

        requestMovies(MovieHelper.SORT_POPULAR);
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
                requestMovies(MovieHelper.SORT_POPULAR);
                return true;

            case R.id.sort_top_rated:
                requestMovies(MovieHelper.SORT_TOP_RATED);
                return true;

            case R.id.sort_favorite:
                loadMovieFavorites();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
            mJsonArray = new JSONArray();
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false)
            {
                String value = cursor.getString(2);
                JSONObject json = JSONhelper.parseJson(value);
                mJsonArray.put(json);
                cursor.moveToNext();
            }
            populateMovieGrid();
        }
        else
            Toast.makeText(getBaseContext(), "no favorites", Toast.LENGTH_LONG).show();

    }

    protected void requestMovies(String sortMethod)
    {
        URL url = NetworkUtils.buildMainPageUrl(sortMethod);
        new GithubQueryTask().execute(url);
    }

    public class GithubQueryTask extends AsyncTask<URL, Void, String>
    {
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
            mLoadingIndicator.setVisibility(View.VISIBLE);
            tvNetworkErrorDisplay.setVisibility(View.INVISIBLE);
        }

        protected void onPostExecute(String str)
        {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            JSONObject json = JSONhelper.parseJson(str);

            if(null != json)
            {
                mJsonArray = JSONhelper.getJsonArray(json, "results");

                if(null!=mJsonArray &&
                        mJsonArray.length()>0)
                {
                    populateMovieGrid();
                }
            }
            else
                tvNetworkErrorDisplay.setVisibility(View.VISIBLE);
        }
    }

    private void populateMovieGrid()
    {
        int size = mJsonArray.length();
        mAdapter = new MovieGridAdapter(size, mListener, mJsonArray);
        mNumbersList.setAdapter(mAdapter);
        mNumbersList.setHasFixedSize(true);
    }

    @Override
    public void onListItemClick(int clickItemIndex)
    {
        if(mToast!=null)
            mToast.cancel();

        // launch detail activity
        JSONObject json = JSONhelper.parseJsonFromArray(mJsonArray, clickItemIndex);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, json.toString());
        startActivity(intent);

        // toast
        String toastmessage = "Item #" + clickItemIndex + "clicked";
        mToast = Toast.makeText(this, toastmessage, Toast.LENGTH_LONG);
        mToast.show();
    }
}
