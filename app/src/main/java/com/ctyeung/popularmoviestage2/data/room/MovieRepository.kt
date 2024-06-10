package com.ctyeung.popularmoviestage2.data.room

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieDao: MovieDao
) {

    private val _event = MutableSharedFlow<DaoEvent>()
    val event : SharedFlow<DaoEvent> = _event
    suspend fun retrieve() {
        movieDao.getMovies().collect(){
            _event.emit(DaoEvent.Retrieve(it))
        }
    }

    suspend fun favorites() {
        movieDao.getFavorites().collect(){
            _event.emit(DaoEvent.Favorites(it))
        }
    }

    suspend fun insert(movie: Movie) {
        movieDao.insert(movie)
    }

    suspend fun dropTable() {
        movieDao.deleteAll()
    }
}

sealed class DaoEvent() {
    data class Retrieve(val movies:List<Movie>):DaoEvent()
    data class Favorites(val movies:List<Movie>):DaoEvent()
}