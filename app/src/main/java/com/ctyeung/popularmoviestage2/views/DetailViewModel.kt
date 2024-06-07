package com.ctyeung.popularmoviestage2.views

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ctyeung.popularmoviestage2.data.room.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/*
 * TODO migrate to Kotlin
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    movieRepository: MovieRepository):ViewModel() {

}