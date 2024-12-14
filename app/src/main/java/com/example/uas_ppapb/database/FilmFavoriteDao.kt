package com.example.uas_ppapb.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FilmFavoriteDao {
    @Insert(onConflict = OnConflictStrategy.NONE)
    fun insert(filmFavorite: FilmFavorite)

    @Update
    fun update(filmFavorite: FilmFavorite)

    @Query("DELETE FROM film_favorite_table")
    fun truncateTable()

    @Query("SELECT * FROM film_favorite_table")
    fun allPostsLocal(): LiveData<List<FilmFavorite>>

    @Query("SELECT * FROM film_favorite_table ORDER BY id DESC")
    fun allLocal(): List<FilmFavorite>

    @Query("SELECT * FROM film_favorite_table WHERE filmId = :id")
    fun getFilmById(id: String?): FilmFavorite

    @Query("DELETE FROM film_favorite_table WHERE filmId = :id")
    fun delete(id: String?)
}