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
    public static final String MAIN_SCROLL = "mainScroll";
    public static final String DETAIL_SCROLL = "detailScroll";

    private Context context;

    public SharedPrefUtility(Context context)
    {
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
    }

    public String getSortMethod()
    {
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

    public int getScrollPos(String page)
    {
        return (sharedPreferences.contains(page))?
                sharedPreferences.getInt(page, 0):
                0;
    }

    public void setScroll(String page, int position)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(page, position);
        editor.commit();
    }
}
