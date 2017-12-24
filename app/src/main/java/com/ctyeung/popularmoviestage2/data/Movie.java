package com.ctyeung.popularmoviestage2.data;

import com.ctyeung.popularmoviestage2.utilities.JSONhelper;
import com.ctyeung.popularmoviestage2.utilities.MovieHelper;

import org.json.JSONObject;

/**
 * Created by ctyeung on 12/24/17.
 */

public class Movie {

    private JSONObject json;

    public Movie()
    {

    }

    public Movie(JSONObject json)
    {
        this.json = json;
    }

    public String getTitle()
    {
        return JSONhelper.parseValueByKey(json, MovieHelper.KEY_TITLE);
    }

    public String getId()
    {
        return JSONhelper.parseValueByKey(json, MovieHelper.KEY_ID);
    }

    public String getPosterUrl()
    {
        return MovieHelper.BASE_POSTER_URL +
                MovieHelper.getSizeByIndex(MovieHelper.INDEX_THUMBNAIL) +
                JSONhelper.parseValueByKey(json, MovieHelper.KEY_POSTER_PATH);
    }

    public String getJSONString()
    {
        return json.toString();
    }

}
