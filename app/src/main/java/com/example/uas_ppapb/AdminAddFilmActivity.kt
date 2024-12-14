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

// Aktivitas untuk memungkinkan admin menambahkan film baru
class AdminAddFilmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAddFilmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengikat layout dengan menggunakan View Binding
        binding = ActivityAdminAddFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol untuk kembali ke aktivitas HomeAdminActivity
        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, HomeAdminActivity::class.java) // Membuat intent untuk berpindah ke HomeAdminActivity
            startActivity(intent) // Memulai aktivitas HomeAdminActivity
            finish() // Menutup aktivitas saat ini
        }

        // Tombol untuk menangani proses submit data film
        binding.btnAdd.setOnClickListener {
            addFilm() // Memanggil fungsi untuk menambahkan film
        }
    }

    // Fungsi untuk menangani logika menambahkan film
    private fun addFilm() {
        // Mengambil data dari inputan pengguna
        val title = binding.txtName.text.toString()
        val director = binding.txtDirector.text.toString()
        val durasi = binding.txtDurasi.text.toString()
        val rating = binding.txtRating.text.toString()
        val sinopsis = binding.txtSinopsis.text.toString()
        val imageUrl = binding.txtImage.text.toString()

        // Validasi input untuk memastikan semua kolom sudah diisi
        if (title.isEmpty() || director.isEmpty() || durasi.isEmpty() || rating.isEmpty() || sinopsis.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(this, "Mohon isi semua kolom dengan lengkap", Toast.LENGTH_SHORT).show()
            return
        }

        // Membuat objek data film yang akan dikirim melalui API
        val filmUserData = FilmUserData(
            title = title,
            director = director,
            durasi = durasi,
            rating = rating,
            sinopsis = sinopsis,
            imageUrl = imageUrl
        )

        // Menggunakan Coroutine untuk menjalankan operasi jaringan di thread latar belakang
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Memanggil API untuk membuat film baru
                val response = ApiClient.api.createMovie(filmUserData)

                // Memeriksa apakah respons berhasil
                if (response.isSuccessful) {
                    runOnUiThread { // Kembali ke thread utama untuk menampilkan Toast
                        Toast.makeText(this@AdminAddFilmActivity, "Film berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@AdminAddFilmActivity, HomeAdminActivity::class.java) // Membuat intent untuk berpindah ke HomeAdminActivity
                        startActivity(intent) // Memulai aktivitas HomeAdminActivity
                        finish() // Menutup aktivitas saat ini
                    }
                } else {
                    runOnUiThread { // Menampilkan pesan jika gagal menambahkan film
                        Toast.makeText(this@AdminAddFilmActivity, "Gagal menambahkan film", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) { // Menangani kesalahan saat melakukan request
                runOnUiThread { // Kembali ke thread utama untuk menampilkan Toast
                    Toast.makeText(this@AdminAddFilmActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
