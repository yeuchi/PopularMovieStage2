package com.ctyeung.popularmoviestage2.data.network

import com.ctyeung.popularmoviestage2.data.room.Movie
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object RetrofitClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }
}

interface ApiService {
    @GET("posts/{id}")
    fun getMovies(): List<Movie>
}