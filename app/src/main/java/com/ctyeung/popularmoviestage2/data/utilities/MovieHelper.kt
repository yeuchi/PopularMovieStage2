package com.ctyeung.popularmoviestage2.data.utilities

/**
 * Created by ctyeung on 8/19/17.
 */
object MovieHelper {
    const val BASE_URL = "https://api.themoviedb.org/3/movie/"
    const val API_KEY_VALUE = "838bb81a894bc77678913ab8b239cfb3"
    const val PARAM_API_KEY = "api_key"
    const val SORT_POPULAR = "popular"
    const val SORT_TOP_RATED = "top_rated"
    const val SORT_FAVORITE = "favorite"
    const val KEY_POSTER_PATH = "poster_path"
    const val KEY_TITLE = "title"
    const val KEY_ORIGINAL_TITLE = "original_title"
    const val KEY_PLOT = "overview"
    const val KEY_RELEASE_DATE = "release_date"
    const val KEY_RUNTIME = "runtime"
    const val KEY_VOTE_AVERAGE = "vote_average"
    const val KEY_TRAILER = "key"
    const val KEY_AUTHOR = "author"
    const val KEY_REVIEW_URL = "url"
    const val KEY_ID = "id"
    const val KEY_NAME = "name"
    const val KEY_TYPE = "type"
    const val BASE_POSTER_URL = "https://image.tmdb.org/t/p/"
    const val PARAM_API_VIDEO = "videos"
    const val PARAM_API_REVIEW = "reviews"
    const val PARAM_API_V = "v"
    const val INDEX_THUMBNAIL = 5
    const val INDEX_DETAIL = 3
    const val IS_FAVORITE = "isFavorite"
    const val BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v="
    fun getSizeByIndex(i: Int): String {
        return when (i) {
            0 -> "w92/"
            1 -> "w154/"
            2 -> "w185/"
            3 -> "w342/"
            4 -> "w500/"
            5 -> "w780/"
            6 -> "original/"
            else -> "w185/"
        }
    }

    fun isVideo(str: String): Boolean {
        return if (str.indexOf("key") > 0) true else false
    }
}
