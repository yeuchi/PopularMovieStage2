package com.ctyeung.popularmoviestage2.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ctyeung.popularmoviestage2.data.network.MovieNetworkRepository
import com.ctyeung.popularmoviestage2.data.network.NetworkEvent
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

    private var favorites = emptyList<Movie>()
    private var movies = emptyList<Movie>()
    var selectedMovie: Movie? = null
    var sortMethod = MovieHelper.SORT_POPULAR

    private var trailerString: String? = null
    private var reviewString: String? = null

    init {
        listen4Network()
    }

    private fun listen4Network() {
        viewModelScope.launch(IO) {
            network.event.collect() {
                when (it) {
                    is NetworkEvent.Movies -> onMovies(it.list)
                    is NetworkEvent.Trailers -> onTrailers(it.str)
                    is NetworkEvent.Reviews -> onReviews(it.str)
                }
            }
        }
    }

    private suspend fun onMovies(list: List<Movie>) {
        val all = db.retrieveAll()
        movies = all
        _event.emit(MainViewEvent.Movies(all))
    }

    private suspend fun onTrailers(str: String?) {
        trailerString = str
        _event.emit(MainViewEvent.Trailers(str))
    }

    private suspend fun onReviews(str: String?) {
        reviewString = str
        _event.emit(MainViewEvent.Reviews(str))
    }

    /**
     * load movie thumbs for main page
     */
    fun request() {
        when (sortMethod) {
            MovieHelper.SORT_FAVORITE -> {
                viewModelScope.launch(IO) {
                    favorites = db.favorites()
                    _event.emit(MainViewEvent.Favorites(favorites))
                }
            }

            MovieHelper.SORT_POPULAR,
            MovieHelper.SORT_TOP_RATED -> loadFromNetwork(sortMethod)
        }
    }

    fun select4Detail(movie: Movie) {
        selectedMovie = movie
        requestTrailers(movie)
    }

    fun selectMovieBundle(): String? {
        return if (selectedMovie != null && trailerString != null && reviewString != null) {
            val mergeString: String = selectedMovie!!.toJson() + "_sep_" +
                    trailerString + "_sep_" +
                    reviewString

            mergeString
        } else {
            null
        }
    }

    private fun requestTrailers(movie: Movie) {
        viewModelScope.launch(IO) {
            network.requestTrailers(movie)
        }
    }

    fun requestReviews(
        selectedMovie: Movie
    ) {
        viewModelScope.launch(IO) {
            network.requestReviews(selectedMovie)
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