package com.example.uas_ppapb.database

// Import untuk anotasi dan fitur Room seperti @Dao, @Insert, @Update, dan lainnya
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

// Menandai bahwa interface ini adalah Data Access Object (DAO) untuk akses database Room
@Dao
interface LocalDao {

    // Fungsi untuk memasukkan data ke dalam tabel lokal
    // Menggunakan strategi konflik NONE agar tidak menimpa data yang sudah ada
    @Insert(onConflict = OnConflictStrategy.NONE)
    fun insert(local: Local)

    // Fungsi untuk memperbarui data yang sudah ada dalam tabel
    @Update
    fun update(local: Local)

    // Fungsi untuk menghapus semua data dalam tabel lokal
    @Query("DELETE FROM local_table")
    fun truncateTable()

    // Fungsi untuk mengambil semua data dari tabel lokal sebagai LiveData
    @Query("SELECT * FROM local_table")
    fun allPostsLocal(): LiveData<List<Local>>

    // Fungsi untuk mengambil semua data dari tabel lokal tanpa LiveData dan mengurutkan berdasarkan ID terbaru
    @Query("SELECT * FROM local_table ORDER BY id DESC")
    fun allLocal(): List<Local>

    // Fungsi untuk mengambil satu data berdasarkan ID yang cocok
    @Query("SELECT * FROM local_table WHERE id= :id")
    fun getFilmById(id: String?): Local

    // Fungsi untuk menghapus satu data berdasarkan ID
    @Query("DELETE FROM local_table WHERE id= :id")
    fun delete(id: String?)
}
