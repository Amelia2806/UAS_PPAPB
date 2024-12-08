package com.example.uas_pppb.network

import com.example.uas_ppapb.model.FilmAdminData
import com.example.uas_pppb.model.FilmUserData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // Ambil daftar film dari server
    @GET("LIcJU/movies")
    fun getMovies(): Call<List<FilmUserData>>

    @POST("LIcJU/movies")
    fun createMovie(@Body movie: FilmAdminData): Call<Void>
}
