package com.example.uas_ppapb.database

// Import untuk anotasi dan fitur Room seperti @Entity, @PrimaryKey, dan @ColumnInfo
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Menandakan bahwa class ini adalah entitas untuk tabel Room
// Tabel akan dinamai "local_table"
@Entity(tableName = "local_table")
data class Local (
    // Kolom Primary Key yang bersifat unik dan di-generate secara otomatis
    @PrimaryKey(autoGenerate = true)
    @NonNull // Menandakan bahwa kolom ini tidak boleh bernilai null
    val id: Int = 0,

    // Kolom yang menyimpan judul film
    @ColumnInfo(name = "judulFilm")
    val judulFilm: String,

    // Kolom yang menyimpan nama sutradara film
    @ColumnInfo(name = "directorFilm")
    val directorFilm: String,

    // Kolom yang menyimpan durasi film
    @ColumnInfo(name = "durasiFilm")
    val durasiFilm: String,

    // Kolom yang menyimpan rating film
    @ColumnInfo(name = "ratingFilm")
    val ratingFilm: String,

    // Kolom yang menyimpan sinopsis film
    @ColumnInfo(name = "sinopsisFilm")
    val sinopsisFilm: String,

    // Kolom yang menyimpan URL atau path gambar film
    @ColumnInfo(name = "imgFilm")
    val imgFilm: String
)
