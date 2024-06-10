package com.ctyeung.popularmoviestage2.data.room

import androidx.room.Entity
import com.ctyeung.popularmoviestage2.data.utilities.JSONhelper
import com.ctyeung.popularmoviestage2.data.utilities.MovieHelper
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

@Entity(tableName = "movie_table", primaryKeys = ["id"])
data class Movie (
    val adult:Boolean,
    @SerializedName("backdrop_path")
    val backDropPath:String,
//    @SerializedName("Genre_ids")
//    val genreIds:List<Int>,
    val id:Int,
    @SerializedName("original_language")
    val originalLanguage:String,
    @SerializedName("original_title")
    val originalTitle:String,
    val overview:String,
    val popularity:Double,
    @SerializedName("poster_path")
    val posterPath:String,
    @SerializedName("release_date")
    val releaseDate:String,
    val title:String,
    val video:Boolean,
    @SerializedName("vote_average")
    val voteAverage:Double,
    @SerializedName("vote_count")
    val voteCount:Int,
    var isFavorite:Boolean = false
) {
    companion object {
        fun parse(json: String): Movie {
            val movie = Gson().fromJson(json, Movie::class.java)
            return movie
        }

        fun create(json: JSONObject): Movie {
            json.apply {
                val movie = Movie(
                    getBoolean("adult"),
                    getString("backdrop_path"),
                    getInt("id"),
                    getString("original_language"),
                    getString("original_title"),
                    getString("overview"),
                    getDouble("popularity"),
                    getString("poster_path"),
                    getString("release_date"),
                    getString("title"),
                    getBoolean("video"),
                    getDouble("vote_average"),
                    getInt("vote_count"),
                    false
                )
                return movie
            }
        }
    }

        fun toJson():String {
            val json = Gson().toJson(this)
            return json
        }

    fun posterThumbPath():String {
        val path = MovieHelper.BASE_POSTER_URL +
                MovieHelper.getSizeByIndex(
                    MovieHelper.INDEX_THUMBNAIL) + this.posterPath
        return path
    }

    fun posterDetailPath():String {
        val path = MovieHelper.BASE_POSTER_URL +
                MovieHelper.getSizeByIndex(MovieHelper.INDEX_DETAIL) + this.posterPath
        return path
    }
}
