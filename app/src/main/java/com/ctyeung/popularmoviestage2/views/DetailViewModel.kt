package com.ctyeung.popularmoviestage2.views

import androidx.lifecycle.ViewModel
import com.ctyeung.popularmoviestage2.data.room.Movie
import com.ctyeung.popularmoviestage2.data.room.MovieRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    db: MovieRepository
) : ViewModel() {

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

    fun parseJson(jsonString: String) {
        movie = Gson().fromJson(jsonString, Movie::class.java)
    }

    fun selectFavorite() {
        /*
         * TODO update isFavorite in DB, memory and DB
         */
    }
}