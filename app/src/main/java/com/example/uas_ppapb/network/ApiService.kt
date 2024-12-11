package com.example.uas_pppab.network

import com.example.uas_ppapb.model.FilmUserData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // Ambil daftar film dari server
    @GET("LIcJU/movies")
    fun getMovies(): Call<List<FilmUserData>>

    @GET("LIcJU/movies/{id}")
    fun getMovieDetails(@Path("id") movieId: String): Call<FilmUserData>

    @POST("LIcJU/movies")
    suspend fun createMovie(@Body movie: FilmUserData): retrofit2.Response<Void>

    @POST("LIcJU/movies/{id}")
    suspend fun updateMovie(@Path("id") movieId: String, @Body movie: FilmUserData): retrofit2.Response<Void>

    @DELETE("LIcJU/movies/{id}")
    suspend fun deleteMovie(@Path("id") movieId: String): retrofit2.Response<Void>
}
