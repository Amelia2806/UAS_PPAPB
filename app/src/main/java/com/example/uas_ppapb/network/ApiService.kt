package com.example.uas_pppab.network

// Impor yang dibutuhkan untuk mengatur komunikasi API dengan Retrofit
import com.example.uas_ppapb.model.FilmUserData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Interface yang mendefinisikan semua endpoint API yang akan digunakan
interface ApiService {

    // Ambil daftar film dari server
    @GET("LIcJU/movies")
    fun getMovies(): Call<List<FilmUserData>> // Mengembalikan daftar film dalam bentuk List<FilmUserData>

    // Ambil detail film berdasarkan ID
    @GET("LIcJU/movies/{id}")
    fun getMovieDetails(@Path("id") movieId: String): Call<FilmUserData> // Mengembalikan detail film dengan ID yang sesuai

    // Kirim data untuk membuat film baru
    @POST("LIcJU/movies")
    suspend fun createMovie(@Body movie: FilmUserData): retrofit2.Response<Void> // Menggunakan suspend untuk operasi jaringan dengan Retrofit

    // Perbarui data film berdasarkan ID
    @POST("LIcJU/movies/{id}")
    suspend fun updateMovie(@Path("id") movieId: String, @Body movie: FilmUserData): retrofit2.Response<Void> // Menggunakan suspend untuk operasi jaringan dengan Retrofit

    // Hapus data film berdasarkan ID
    @DELETE("LIcJU/movies/{id}")
    suspend fun deleteMovie(@Path("id") movieId: String): retrofit2.Response<Void> // Menggunakan suspend untuk operasi jaringan dengan Retrofit
}
