package com.example.uas_ppapb.database

// Import yang diperlukan untuk anotasi Room
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Mendeklarasikan entitas tabel untuk Room Database dengan nama tabel "film_favorite_table"
@Entity(tableName = "film_favorite_table")
data class FilmFavorite (

    // Primary key untuk tabel, diatur untuk auto-generate nilainya
    @PrimaryKey(autoGenerate = true)
    @NonNull // Menandakan bahwa nilai id tidak boleh null
    val id: Int = 0,

    // Kolom untuk menyimpan filmId dalam tabel, nama kolom ditentukan secara eksplisit
    @ColumnInfo(name = "filmId")
    val filmId: String
)
