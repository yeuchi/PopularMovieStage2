package com.ctyeung.popularmoviestage2.di

import android.content.Context
import androidx.room.Room
import com.ctyeung.popularmoviestage2.data.network.MovieNetworkRepository
import com.ctyeung.popularmoviestage2.data.room.MovieDao
import com.ctyeung.popularmoviestage2.data.room.MovieDatabase
import com.ctyeung.popularmoviestage2.data.room.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    fun provideMovieRepository(dao: MovieDao): MovieRepository {
        return MovieRepository(movieDao = dao)
    }

    @Provides
    fun provideMovieDao(db: MovieDatabase): MovieDao {
        return db.movieDao()
    }

    @Provides
    @Singleton
    fun providesMovieDatabase(@ApplicationContext appContext: Context): MovieDatabase {
        return Room.databaseBuilder(appContext, MovieDatabase::class.java, "movie_database").build()
    }
}