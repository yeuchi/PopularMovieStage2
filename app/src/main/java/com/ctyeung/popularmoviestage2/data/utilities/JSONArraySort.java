package com.ctyeung.popularmoviestage2.data.utilities;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ctyeung on 8/19/17.
 */

public class JSONArraySort
{
    private JSONArray _jsonArray;
    private String _key;
    private JSONArray _sorted;

    public JSONArraySort(JSONArray jsonArray, String key)
    {
        this._jsonArray = jsonArray;
        this._key = key;
    }

    public JSONArray sort() {
        _sorted = new JSONArray();

        // step through all objects
        for (int i = 0; i < _jsonArray.length(); i++) {
            JSONObject json = JSONhelper.parseJsonFromArray(_jsonArray, i);
            String value = JSONhelper.parseValueByKey(json, _key);
            Double num = Double.parseDouble(value);
            int index = bisection(num);
            _sorted.put(index);
        }
        return _sorted;
    }

    /*
     * find appropriate index to insert
     */
    protected int bisection(Double num)
    {
        int leftIndex = 0;
        int rightIndex = _jsonArray.length()-1;

        while(rightIndex>leftIndex)
        {
            int midIndex = (rightIndex + leftIndex ) / 2;
            JSONObject json = JSONhelper.parseJsonFromArray(_jsonArray, midIndex);
            String value = JSONhelper.parseValueByKey(json, _key);
            Double n = Double.parseDouble(value);

            if(n < num)
            {
                leftIndex = midIndex;
            }
            else
            {
                rightIndex = midIndex;
            }
        }

        // almost there... but not placing at the end of the list for larger value
        return leftIndex;
    }
}
