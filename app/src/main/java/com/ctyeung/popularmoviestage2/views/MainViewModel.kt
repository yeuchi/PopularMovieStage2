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
    private val db: MovieRepository,
    private val network: MovieNetworkRepository
) : ViewModel() {

    private val _event = MutableSharedFlow<MainViewEvent>()
    val event: SharedFlow<MainViewEvent> = _event

    private var favorites = emptyList<Movie>()
    private var movies = emptyList<Movie>()
    var sortMethod = MovieHelper.SORT_POPULAR

    init {
        listen4Network()
    }

    private fun listen4Network() {
        viewModelScope.launch(IO) {
            network.event.collect() {
                when (it) {
                    is NetworkEvent.Movies -> onMovies()
                    is NetworkEvent.Trailers -> {}
                    is NetworkEvent.Reviews -> {}

                }
            }
        }
    }

    private suspend fun onMovies() {
        val all = db.retrieveAll()
        movies = all
        _event.emit(MainViewEvent.Movies(all))
    }


    /**
     * Description: request data
     * a. favorites -> query from db
     * b. others -> REST call from service to retrieve all moives; then sort
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

    /**
     * Request network {popular or top_rated}
     */
    private fun loadFromNetwork(sortMethod: String) {
        viewModelScope.launch(IO) {
            network.requestMovies(sortMethod)
        }
    }
}

sealed class MainViewEvent() {
    data class Favorites(val list: List<Movie>) : MainViewEvent()
    data class Movies(val list: List<Movie>) : MainViewEvent()
}