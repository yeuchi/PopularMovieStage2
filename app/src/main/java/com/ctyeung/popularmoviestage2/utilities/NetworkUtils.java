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
package com.ctyeung.popularmoviestage2.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    /**
     * Builds the URL used to query GitHub.
     *
     * @return The URL to use to query the GitHub.
     */
    public static URL buildMainPageUrl(String sortMethod)
    {
        String str = MovieHelper.BASE_URL + sortMethod;
        Uri builtUri = Uri.parse(str)
                            .buildUpon()
                            .appendQueryParameter(MovieHelper.PARAM_API_KEY, MovieHelper.API_KEY_VALUE)
                            .build();

        URL url = null;
        try
        {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildVideoUrl(String id)
    {
        String str = MovieHelper.BASE_URL + id.toString() + "/" + MovieHelper.PARAM_API_VIDEO;
        Uri builtUri = Uri.parse(str)
                .buildUpon()
                .appendQueryParameter(MovieHelper.PARAM_API_KEY, MovieHelper.API_KEY_VALUE)
                .build();

        URL url = null;
        try
        {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildYoutubeUrl(String youtubeId)
    {
        String str = MovieHelper.BASE_YOUTUBE_URL;
        Uri builtUri = Uri.parse(str)
                .buildUpon()
                .appendQueryParameter(MovieHelper.PARAM_API_V, youtubeId)
                .build();

        URL url = null;
        try
        {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildReviewUrl(String id)
    {
        String str = MovieHelper.BASE_URL + id.toString() + "/" + MovieHelper.PARAM_API_REVIEW;
        Uri builtUri = Uri.parse(str)
                .buildUpon()
                .appendQueryParameter(MovieHelper.PARAM_API_KEY, MovieHelper.API_KEY_VALUE)
                .build();

        URL url = null;
        try
        {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try
        {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput)
            {
                return scanner.next();
            }
            else
            {
                return null;
            }
        }
        finally
        {
            urlConnection.disconnect();
        }
    }
}