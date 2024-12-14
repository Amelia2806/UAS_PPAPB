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
    private val filmUserList: List<FilmUserData>,
    private val context: Context
) : RecyclerView.Adapter<FilmUserAdapter.FilmUserViewHolder>() {

    // ViewHolder merepresentasikan setiap item dalam RecyclerView
    inner class FilmUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleUser: TextView = itemView.findViewById(R.id.judul_film_user)
        val imageUser: ImageView = itemView.findViewById(R.id.image_film_user)
        val btnFavorite: ImageButton = itemView.findViewById(R.id.btnFavorite)
    }

    // Membuat instance ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmUserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film, parent, false)
        return FilmUserViewHolder(itemView)
    }

    // Menghubungkan data dengan ViewHolder
    override fun onBindViewHolder(holder: FilmUserViewHolder, position: Int) {
        val currentItem = filmUserList[position]

        // Menampilkan judul dan gambar menggunakan Glide
        holder.titleUser.text = currentItem.title
        Glide.with(context)
            .load(currentItem.imageUrl)
            .into(holder.imageUser)

        // Menangani klik pada item untuk membuka DetailFilmActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailFilmActivity::class.java).apply {
                putExtra("judul", currentItem.title)
                putExtra("director", currentItem.director)
                putExtra("durasi", currentItem.durasi)
                putExtra("rating", currentItem.rating)
                putExtra("deskripsi", currentItem.sinopsis)
                putExtra("imageUrl", currentItem.imageUrl)
            }
            context.startActivity(intent)
        }

        // Memeriksa apakah film sudah ada di favorit
        isFavorite(currentItem._id) { isFav ->
            holder.btnFavorite.setImageResource(
                if (isFav) R.drawable.favorite
                else R.drawable.baseline_favorite_border_24
            )
        }

        // Menangani klik tombol favorit
        holder.btnFavorite.setOnClickListener {
            toggleFavorite(currentItem) { isFav ->
                holder.btnFavorite.setImageResource(
                    if (isFav) R.drawable.baseline_favorite_border_24
                    else R.drawable.baseline_favorite_border_24
                )
            }
        }
    }

    // Mendapatkan jumlah item dalam RecyclerView
    override fun getItemCount(): Int = filmUserList.size

    // Fungsi untuk memeriksa apakah film sudah ada di favorit
    private fun isFavorite(_id: String?, callback: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = LocalRoomDatabase.getDatabase(context)?.localDao()
            val isFav = db?.getFilmById(_id) != null
            withContext(Dispatchers.Main) {
                callback(isFav)
            }
        }
    }

    // Fungsi untuk menambah atau menghapus film dari favorit
    private fun toggleFavorite(film: FilmUserData, callback: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = LocalRoomDatabase.getDatabase(context)?.filmFavoriteDao()
            val filmFavorite = FilmFavorite(
                filmId = film._id.toString()
            )

            val isFav = db?.getFilmById(film._id) != null
            System.out.println("--------------")
            System.out.println(isFav)
            if (isFav) {
                db?.delete(film._id)
            } else {
                db?.insert(filmFavorite)
                System.out.println(db?.allLocal())
            }
            withContext(Dispatchers.Main) {
                callback(!isFav)
            }
        }
    }
}
