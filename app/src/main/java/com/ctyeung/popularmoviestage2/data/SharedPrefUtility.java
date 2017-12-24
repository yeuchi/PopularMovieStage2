package com.ctyeung.popularmoviestage2.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ctyeung.popularmoviestage2.utilities.MovieHelper;

/**
 * Created by ctyeung on 12/24/17.
 */

public class SharedPrefUtility
{
    SharedPreferences sharedPreferences;

    public static final String mypreference = "mypref";
    public static final String SORT_METHOD = "sort";

    private Context context;

    public SharedPrefUtility(Context context)
    {
        this.context = context;
    }

    public String getSortMethod()
    {
        sharedPreferences = this.context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        return (sharedPreferences.contains(SORT_METHOD))?
                sharedPreferences.getString(SORT_METHOD, MovieHelper.SORT_POPULAR):
                MovieHelper.SORT_POPULAR;
    }

    public void setSortMethod(String method)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SORT_METHOD, method);
        editor.commit();
    }
}
