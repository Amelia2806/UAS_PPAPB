package com.example.uas_ppapb.model

data class FilmUserData(
    val _id: String? = null,                // ID unik dari setiap film
    val title: String? = null,           // Judul film
    val director: String? = null,        // Sutradara film
    val durasi: String? = null,          // Durasi film
    val rating: String? = null,          // Rating film
    val sinopsis: String? = null,        // Sinopsis film
    val imageUrl: String? = null         // URL gambar poster film
)
