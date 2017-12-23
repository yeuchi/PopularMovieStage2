package com.ctyeung.popularmoviestage2;

import android.content.Intent;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ctyeung.popularmoviestage2.utilities.JSONhelper;
import com.ctyeung.popularmoviestage2.utilities.MovieHelper;
import com.ctyeung.popularmoviestage2.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by ctyeung on 12/21/17.
 */

public class AdvocateActivity extends AppCompatActivity
        implements com.ctyeung.popularmoviestage2.ListAdapter.ListItemClickListener {

    private ListAdapter mAdapter;
    private Toast toast;

    private RecyclerView mRecyclerView;
    private ListAdapter.ListItemClickListener mListener;

    private JSONArray mTrailerJsonArray;
    private JSONArray mReviewJsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        GridLayoutManager reviewManager = new GridLayoutManager(this, 1);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_advocates);
        mRecyclerView.setLayoutManager(reviewManager);

        mListener = this;
    }

    private void initListElements()
    {
        String str = this.getIntent().getStringExtra(Intent.EXTRA_TEXT);
        boolean typeVideo = MovieHelper.isVideo(str);
        JSONObject json = JSONhelper.parseJson(str);
        JSONArray jsonArray = JSONhelper.getJsonArray(json, "advocates");
        int size = jsonArray.length();

        mAdapter = new com.ctyeung.popularmoviestage2.ListAdapter(size, mListener, jsonArray, typeVideo);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
    }


        @Override
    public void onListItemClick(int clickItemIndex,
                                boolean isVideo)
    {
        if(toast!=null)
            toast.cancel();

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
        String toastmessage = "Item #" + clickItemIndex + "clicked";
        toast = Toast.makeText(this, toastmessage, Toast.LENGTH_LONG);
        toast.show();
    }

}
