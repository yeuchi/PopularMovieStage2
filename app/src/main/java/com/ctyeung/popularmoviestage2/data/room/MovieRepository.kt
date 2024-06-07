package com.ctyeung.popularmoviestage2.data.room

import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieDao: MovieDao
) {
    suspend fun retrieve():List<Movie> {
        return movieDao.getMovies()
    }
}