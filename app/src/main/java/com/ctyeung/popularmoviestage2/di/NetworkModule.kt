package com.ctyeung.popularmoviestage2.di

import com.ctyeung.popularmoviestage2.data.network.MovieNetworkRepository
import com.ctyeung.popularmoviestage2.data.room.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideMovieNetworkRepository(db: MovieRepository): MovieNetworkRepository {
        return MovieNetworkRepository(db = db)
    }
}