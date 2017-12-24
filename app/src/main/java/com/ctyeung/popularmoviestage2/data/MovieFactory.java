package com.ctyeung.popularmoviestage2.data;

/**
 * Created by ctyeung on 12/24/17.
 */
import com.ctyeung.popularmoviestage2.utilities.JSONhelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieFactory
{
    public static List<Movie> CreateMovies(JSONArray movieList)
    {
        List<Movie> movies = new ArrayList<Movie>();
        for(int i=0; i<movieList.length(); i++)
        {
            JSONObject json = JSONhelper.parseJsonFromArray(movieList, i);
            Movie movie = new Movie(json);
            movies.add(i, movie);
        }
        return movies;
    }
}
