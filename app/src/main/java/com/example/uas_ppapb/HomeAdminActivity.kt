package com.example.uas_ppapb

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_ppapb.database.LocalDao
import com.example.uas_ppapb.database.LocalRoomDatabase
import com.example.uas_ppapb.databinding.ActivityHomeAdminBinding
import com.example.uas_ppapb.model.FilmUserData
import com.example.uas_pppb.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HomeAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeAdminBinding
    private lateinit var itemAdapter: FilmAdminAdapter
    private lateinit var itemList: ArrayList<FilmUserData>
    private lateinit var recyclerViewItem: RecyclerView

    private lateinit var mLocalDao: LocalDao
    private lateinit var dbExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        recyclerViewItem = binding.rvFilm
        recyclerViewItem.setHasFixedSize(true)
        recyclerViewItem.layoutManager = LinearLayoutManager(this)

        itemList = arrayListOf()
        itemAdapter = FilmAdminAdapter(itemList)
        recyclerViewItem.adapter = itemAdapter

        // Initialize Room database & Executor for database access
        dbExecutor = Executors.newSingleThreadExecutor()
        mLocalDao = LocalRoomDatabase.getDatabase(this)!!.localDao()!!

        // Check for connectivity & fetch data
        if (isInternetAvailable(this)) {
            fetchDataFromServer()
            Toast.makeText(this, "Establishing Connection", Toast.LENGTH_SHORT).show()
        } else {
            fetchDataOffline()
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    // Check if the device has internet access
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                )
    }

    private fun fetchDataFromServer() {
//        ApiClient.api.getMovies().enqueue(object : Callback<List<FilmUserData>> {
//            override fun onResponse(call: Call<List<FilmUserData>>, response: Response<List<FilmAdminData>>) {
//                if (response.isSuccessful) {
//                    Log.d("API_SUCCESS", "Data successfully fetched")
//                    itemList.clear()
//                    response.body()?.forEach { movie ->
//                        itemList.add(movie)
//                    }
//                    runOnUiThread {
//                        itemAdapter.notifyDataSetChanged()
//                    }
//                } else {
//                    Log.e("API_ERROR", "Server error: ${response.code()}")
//                    runOnUiThread {
//                        Toast.makeText(this@HomeAdminActivity, "Failed to load server data", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<List<FilmAdminData>>, t: Throwable) {
//                Log.e("API_FAILURE", "Network call failed", t)
//                runOnUiThread {
//                    Toast.makeText(this@HomeAdminActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        })

        ApiClient.api.getMovies().enqueue(object : Callback<List<FilmUserData>> {
            override fun onResponse(call: Call<List<FilmUserData>>, response: Response<List<FilmUserData>>) {
                if (response.isSuccessful) {
//                    recyclerView.adapter = response.body()?.let { PostAdapter(it) }
                    System.out.println("movies: =====");
                    System.out.println(response.body())

                    response.body()?.forEach { filmUser ->
                        val local = FilmUserData(
                            _id = filmUser._id,
                            title = filmUser.title,
                            director = filmUser.director,
                            durasi = filmUser.durasi,
                            rating = filmUser.rating,
                            sinopsis = filmUser.sinopsis,
                            imageUrl = filmUser.imageUrl
                        )
                        itemList.add(local)
                    }

                    itemAdapter.notifyDataSetChanged()
                } else {
                    System.out.println("failed not isSuccessful");

//                    Toast.makeText(this@MainActivity, "Failed to load posts", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FilmUserData>>, t: Throwable) {
                System.out.println("failed onFailure");
                System.out.println("Error: ${t.message}");

//                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        });
    }

    private fun fetchDataOffline() {
        dbExecutor.execute {
            mLocalDao.allPostsLocal().observe(this) { movies ->
                Log.d("LOCAL_DB", "Loading offline data...")
                itemList.clear()
                movies.forEach { movie ->
                    val localFilm = FilmUserData(
                        title = movie.judulFilm,
                        director = movie.directorFilm,
                        durasi = movie.durasiFilm,
                        rating = movie.ratingFilm,
                        sinopsis = movie.sinopsisFilm,
                        imageUrl = movie.imgFilm
                    )
                    itemList.add(localFilm)
                }
                runOnUiThread {
                    itemAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
