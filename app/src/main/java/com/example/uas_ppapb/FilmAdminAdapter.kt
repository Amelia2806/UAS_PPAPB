package com.example.uas_ppapb

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas_ppapb.model.FilmUserData
import com.example.uas_pppab.network.ApiService
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Adapter untuk RecyclerView yang digunakan oleh admin untuk menampilkan daftar film
class FilmAdminAdapter(private val filmAdminList: ArrayList<FilmUserData>, private val apiService: ApiService) : RecyclerView.Adapter<FilmAdminAdapter.FilmAdminViewHolder>() {

    // ViewHolder untuk merepresentasikan setiap item dalam daftar RecyclerView
    class FilmAdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title_film_admin) // Referensi untuk judul film
        val director: TextView = itemView.findViewById(R.id.director_film_admin) // Referensi untuk sutradara film
        val durasi: TextView = itemView.findViewById(R.id.durasi_film_admin) // Referensi untuk durasi film
        val rating: TextView = itemView.findViewById(R.id.rating_film_admin) // Referensi untuk rating film
        val sinopsis: TextView = itemView.findViewById(R.id.sinopsis_film_admin) // Referensi untuk sinopsis film
        val image: ImageView = itemView.findViewById(R.id.image_film_admin) // Referensi untuk gambar film
    }

    // Membuat ViewHolder baru ketika diperlukan
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmAdminViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.admin_film, parent, false) // Menginflate layout untuk setiap item
        return FilmAdminViewHolder(itemView) // Mengembalikan ViewHolder dengan tampilan yang diinflate
    }

    // Mengikat data ke tampilan dalam setiap ViewHolder
    override fun onBindViewHolder(holder: FilmAdminViewHolder, position: Int) {
        val currentItem = filmAdminList[position] // Ambil item dari daftar berdasarkan posisi

        // Mengisi tampilan dengan data film dari daftar
        holder.title.text = currentItem.title
        holder.director.text = currentItem.director
        holder.durasi.text = currentItem.durasi
        holder.rating.text = currentItem.rating
        holder.sinopsis.text = currentItem.sinopsis

        // Menggunakan Glide untuk memuat gambar dari URL
        Glide.with(holder.itemView.context)
            .load(currentItem.imageUrl) // Memuat gambar dari URL
            .into(holder.image) // Memasukkan gambar ke ImageView

        // Logika untuk tombol Edit
        holder.itemView.findViewById<ImageButton>(R.id.btn_edit).setOnClickListener {
            val intent = Intent(holder.itemView.context, AdminEditFilmActivity::class.java).apply {
                putExtra("_id", currentItem._id) // Mengirim ID film
                putExtra("title", currentItem.title) // Mengirim data judul
                putExtra("director", currentItem.director) // Mengirim data sutradara
                putExtra("durasi", currentItem.durasi) // Mengirim data durasi
                putExtra("rating", currentItem.rating) // Mengirim data rating
                putExtra("sinopsis", currentItem.sinopsis) // Mengirim data sinopsis
                putExtra("imgId", currentItem.imageUrl) // Mengirim URL gambar
            }
            holder.itemView.context.startActivity(intent) // Memulai aktivitas baru untuk edit data
        }

        // Logika untuk tombol Hapus
        holder.itemView.findViewById<ImageButton>(R.id.btn_hapus).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch { // Jalankan operasi hapus di thread latar belakang
                try {
                    val response = apiService.deleteMovie(currentItem._id.toString()) // Memanggil endpoint DELETE
                    if (response.isSuccessful) {
                        // Jika sukses, hapus dari daftar dan notifikasi perubahan ke UI
                        CoroutineScope(Dispatchers.Main).launch {
                            filmAdminList.removeAt(position) // Menghapus item dari daftar
                            notifyItemRemoved(position) // Memanggil notifikasi untuk perubahan UI
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace() // Menangani kesalahan jika operasi gagal
                }
            }
        }
    }

    // Mengembalikan jumlah item dalam daftar
    override fun getItemCount(): Int = filmAdminList.size
}
