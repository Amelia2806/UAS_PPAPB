package com.example.uas_ppapb

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas_ppapb.database.FilmFavorite
import com.example.uas_ppapb.database.FilmFavoriteDao
import com.example.uas_ppapb.database.Local
import com.example.uas_ppapb.database.LocalRoomDatabase
import com.example.uas_ppapb.model.FilmUserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Adapter untuk menampilkan data film di RecyclerView
class FilmUserAdapter(
    private val filmUserList: List<FilmUserData>, // Daftar data film yang akan ditampilkan
    private val context: Context // Context digunakan untuk memulai aktivitas baru dan akses database
) : RecyclerView.Adapter<FilmUserAdapter.FilmUserViewHolder>() {

    // ViewHolder merepresentasikan setiap item dalam RecyclerView
    inner class FilmUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleUser: TextView = itemView.findViewById(R.id.judul_film_user) // Referensi ke elemen judul film
        val imageUser: ImageView = itemView.findViewById(R.id.image_film_user) // Referensi ke elemen gambar film
        val btnFavorite: ImageButton = itemView.findViewById(R.id.btnFavorite) // Referensi ke tombol favorit
    }

    // Membuat instance ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmUserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film, parent, false) // Menginflate layout untuk setiap item di RecyclerView
        return FilmUserViewHolder(itemView) // Mengembalikan ViewHolder dengan tampilan yang diinflate
    }

    // Menghubungkan data dengan ViewHolder
    override fun onBindViewHolder(holder: FilmUserViewHolder, position: Int) {
        val currentItem = filmUserList[position] // Mengambil data film berdasarkan posisi

        // Menampilkan judul dan gambar menggunakan Glide
        holder.titleUser.text = currentItem.title // Mengisi judul film
        Glide.with(context) // Memuat gambar dari URL menggunakan Glide
            .load(currentItem.imageUrl)
            .into(holder.imageUser)

        // Menangani klik pada item untuk membuka DetailFilmActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailFilmActivity::class.java).apply {
                putExtra("judul", currentItem.title) // Mengirim data judul ke aktivitas DetailFilmActivity
                putExtra("director", currentItem.director) // Mengirim data sutradara ke aktivitas DetailFilmActivity
                putExtra("durasi", currentItem.durasi) // Mengirim durasi ke aktivitas DetailFilmActivity
                putExtra("rating", currentItem.rating) // Mengirim rating ke aktivitas DetailFilmActivity
                putExtra("deskripsi", currentItem.sinopsis) // Mengirim deskripsi ke aktivitas DetailFilmActivity
                putExtra("imageUrl", currentItem.imageUrl) // Mengirim URL gambar ke aktivitas DetailFilmActivity
            }
            context.startActivity(intent) // Memulai aktivitas DetailFilmActivity
        }

        // Memeriksa apakah film sudah ada di daftar favorit
        isFavorite(currentItem._id) { isFav ->
            holder.btnFavorite.setImageResource(
                if (isFav) R.drawable.favorite // Jika favorit, tampilkan ikon favorit
                else R.drawable.baseline_favorite_border_24 // Jika tidak, tampilkan ikon "favorit tidak aktif"
            )
        }

        // Menangani klik tombol favorit
        holder.btnFavorite.setOnClickListener {
            toggleFavorite(currentItem) { isFav ->
                holder.btnFavorite.setImageResource(
                    if (isFav) R.drawable.baseline_favorite_border_24
                    else R.drawable.favorite
                )
            }
        }
    }

    // Mengembalikan jumlah item dalam RecyclerView
    override fun getItemCount(): Int = filmUserList.size

    // Fungsi untuk memeriksa apakah film sudah ada di daftar favorit
    private fun isFavorite(_id: String?, callback: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch { // Operasi database dilakukan di thread latar belakang
            val db = LocalRoomDatabase.getDatabase(context)?.filmFavoriteDao() // Mengakses DAO database
            val isFav = db?.getFilmById(_id) != null // Memeriksa apakah film sudah ada di database
            System.out.println("isFav=--------------") // Log untuk debugging
            System.out.println(isFav)
            withContext(Dispatchers.Main) { // Kembali ke thread utama untuk memperbarui UI
                callback(isFav) // Memanggil callback dengan hasil pemeriksaan
            }
        }
    }

    // Fungsi untuk menambah atau menghapus film dari daftar favorit
    private fun toggleFavorite(film: FilmUserData, callback: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch { // Operasi database dilakukan di thread latar belakang
            val db = LocalRoomDatabase.getDatabase(context)?.filmFavoriteDao() // Mengakses DAO database
            val filmFavorite = FilmFavorite(
                filmId = film._id.toString()
            )

            val isFav = db?.getFilmById(film._id) != null // Memeriksa apakah film sudah ada di daftar favorit
            System.out.println("--------------") // Log untuk debugging
            System.out.println(isFav)

            if (isFav) {
                db?.delete(film._id) // Menghapus jika sudah ada
            } else {
                db?.insert(filmFavorite) // Menambahkan ke daftar favorit jika belum ada
                System.out.println(db?.allLocal()) // Log untuk debugging
            }
            withContext(Dispatchers.Main) { // Kembali ke thread utama untuk memperbarui UI
                callback(!isFav) // Memanggil callback dengan status baru favorit
            }
        }
    }
}
