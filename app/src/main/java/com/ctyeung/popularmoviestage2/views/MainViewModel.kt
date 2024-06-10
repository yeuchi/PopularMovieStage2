package com.ctyeung.popularmoviestage2.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ctyeung.popularmoviestage2.data.network.MovieNetworkRepository
import com.ctyeung.popularmoviestage2.data.network.NetworkEvent
import com.ctyeung.popularmoviestage2.data.room.DaoEvent
import com.ctyeung.popularmoviestage2.data.room.Movie
import com.ctyeung.popularmoviestage2.data.room.MovieRepository
import com.ctyeung.popularmoviestage2.data.utilities.MovieHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    val db: MovieRepository,
    val network: MovieNetworkRepository
) : ViewModel() {

    private val _event = MutableSharedFlow<MainViewEvent>()
    val event: SharedFlow<MainViewEvent> = _event

    var movies = emptyList<Movie>()
    var selectedMovie: Movie? = null

    init {
        listen4DB()
        listen4Network()
    }

    private fun listen4Network() {
        viewModelScope.launch {
            network.event.collect() {
                when (it) {
                    is NetworkEvent.Movies -> onMovies(it.list)
                    is NetworkEvent.Trailers -> onTrailers(it.str)
                    is NetworkEvent.Reviews -> onReviews(it.str)
                }
            }
        }
    }

    private fun listen4DB() {
        viewModelScope.launch {
            db.event.collect() {
                when (it) {
                    is DaoEvent.Retrieve -> {
                        movies = it.movies
                        _event.emit(MainViewEvent.Movies(it.movies))
                    }

                    is DaoEvent.Favorites -> {
                        movies = it.movies
                        _event.emit(MainViewEvent.Favorites(it.movies))
                    }
                }
            }
        }
    }

    private suspend fun onMovies(list: List<Movie>) {
        db.retrieve()
    }

    private suspend fun onTrailers(str: String?) {
        _event.emit(MainViewEvent.Trailers(str))
    }

    private suspend fun onReviews(str: String?) {
        _event.emit(MainViewEvent.Reviews(str))
    }

    /**
     * load movie thumbs for main page
     */
    fun request(sortMethod: String) {

        when (sortMethod) {
            MovieHelper.SORT_FAVORITE -> {
                if (movies.isNotEmpty()) {
                    loadDbFavorites()
                } else {
                    /*
                     * TODO better solution ?
                     */
                    loadFromNetwork(MovieHelper.SORT_POPULAR)
                }
            }

            MovieHelper.SORT_POPULAR,
            MovieHelper.SORT_TOP_RATED -> loadFromNetwork(sortMethod)
        }
    }

    fun requestTrailers(
        selectedMovie: Movie,
        id: String
    ) {

        viewModelScope.launch(IO) {
            network.requestTrailers(selectedMovie, id)
        }
    }

    fun requestReviews(
        selectedMovie: Movie,
        id: String
    ) {

        viewModelScope.launch(IO) {
            network.requestReviews(selectedMovie, id)
        }
    }

    private fun loadDbFavorites() {
        viewModelScope.launch(IO) {
            db.favorites()
        }
    }

    private fun loadFromNetwork(sortMethod: String) {
        viewModelScope.launch(IO) {
            network.requestMovies(sortMethod)
        }
    }
}

sealed class MainViewEvent() {
    data class Favorites(val list: List<Movie>) : MainViewEvent()
    data class Movies(val list: List<Movie>) : MainViewEvent()
    data class Trailers(val str: String?) : MainViewEvent()
    data class Reviews(val str: String?) : MainViewEvent()
}