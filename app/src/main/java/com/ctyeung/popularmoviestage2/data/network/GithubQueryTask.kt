package com.ctyeung.popularmoviestage2.data.network

import android.os.AsyncTask
import com.ctyeung.popularmoviestage2.data.room.Movie
import com.ctyeung.popularmoviestage2.data.utilities.JSONhelper
import com.ctyeung.popularmoviestage2.data.utilities.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.URL

    /*
    * TODO Replace with retrofit ?
    */
class GithubQueryTask(
    val method: String,
    val selectedMovie: Movie? = null
) : AsyncTask<URL?, Void?, String?>() {

    private val _event = MutableSharedFlow<QueryEvent>()
    val event: SharedFlow<QueryEvent> = _event

    override fun doInBackground(vararg urls: URL?): String? {
        val searchUrl = urls[0]
        var githubSearchResults: String? = null
        try {
            searchUrl?.let {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl)
            } ?: throw Exception("missing url")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return githubSearchResults
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun onPostExecute(str: String?) {
        CoroutineScope(IO).launch {
            when (method) {
                METHOD_THUMBS -> handleMovieData(str)
                METHOD_TRAILERS -> handleTrailers(str)
                METHOD_REVIEWS -> handleReviews(str)
            }
        }
    }

    private suspend fun handleTrailers(str: String?) {
        _event.emit(QueryEvent.Trailers(str))
    }

    private suspend fun handleReviews(str: String?) {
        _event.emit(QueryEvent.Reviews(str))

//        val typeVideo = MovieHelper.isVideo(str)
//        if (null != selectedMovie) {
//            if (typeVideo) {
//                trailerString = str
//            } else {
//                reviewString = str
//            }
//
//            // launch detail page when we have all content (selection, trailers, reviews json)
//            if (null != trailerString &&
//                null != reviewString
//            ) launchDetailActivity(selectedMovie)
//        } else {
//            // display info explaining 'no selection, trailer or review' available
//            tvNetworkErrorDisplay!!.visibility = View.VISIBLE
//        }
    }

    private suspend fun handleMovieData(str: String?) {
        val json = JSONhelper.parseJson(str)
        if (null != json) {
            val jsonArray = JSONhelper.getJsonArray(json, "results")
            val movies = ArrayList<Movie>()
            jsonArray?.let {
                for (i in 0 until jsonArray.length()) {
                    val json = JSONhelper.parseJsonFromArray(jsonArray, i)
                    json?.let {
                        val movie = Movie.create(json)
                        movies.add(i, movie)
                    }
                }
            }
            _event.emit(QueryEvent.Thumbs(movies))
        }
    }

    companion object {
        const val METHOD_THUMBS = "THUMBS"
        const val METHOD_TRAILERS = "TRAILERS"
        const val METHOD_REVIEWS = "REVIEWS"
    }
}

sealed class QueryEvent() {
    data class Thumbs(val list:List<Movie>) : QueryEvent()
    data class Trailers(val str: String?) : QueryEvent()
    data class Reviews(val str: String?) : QueryEvent()
}