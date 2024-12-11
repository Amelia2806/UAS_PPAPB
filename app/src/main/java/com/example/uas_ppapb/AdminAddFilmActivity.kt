package com.example.uas_ppapb

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_ppapb.databinding.ActivityAdminAddFilmBinding
import com.example.uas_ppapb.model.FilmUserData
import com.example.uas_pppab.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminAddFilmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAddFilmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAddFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol untuk kembali ke HomeAdminActivity
        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, HomeAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Tombol untuk submit data film
        binding.btnAdd.setOnClickListener {
            addFilm()
        }
    }

    private fun addFilm() {
        val title = binding.txtName.text.toString()
        val director = binding.txtDirector.text.toString()
        val durasi = binding.txtDurasi.text.toString()
        val rating = binding.txtRating.text.toString()
        val sinopsis = binding.txtSinopsis.text.toString()
        val imageUrl = binding.txtImage.text.toString()

        // Validasi input
        if (title.isEmpty() || director.isEmpty() || durasi.isEmpty() || rating.isEmpty() || sinopsis.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(this, "Mohon isi semua kolom dengan lengkap", Toast.LENGTH_SHORT).show()
            return
        }

        val filmUserData = FilmUserData(
            title = title,
            director = director,
            durasi = durasi,
            rating = rating,
            sinopsis = sinopsis,
            imageUrl = imageUrl
        )

        // Kirim data melalui API menggunakan Coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.api.createMovie(filmUserData)

                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@AdminAddFilmActivity, "Film berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@AdminAddFilmActivity, HomeAdminActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@AdminAddFilmActivity, "Gagal menambahkan film", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@AdminAddFilmActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
