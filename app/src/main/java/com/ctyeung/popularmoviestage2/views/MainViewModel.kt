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

    init {
        viewModelScope.launch {
            network.event.collect() {
                val result = when (it) {
                    is NetworkEvent.Movies -> MainViewEvent.Movies(it.list)
                    is NetworkEvent.Trailers -> MainViewEvent.Trailers(it.str)
                    is NetworkEvent.Reviews -> MainViewEvent.Reviews(it.str)
                }
                _event.emit(result)
            }
        }
    }

    /**
     * load movie thumbs for main page
     */
    fun request(sortMethod: String) {
        when (sortMethod) {
            MovieHelper.SORT_FAVORITE -> loadDbFavorites()

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
            db.retrieve().let {
                _event.emit(MainViewEvent.Favorites(it))
            }
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