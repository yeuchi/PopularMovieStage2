package com.ctyeung.popularmoviestage2.data.network

import com.ctyeung.popularmoviestage2.data.room.Movie
import com.ctyeung.popularmoviestage2.data.room.MovieRepository
import com.ctyeung.popularmoviestage2.data.utilities.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieNetworkRepository  @Inject constructor(
    private val db: MovieRepository
) {
    private val _event = MutableSharedFlow<NetworkEvent>()
    val event: SharedFlow<NetworkEvent> = _event
    private var task: GithubQueryTask?=null

    suspend fun requestMovies(sortMethod:String) {
        val url = NetworkUtils.buildMainPageUrl(sortMethod)
        task = GithubQueryTask(GithubQueryTask.METHOD_THUMBS)
        task?.execute(url)
        listen4Event()
        /* insert db ? */
    }

    suspend fun requestReviews(
        selectedMovie: Movie,
        id: String
    ) {
        val url = NetworkUtils.buildReviewUrl(id)
        task = GithubQueryTask(GithubQueryTask.METHOD_REVIEWS, selectedMovie)
        task?.execute(url)
        listen4Event()
    }

    suspend fun requestTrailers(
        selectedMovie: Movie,
        id: String
    ) {
        val url = NetworkUtils.buildVideoUrl(id)
        task = GithubQueryTask(GithubQueryTask.METHOD_TRAILERS, selectedMovie)
        task?.execute(url)
        listen4Event()
    }

    private fun listen4Event() {
        CoroutineScope(IO).launch {
            task?.event?.collect() {
                when(it){
                    is QueryEvent.Thumbs -> onMovies(it.list)
                    is QueryEvent.Trailers -> onTrailers(it.str)
                    is QueryEvent.Reviews -> onReviews(it.str)
                }
            }
        }
    }

    private suspend fun onMovies(movies:List<Movie>) {
        db.dropTable()
        movies.forEach {
            db.insert(it)
        }
        _event.emit(NetworkEvent.Movies(movies))
    }

    private suspend fun onTrailers(str:String?) {
        _event.emit(NetworkEvent.Trailers(str))
    }

    private suspend fun onReviews(str:String?) {
        _event.emit(NetworkEvent.Reviews(str))

    }
}

sealed class NetworkEvent() {
    data class Movies(val list:List<Movie>):NetworkEvent()
    data class Trailers(val str:String?):NetworkEvent()
    data class Reviews(val str:String?):NetworkEvent()
}