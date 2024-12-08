package com.example.uas_ppapb.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface LocalDao {
    @Insert(onConflict = OnConflictStrategy.NONE)
    fun insert(local: Local)

    @Update
    fun update(local: Local)

    @Query("DELETE FROM local_table")
    fun truncateTable()

    @Query("SELECT * FROM local_table")
    fun allPostsLocal(): LiveData<List<Local>>
}