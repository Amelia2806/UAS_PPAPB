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

class AdminEditFilmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminEditFilmBinding

    // Data yang dikirim dari halaman sebelumnya
    private var movieId: String? = null
    private var originalImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminEditFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari intent sebelumnya
        movieId = intent.getStringExtra("id")
        originalImageUrl = intent.getStringExtra("imgUrl")

        // Isi data awal dari intent
        movieId=intent.getStringExtra("_id") //add_id
        binding.txtTitleEdit.setText(intent.getStringExtra("title"))
        binding.txtDirectorEdit.setText(intent.getStringExtra("director"))
        binding.txtDurasiEdit.setText(intent.getStringExtra("durasi"))
        binding.txtRatingEdit.setText(intent.getStringExtra("rating"))
        binding.txtSinopsisEdit.setText(intent.getStringExtra("sinopsis"))

        // Tombol untuk meng-update data film
        binding.btnUpdate.setOnClickListener {
            updateFilmData()
        }

        // Tombol kembali ke HomeAdminActivity
        binding.buttonBack.setOnClickListener {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeAdminActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun updateFilmData() {
        lifecycleScope.launch {
            try {
                val updatedTitle = binding.txtTitleEdit.text.toString()
                val updatedDirector = binding.txtDirectorEdit.text.toString()
                val updatedDurasi = binding.txtDurasiEdit.text.toString()
                val updatedRating = binding.txtRatingEdit.text.toString()
                val updatedSinopsis = binding.txtSinopsisEdit.text.toString()

                // Membuat data film baru
                val filmData = FilmUserData(
                    _id = movieId,
                    title = updatedTitle,
                    director = updatedDirector,
                    durasi = updatedDurasi,
                    rating = updatedRating,
                    sinopsis = updatedSinopsis,
                    imageUrl = originalImageUrl
                )

                // Memanggil API
                val response = withContext(Dispatchers.IO) {
                    System.out.println(movieId)
                    System.out.println(filmData)
                    System.out.println("-------------------")
                    ApiClient.api.updateMovie(movieId!!, filmData)
                }

                if (response.isSuccessful) {
                    Toast.makeText(this@AdminEditFilmActivity, "Film berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                } else {
                    Toast.makeText(this@AdminEditFilmActivity, "Gagal memperbarui film", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Update Error", "Gagal memperbarui data", e)
                Toast.makeText(this@AdminEditFilmActivity, "Terjadi kesalahan!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
