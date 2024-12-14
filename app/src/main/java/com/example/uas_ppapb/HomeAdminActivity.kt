package com.example.uas_ppapb

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas_ppapb.database.LocalDao
import com.example.uas_ppapb.database.LocalRoomDatabase
import com.example.uas_ppapb.databinding.ActivityHomeAdminBinding
import com.example.uas_ppapb.model.FilmUserData
import com.example.uas_pppab.network.ApiClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// Aktivitas utama untuk halaman Home Admin
class HomeAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeAdminBinding // Binding untuk mengakses elemen di XML
    private lateinit var itemAdapter: FilmAdminAdapter // Adapter untuk mengatur data RecyclerView
    private lateinit var itemList: ArrayList<FilmUserData> // Daftar untuk menyimpan data film
    private lateinit var dbExecutor: ExecutorService // Executor untuk operasi database secara latar belakang
    private lateinit var mLocalDao: LocalDao // DAO untuk mengakses database lokal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind tampilan dengan View Binding
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi RecyclerView
        initializeRecyclerView()

        // Inisialisasi database
        initializeDatabase()

        // Periksa koneksi internet dan ambil data
        checkInternetAndFetchData()

        // Atur pendengar untuk tombol dan aksi terkait
        setupListeners()
    }

    // Mengatur RecyclerView dengan adapter dan data
    private fun initializeRecyclerView() {
        itemList = arrayListOf() // Inisialisasi daftar kosong
        val apiService = ApiClient.api // Mengambil instance ApiService dari Retrofit
        itemAdapter = FilmAdminAdapter(itemList, apiService) // Membuat adapter untuk RecyclerView
        binding.rvFilm.apply {
            setHasFixedSize(true) // Mengoptimalkan performa RecyclerView
            layoutManager = LinearLayoutManager(this@HomeAdminActivity) // Mengatur layout manager
            adapter = itemAdapter // Mengaitkan adapter dengan RecyclerView
        }
    }

    // Mengatur koneksi ke database lokal
    private fun initializeDatabase() {
        dbExecutor = Executors.newSingleThreadExecutor() // Executor untuk menjalankan operasi database secara latar belakang
        mLocalDao = LocalRoomDatabase.getDatabase(this)!!.localDao()!! // Mengakses DAO database lokal
    }

    // Memeriksa koneksi internet dan memutuskan untuk mengambil data dari server atau database lokal
    private fun checkInternetAndFetchData() {
        if (isInternetAvailable()) {
            fetchDataFromServer() // Jika ada koneksi internet, ambil data dari server
            Toast.makeText(this, "Establishing Connection", Toast.LENGTH_SHORT).show()
        } else {
            fetchDataOffline() // Jika tidak ada koneksi, ambil data dari database lokal
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk memeriksa koneksi internet
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    // Mengambil data dari server melalui API
    private fun fetchDataFromServer() {
        ApiClient.api.getMovies().enqueue(object : Callback<List<FilmUserData>> {
            override fun onResponse(
                call: Call<List<FilmUserData>>,
                response: Response<List<FilmUserData>>
            ) {
                if (response.isSuccessful) {
                    itemList.clear() // Membersihkan daftar sebelumnya
                    response.body()?.forEach {
                        itemList.add(it) // Menambahkan data dari server ke daftar
                    }
                    itemAdapter.notifyDataSetChanged() // Memperbarui tampilan
                } else {
                    Log.e("API_ERROR", "Response Error") // Log jika terjadi kesalahan dalam merespons API
                }
            }

            override fun onFailure(call: Call<List<FilmUserData>>, t: Throwable) {
                Log.e("API_FAILURE", "Error: ${t.message}") // Log jika API gagal diakses
            }
        })
    }

    // Mengambil data dari database lokal jika tidak ada koneksi internet
    private fun fetchDataOffline() {
        dbExecutor.execute {
            mLocalDao.allPostsLocal().observe(this) { movies ->
                itemList.clear() // Membersihkan daftar sebelumnya
                movies.forEach {
                    val localFilm = FilmUserData(
                        title = it.judulFilm,
                        director = it.directorFilm,
                        durasi = it.durasiFilm,
                        rating = it.ratingFilm,
                        sinopsis = it.sinopsisFilm,
                        imageUrl = it.imgFilm
                    )
                    itemList.add(localFilm) // Mengisi daftar dengan data dari database lokal
                }
                runOnUiThread {
                    itemAdapter.notifyDataSetChanged() // Memperbarui tampilan di thread utama
                }
            }
        }
    }

    // Mengatur pendengar untuk tombol seperti navigasi dan logout
    private fun setupListeners() {
        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AdminAddFilmActivity::class.java) // Navigasi ke halaman AdminAddFilmActivity
            startActivity(intent)
            Toast.makeText(this, "Navigating to Add Film", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            handleLogout() // Menangani proses logout
        }
    }

    // Fungsi untuk menangani logout pengguna
    private fun handleLogout() {
        try {
            // Sign out dari Firebase
            Firebase.auth.signOut()

            // Navigasi ke halaman LoginRegisterActivity dan menghapus back stack
            val intent = Intent(this, LoginRegisterActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)

            Toast.makeText(this, "Logout successful!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Logout failed: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("LOGOUT_ERROR", "Error during logout", e) // Log jika terjadi kesalahan saat logout
        }
    }
}
