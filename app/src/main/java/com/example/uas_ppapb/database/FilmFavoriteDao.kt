package com.example.uas_ppapb.database

// Import untuk anotasi dan class Room
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

// Data Access Object (DAO) untuk mengakses tabel "film_favorite_table"
@Dao
interface FilmFavoriteDao {

    // Menyisipkan data baru ke tabel "film_favorite_table"
    // OnConflictStrategy.NONE berarti tidak ada penanganan konflik; jika data sudah ada, akan terjadi error
    @Insert(onConflict = OnConflictStrategy.NONE)
    fun insert(filmFavorite: FilmFavorite)

    // Memperbarui data di tabel "film_favorite_table"
    @Update
    fun update(filmFavorite: FilmFavorite)

    // Menghapus semua data di tabel "film_favorite_table"
    @Query("DELETE FROM film_favorite_table")
    fun truncateTable()

    // Mengambil semua data dari tabel "film_favorite_table" sebagai LiveData
    // LiveData memungkinkan data ini untuk dipantau secara reaktif di UI
    @Query("SELECT * FROM film_favorite_table")
    fun allPostsLocal(): LiveData<List<FilmFavorite>>

    // Mengambil semua data dari tabel "film_favorite_table" dalam urutan menurun berdasarkan ID
    // Mengembalikan data dalam bentuk List
    @Query("SELECT * FROM film_favorite_table ORDER BY id DESC")
    fun allLocal(): List<FilmFavorite>

    // Mengambil data dari tabel "film_favorite_table" berdasarkan nilai `filmId`
    @Query("SELECT * FROM film_favorite_table WHERE filmId = :id")
    fun getFilmById(id: String?): FilmFavorite

    // Menghapus data dari tabel "film_favorite_table" berdasarkan nilai `filmId`
    @Query("DELETE FROM film_favorite_table WHERE filmId = :id")
    fun delete(id: String?)
}
