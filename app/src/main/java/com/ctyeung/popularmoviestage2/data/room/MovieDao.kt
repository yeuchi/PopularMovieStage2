package com.ctyeung.popularmoviestage2.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * from movie_table")
    fun getMovies(): List<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: Movie)

    @Query("DELETE FROM movie_table")
     fun deleteAll()

    @Query("DELETE FROM movie_table WHERE originalTitle = :title")
     fun deleteBy(title: String)
}

