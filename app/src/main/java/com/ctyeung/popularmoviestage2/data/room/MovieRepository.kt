package com.ctyeung.popularmoviestage2.data.room

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieDao: MovieDao
) {
    suspend fun retrieveAll():List<Movie> {
        return movieDao.getMovies()
    }

    suspend fun retrieve(title:String):Movie? {
        return movieDao.getMovie(title)
    }

    suspend fun favorites():List<Movie> {
        return movieDao.getFavorites()
    }

    suspend fun insert(movie: Movie) {
        movieDao.insert(movie)
    }

    suspend fun dropTable() {
        movieDao.deleteAll()
    }

    suspend fun deleteNonFavorites() {
        movieDao.deleteNonFavorites()
    }
}

sealed class DaoEvent() {
    data class RetrieveAll(val movies: List<Movie>) : DaoEvent()
    data class Retrieve(val movie: Movie?=null) : DaoEvent()
    data class Favorites(val movies: List<Movie>) : DaoEvent()
}