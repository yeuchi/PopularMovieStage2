package com.ctyeung.popularmoviestage2.data.utilities

import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by ctyeung on 8/18/17.
 */
object JSONhelper {
    fun parseJson(str: String?): JSONObject? {
        var json: JSONObject? = null
        try {
            json = JSONObject(str)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return json
    }

    fun getJsonArray(json: JSONObject, key: String?): JSONArray? {
        var jsonArray: JSONArray? = null
        try {
            jsonArray = json.getJSONArray(key)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return jsonArray
    }

    fun getJsonArray(jsonString: String?, key: String?): JSONArray? {
        if (null == jsonString) return null
        val json = parseJson(jsonString) ?: return null
        return getJsonArray(json, key)
    }

    fun parseJsonFromArray(jsonArray: JSONArray, index: Int): JSONObject? {
        var json: JSONObject? = null
        try {
            json = jsonArray.getJSONObject(index)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return json
    }

    fun parseValueByKey(json: JSONObject, key: String?): String? {
        var str: String? = null
        try {
            str = json.getString(key)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return str
    }
}
