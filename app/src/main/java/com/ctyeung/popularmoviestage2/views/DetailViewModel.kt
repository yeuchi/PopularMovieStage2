package com.ctyeung.popularmoviestage2.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ctyeung.popularmoviestage2.data.network.MovieNetworkRepository
import com.ctyeung.popularmoviestage2.data.network.NetworkEvent
import com.ctyeung.popularmoviestage2.data.room.Movie
import com.ctyeung.popularmoviestage2.data.room.MovieRepository
import com.ctyeung.popularmoviestage2.data.utilities.JSONhelper
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val db: MovieRepository,
    private val network: MovieNetworkRepository
) : ViewModel() {

    private val _event = MutableSharedFlow<DetailViewEvent>()
    val event: SharedFlow<DetailViewEvent> = _event

    var mTrailerJsonArray: JSONArray? = null
    var mReviewJsonArray: JSONArray? = null

    var movie: Movie? = null
    val id: Int?
        get() {
            return movie?.id
        }
    val title: String?
        get() {
            return movie?.title
        }

    val originalTitle: String?
        get() {
            return movie?.originalTitle
        }

    val isFavorite: Boolean
        get() {
            return movie?.isFavorite ?: false
        }

    val releaseDate: String?
        get() {
            return movie?.releaseDate
        }

    val plot: String?
        get() {
            return movie?.overview
        }

    val voteAverage: Double
        get() {
            return movie?.voteAverage ?: 0.0
        }

    init {
        listen4Network()
    }

    private fun listen4Network() {
        viewModelScope.launch(IO) {
            network.event.collect() {
                when (it) {
                    is NetworkEvent.Movies -> {}
                    is NetworkEvent.Trailers -> onTrailers(it.str)
                    is NetworkEvent.Reviews -> onReviews(it.str)
                }
            }
        }
    }

    private suspend fun onTrailers(str: String?) {
        mTrailerJsonArray = JSONhelper.getJsonArray(str, "results")
        _event.emit(DetailViewEvent.Trailers(str))
    }

    private suspend fun onReviews(str: String?) {
        mReviewJsonArray = JSONhelper.getJsonArray(str, "results")
        _event.emit(DetailViewEvent.Reviews(str))
    }

    fun parseJson(jsonString: String):Boolean {
        movie = Gson().fromJson(jsonString, Movie::class.java)
        return movie?.let { true } ?: false
    }

    fun selectFavorite() {
        viewModelScope.launch(IO) {
            movie?.apply {
                isFavorite = !isFavorite
                db.insert(this)
            }
        }
    }

    fun requestTrailers() {
        viewModelScope.launch(IO) {
            movie?.let {
                network.requestTrailers(it)
            }
        }
    }

    fun requestReviews() {
        viewModelScope.launch(IO) {
            movie?.let {
                network.requestReviews(it)
            }
        }
    }
}

sealed class DetailViewEvent() {
    data class Trailers(val str: String?) : DetailViewEvent()
    data class Reviews(val str: String?) : DetailViewEvent()
}