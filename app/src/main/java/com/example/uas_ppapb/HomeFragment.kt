package com.example.uas_ppapb

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_ppapb.FilmUserAdapter
import com.example.uas_ppapb.database.Local
import com.example.uas_ppapb.database.LocalDao
import com.example.uas_ppapb.database.LocalRoomDatabase
import com.example.uas_ppapb.databinding.FragmentHomeBinding
import com.example.uas_ppapb.databinding.ItemFilmBinding
import com.example.uas_ppapb.model.FilmUserData
import com.example.uas_pppab.network.ApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HomeFragment : Fragment() {

    // Inisialisasi variabel untuk mengikat tampilan XML
    private lateinit var binding: FragmentHomeBinding
    private lateinit var itemAdapter: FilmUserAdapter // Adapter untuk mengisi RecyclerView
    private var itemList: ArrayList<FilmUserData> = ArrayList<FilmUserData>() // Daftar untuk menyimpan data film user
    private lateinit var recyclerViewItem: RecyclerView // Variabel untuk RecyclerView

    // Firebase database dan autentikasi
    private lateinit var database: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    // Room Database
    private lateinit var mLocalDao: LocalDao
    private lateinit var executorService: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Mengikat tampilan dengan View Binding
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi autentikasi Firebase
        mAuth = Firebase.auth
        val currentUser = mAuth.currentUser!!.email

        // Inisialisasi Room database dan DAO
        executorService = Executors.newSingleThreadExecutor()
        val db = LocalRoomDatabase.getDatabase(requireContext())
        mLocalDao = db!!.localDao()!!

        // Cek ketersediaan koneksi internet sebelum mengambil data dari server atau database lokal
        if (isInternetAvailable(requireActivity())) {
            fetchData()
            Toast.makeText(requireActivity(), "Establishing Connection", Toast.LENGTH_SHORT).show()
        } else {
            fetchDataOffline()
            Toast.makeText(requireActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show()
        }

        // Mengatur username untuk ditampilkan di tampilan dari email pengguna yang sedang aktif
        binding.getUsername.setText(currentUser!!.substringBefore('@').toString())

        // Mengatur RecyclerView untuk menampilkan data dalam bentuk daftar
        recyclerViewItem = binding.rvFilm
        recyclerViewItem.setHasFixedSize(true)

        // Menggunakan GridLayoutManager untuk menampilkan daftar dalam dua kolom
        recyclerViewItem.layoutManager = GridLayoutManager(requireContext(), 2)

        // Mengatur adapter dengan daftar kosong dan konteks
        itemAdapter = FilmUserAdapter(itemList, requireContext())
        recyclerViewItem.adapter = itemAdapter
    }

    // Fungsi untuk membersihkan tabel lokal dari Room Database
    private fun truncateTable() {
        executorService.execute { mLocalDao.truncateTable() }
    }

    // Fungsi untuk menyisipkan data ke database lokal
    private fun insert(local: Local) {
        executorService.execute { mLocalDao.insert(local) }
    }

    // Fungsi untuk mengecek koneksi internet dengan melihat kemampuan jaringan
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    // Fungsi untuk mengambil data dari server menggunakan Retrofit API
    private fun fetchData() {
        ApiClient.api.getMovies().enqueue(object : Callback<List<FilmUserData>> {
            override fun onResponse(call: Call<List<FilmUserData>>, response: Response<List<FilmUserData>>) {
                if (response.isSuccessful) {
                    System.out.println("movies: =====")
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
                        itemList.add(local) // Menambahkan data ke daftar
                    }

                    itemAdapter.notifyDataSetChanged() // Memperbarui tampilan dengan data baru
                } else {
                    System.out.println("failed not isSuccessful")
                }
            }

            override fun onFailure(call: Call<List<FilmUserData>>, t: Throwable) {
                System.out.println("failed onFailure")
                System.out.println("Error: ${t.message}") // Log error jika gagal mengambil data
            }
        })
    }

    // Fungsi untuk mengambil data dari database lokal jika tidak ada koneksi internet
    private fun fetchDataOffline() {
        itemList.clear() // Mengosongkan daftar sementara
    }
}
