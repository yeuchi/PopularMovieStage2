package com.ctyeung.popularmoviestage2.data.utilities

import org.json.JSONArray

/**
 * Created by ctyeung on 8/19/17.
 */
class JSONArraySort(private val _jsonArray: JSONArray, private val _key: String) {
    private var _sorted: JSONArray? = null
    fun sort(): JSONArray {
        _sorted = JSONArray()

        // step through all objects
        for (i in 0 until _jsonArray.length()) {
            val json = JSONhelper.parseJsonFromArray(_jsonArray, i)
            json?.let {
                val value = JSONhelper.parseValueByKey(json, _key)
                value?.let {
                    val num = value.toDouble()
                    val index = bisection(num)
                    _sorted!!.put(index)
                }
            }
        }
        return _sorted!!
    }

    /*
     * find appropriate index to insert
     */
    protected fun bisection(num: Double): Int {
        var leftIndex = 0
        var rightIndex = _jsonArray.length() - 1
        while (rightIndex > leftIndex) {
            val midIndex = (rightIndex + leftIndex) / 2
            val json = JSONhelper.parseJsonFromArray(_jsonArray, midIndex)
            json?.let {
                val value = JSONhelper.parseValueByKey(json, _key)
                value?.let {
                    val n = value.toDouble()
                    if (n < num) {
                        leftIndex = midIndex
                    } else {
                        rightIndex = midIndex
                    }
                }
            }
        }

        // almost there... but not placing at the end of the list for larger value
        return leftIndex
    }
}
