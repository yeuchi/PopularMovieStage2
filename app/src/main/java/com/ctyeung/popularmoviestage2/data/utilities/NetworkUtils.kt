/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ctyeung.popularmoviestage2.data.utilities

import android.net.Uri
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.Scanner

/**
 * These utilities will be used to communicate with the network.
 */
object NetworkUtils {
    /**
     * Builds the URL used to query GitHub.
     *
     * @return The URL to use to query the GitHub.
     */
    fun buildMainPageUrl(sortMethod: String): URL? {
        val str = MovieHelper.BASE_URL + sortMethod
        val builtUri = Uri.parse(str)
            .buildUpon()
            .appendQueryParameter(MovieHelper.PARAM_API_KEY, MovieHelper.API_KEY_VALUE)
            .build()
        var url: URL? = null
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return url
    }

    fun buildVideoUrl(id: String): URL? {
        val str = MovieHelper.BASE_URL + id.toString() + "/" + MovieHelper.PARAM_API_VIDEO
        val builtUri = Uri.parse(str)
            .buildUpon()
            .appendQueryParameter(MovieHelper.PARAM_API_KEY, MovieHelper.API_KEY_VALUE)
            .build()
        var url: URL? = null
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return url
    }

    fun buildYoutubeUrl(youtubeId: String?): URL? {
        val str = MovieHelper.BASE_YOUTUBE_URL
        val builtUri = Uri.parse(str)
            .buildUpon()
            .appendQueryParameter(MovieHelper.PARAM_API_V, youtubeId)
            .build()
        var url: URL? = null
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return url
    }

    fun buildReviewUrl(id: String): URL? {
        val str = MovieHelper.BASE_URL + id.toString() + "/" + MovieHelper.PARAM_API_REVIEW
        val builtUri = Uri.parse(str)
            .buildUpon()
            .appendQueryParameter(MovieHelper.PARAM_API_KEY, MovieHelper.API_KEY_VALUE)
            .build()
        var url: URL? = null
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return url
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    @Throws(IOException::class)
    fun getResponseFromHttpUrl(url: URL): String? {
        val urlConnection = url.openConnection() as HttpURLConnection
        return try {
            val `in` = urlConnection.inputStream
            val scanner = Scanner(`in`)
            scanner.useDelimiter("\\A")
            val hasInput = scanner.hasNext()
            if (hasInput) {
                scanner.next()
            } else {
                null
            }
        } finally {
            urlConnection.disconnect()
        }
    }
}