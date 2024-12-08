package com.example.uas_ppapb.model;

// Kelas `FilmAdminData` merepresentasikan data film pada bagian admin
// Implementasi Serializable memungkinkan objek dari kelas ini dapat dikirimkan antar aktivitas menggunakan Intent
// Setiap objek `FilmAdminData` memiliki properti yang mencakup informasi judul, sutradara, durasi, rating, sinopsis, dan URL gambar film

class FilmAdminData(
    val title : String?= null,
    val director : String?= null,
    val durasi : String?= null,
    var rating : String?= null,
    val sinopsis : String?= null,
    val imageUrl : String?= null)
