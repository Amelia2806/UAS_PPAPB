package com.example.uas_ppapb.database

// Import yang dibutuhkan untuk Room Database dan migrasi
import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room.databaseBuilder
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Anotasi @Database untuk mendefinisikan database dengan tabel-tabel terkait dan versi database
@Database(entities = [Local::class, FilmFavorite::class], version = 2, exportSchema = true)
abstract class LocalRoomDatabase : RoomDatabase() {

    // Abstract function untuk mengakses DAO terkait tabel Local
    abstract fun localDao() : LocalDao?

    // Abstract function untuk mengakses DAO terkait tabel FilmFavorite
    abstract fun filmFavoriteDao() : FilmFavoriteDao?

    // Bagian Singleton Database untuk memastikan hanya satu instance database yang dibuat
    companion object {
        @Volatile
        private var INSTANCE: LocalRoomDatabase? = null

        // Fungsi untuk mendapatkan instance database dengan mengamankan thread-safe menggunakan synchronized
        fun getDatabase(context: Context): LocalRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(LocalRoomDatabase::class.java) {
                    INSTANCE = databaseBuilder(
                        context.applicationContext,
                        LocalRoomDatabase::class.java,
                        "movie_database"
                    )
                        .addMigrations(MIGRATION_1_2) // Tambahkan migrasi manual jika database memerlukan perubahan struktur
                        .fallbackToDestructiveMigration() // Jika migrasi gagal, database akan dihapus dan dibuat ulang
                        .build()
                }
            }

            return INSTANCE
        }

        // Definisi untuk migrasi database dari versi 1 ke versi 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Perintah SQL untuk membuat tabel baru jika belum ada
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS film_favorite_table (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                filmId TEXT NOT NULL 
            )
        """)
            }
        }
    }
}
