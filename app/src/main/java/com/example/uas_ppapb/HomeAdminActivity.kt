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

class HomeAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeAdminBinding
    private lateinit var itemAdapter: FilmAdminAdapter
    private lateinit var itemList: ArrayList<FilmUserData>
    private lateinit var dbExecutor: ExecutorService
    private lateinit var mLocalDao: LocalDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind the view with View Binding
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        initializeRecyclerView()

        // Initialize database access
        initializeDatabase()

        // Check internet dan ambil data
        checkInternetAndFetchData()

        // Set up event listeners
        setupListeners()
    }

    private fun initializeRecyclerView() {
        itemList = arrayListOf()
        val apiService = ApiClient.api // Mengambil instance ApiService dari Retrofit
        itemAdapter = FilmAdminAdapter(itemList, apiService) // Berikan itemList dan apiService
        binding.rvFilm.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@HomeAdminActivity)
            adapter = itemAdapter
        }
    }

    private fun initializeDatabase() {
        dbExecutor = Executors.newSingleThreadExecutor()
        mLocalDao = LocalRoomDatabase.getDatabase(this)!!.localDao()!!
    }

    private fun checkInternetAndFetchData() {
        if (isInternetAvailable()) {
            fetchDataFromServer()
            Toast.makeText(this, "Establishing Connection", Toast.LENGTH_SHORT).show()
        } else {
            fetchDataOffline()
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun fetchDataFromServer() {
        ApiClient.api.getMovies().enqueue(object : Callback<List<FilmUserData>> {
            override fun onResponse(
                call: Call<List<FilmUserData>>,
                response: Response<List<FilmUserData>>
            ) {
                if (response.isSuccessful) {
                    itemList.clear()
                    response.body()?.forEach {
                        itemList.add(it)
                    }
                    itemAdapter.notifyDataSetChanged()
                } else {
                    Log.e("API_ERROR", "Response Error")
                }
            }

            override fun onFailure(call: Call<List<FilmUserData>>, t: Throwable) {
                Log.e("API_FAILURE", "Error: ${t.message}")
            }
        })
    }

    private fun fetchDataOffline() {
        dbExecutor.execute {
            mLocalDao.allPostsLocal().observe(this) { movies ->
                itemList.clear()
                movies.forEach {
                    val localFilm = FilmUserData(
                        title = it.judulFilm,
                        director = it.directorFilm,
                        durasi = it.durasiFilm,
                        rating = it.ratingFilm,
                        sinopsis = it.sinopsisFilm,
                        imageUrl = it.imgFilm
                    )
                    itemList.add(localFilm)
                }
                runOnUiThread {
                    itemAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AdminAddFilmActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Navigating to Add Film", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            handleLogout()
        }
    }

    private fun handleLogout() {
        try {
            // Sign out from Firebase
            Firebase.auth.signOut()

            // Clear back stack and navigate ke LoginRegisterActivity
            val intent = Intent(this, LoginRegisterActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)

            Toast.makeText(this, "Logout successful!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Logout failed: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("LOGOUT_ERROR", "Error during logout", e)
        }
    }
}
