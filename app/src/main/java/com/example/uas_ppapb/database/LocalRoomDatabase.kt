package com.example.uas_ppapb.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room.databaseBuilder

@Database(entities = [Local::class], version = 1, exportSchema = false)
abstract class LocalRoomDatabase : RoomDatabase() {
    abstract fun localDao() : LocalDao?

    companion object {
        @Volatile
        private var INSTANCE: LocalRoomDatabase? = null
        fun getDatabase(context: Context): LocalRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(LocalRoomDatabase::class.java) {
                    INSTANCE = databaseBuilder(
                        context.applicationContext,
                        LocalRoomDatabase::class.java,
                        "movie_database"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}