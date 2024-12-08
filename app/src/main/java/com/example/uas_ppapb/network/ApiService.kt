package com.example.uas_pppb.network

import com.example.uas_pppb.model.FilmUserData
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    // Ambil daftar film dari server
    @GET("LIcJU/movies")
    fun getMovies(): Call<List<FilmUserData>>

//    // Ambil detail film berdasarkan ID (opsional jika detail diperlukan)
//    @GET("https://ppbo-api.vercel.app/LIcJU/movies/67551c93b0c67a085459f255")
//    fun getMovieDetail(
//        @retrofit2.http.Path("id") movieId: String
//    ): Call<FilmUserData>
}
