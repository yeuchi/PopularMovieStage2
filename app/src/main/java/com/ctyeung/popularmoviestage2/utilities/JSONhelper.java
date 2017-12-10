package com.ctyeung.popularmoviestage2.utilities;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ctyeung on 8/18/17.
 */

public class JSONhelper
{
    public static JSONObject parseJson(String str)
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

    public static JSONArray getJsonArray(JSONObject json, String key)
    {
        JSONArray jsonArray = null;
        try
        {
            jsonArray = json.getJSONArray("results");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return jsonArray;
    }

    public static JSONObject parseJsonFromArray(JSONArray jsonArray, int index)
    {
        JSONObject json = null;

        try
        {
            json = jsonArray.getJSONObject(index);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return json;
    }

    public static String parseValueByKey(JSONObject json, String key)
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
}
