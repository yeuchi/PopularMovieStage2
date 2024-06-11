package com.ctyeung.popularmoviestage2.views

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ctyeung.popularmoviestage2.data.room.Movie
import com.ctyeung.popularmoviestage2.data.room.MovieRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject

/*
 * TODO migrate to Kotlin
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    db: MovieRepository
) : ViewModel() {

    var movie: Movie? = null
    var id: Int? = null
    var title: String? = null
    var isFavorite = false
    fun parseJson(jsonString: String) {
        movie = Gson().fromJson(jsonString, Movie::class.java)
        id = movie?.id
        title = movie?.title
        isFavorite = movie?.isFavorite?:false
    }

    fun selectFavorite() {
    }
}