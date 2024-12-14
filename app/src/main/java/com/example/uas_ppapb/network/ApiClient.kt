package com.example.uas_pppab.network

// Impor yang dibutuhkan untuk mengatur koneksi Retrofit dan konversi data JSON
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton untuk mengatur koneksi ke API dengan Retrofit
object ApiClient {

    // Base URL untuk endpoint API yang digunakan
    private const val BASE_URL = "https://ppbo-api.vercel.app/"

    // Membuat instance Retrofit dengan konversi JSON menggunakan Gson
    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL) // Atur URL dasar untuk koneksi ke server API
        .addConverterFactory(GsonConverterFactory.create()) // Konversi JSON ke model data menggunakan Gson
        .build() // Bangun Retrofit instance
        .create(ApiService::class.java) // Buat implementasi API interface
}
