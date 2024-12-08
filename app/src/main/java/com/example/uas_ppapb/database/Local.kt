package com.example.uas_ppapb.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_table")
data class Local (
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val id: Int = 0,
    @ColumnInfo(name = "judulFilm")
    val judulFilm: String,
    @ColumnInfo(name = "directorFilm")
    val directorFilm: String,
    @ColumnInfo(name = "durasiFilm")
    val durasiFilm: String,
    @ColumnInfo(name = "ratingFilm")
    val ratingFilm: String,
    @ColumnInfo(name = "sinopsisFilm")
    val sinopsisFilm: String,
    @ColumnInfo(name = "imgFilm")
    val imgFilm: String
    )