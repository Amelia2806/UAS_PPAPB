package com.example.uas_ppapb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.uas_pppab.network.ApiClient
import com.example.uas_ppapb.databinding.ActivityAdminEditFilmBinding
import com.example.uas_ppapb.model.FilmUserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Aktivitas untuk mengedit data film oleh admin
class AdminEditFilmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminEditFilmBinding

    // Variabel untuk menyimpan ID film yang akan diedit
    private var movieId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengikat tampilan dengan View Binding
        binding = ActivityAdminEditFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengambil data dari intent sebelumnya
        movieId = intent.getStringExtra("id") // Mendapatkan ID film dari intent
        movieId = intent.getStringExtra("_id") // ID tambahan dari intent sebelumnya

        // Mengisi data awal pada form dengan data dari intent
        binding.txtTitleEdit.setText(intent.getStringExtra("title")) // Mengisi judul film
        binding.txtDirectorEdit.setText(intent.getStringExtra("director")) // Mengisi nama sutradara
        binding.txtDurasiEdit.setText(intent.getStringExtra("durasi")) // Mengisi durasi film
        binding.txtRatingEdit.setText(intent.getStringExtra("rating")) // Mengisi rating film
        binding.txtSinopsisEdit.setText(intent.getStringExtra("sinopsis")) // Mengisi sinopsis film

        // Tombol untuk meng-update data film ketika ditekan
        binding.btnUpdate.setOnClickListener {
            updateFilmData()
        }

        // Tombol untuk kembali ke halaman HomeAdminActivity
        binding.buttonBack.setOnClickListener {
            navigateToHome()
        }
    }

    // Fungsi untuk berpindah ke halaman HomeAdminActivity
    private fun navigateToHome() {
        val intent = Intent(this, HomeAdminActivity::class.java) // Membuat intent untuk berpindah aktivitas
        startActivity(intent) // Memulai aktivitas baru
        finish() // Menutup aktivitas saat ini
    }

    // Fungsi untuk mengupdate data film melalui API
    private fun updateFilmData() {
        lifecycleScope.launch { // Menggunakan lifecycleScope untuk menjalankan coroutine yang terkait dengan siklus hidup aktivitas ini
            try {
                // Mengambil data yang diinputkan oleh pengguna dari form
                val updatedTitle = binding.txtTitleEdit.text.toString()
                val updatedDirector = binding.txtDirectorEdit.text.toString()
                val updatedDurasi = binding.txtDurasiEdit.text.toString()
                val updatedRating = binding.txtRatingEdit.text.toString()
                val updatedSinopsis = binding.txtSinopsisEdit.text.toString()
                val updatedImageUrl = binding.txtImageEdit.text.toString()

                // Membuat objek data film dengan data yang diperbarui
                val filmData = FilmUserData(
                    _id = movieId, // ID film yang akan diperbarui
                    title = updatedTitle,
                    director = updatedDirector,
                    durasi = updatedDurasi,
                    rating = updatedRating,
                    sinopsis = updatedSinopsis,
                    imageUrl = updatedImageUrl
                )

                // Memanggil API untuk memperbarui data film dengan menggunakan coroutine di thread latar belakang
                val response = withContext(Dispatchers.IO) {
                    ApiClient.api.updateMovie(movieId!!, filmData) // Memanggil metode updateMovie dari API
                }

                // Memeriksa apakah respons dari server berhasil
                if (response.isSuccessful) {
                    Toast.makeText(this@AdminEditFilmActivity, "Film berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    navigateToHome() // Jika berhasil, berpindah ke halaman HomeAdminActivity
                } else {
                    Toast.makeText(this@AdminEditFilmActivity, "Gagal memperbarui film", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Menangani kesalahan yang mungkin terjadi saat memanggil API
                Log.e("Update Error", "Gagal memperbarui data", e) // Mencetak log kesalahan
                Toast.makeText(this@AdminEditFilmActivity, "Terjadi kesalahan!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
