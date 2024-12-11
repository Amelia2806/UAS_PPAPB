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

class FilmAdminAdapter(private val filmAdminList: ArrayList<FilmUserData>, private val apiService: ApiService) : RecyclerView.Adapter<FilmAdminAdapter.FilmAdminViewHolder>() {

    class FilmAdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title_film_admin)
        val director: TextView = itemView.findViewById(R.id.director_film_admin)
        val durasi: TextView = itemView.findViewById(R.id.durasi_film_admin)
        val rating: TextView = itemView.findViewById(R.id.rating_film_admin)
        val sinopsis: TextView = itemView.findViewById(R.id.sinopsis_film_admin)
        val image: ImageView = itemView.findViewById(R.id.image_film_admin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmAdminViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.admin_film, parent, false)
        return FilmAdminViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FilmAdminViewHolder, position: Int) {
        val currentItem = filmAdminList[position]

        holder.title.text = currentItem.title
        holder.director.text = currentItem.director
        holder.durasi.text = currentItem.durasi
        holder.rating.text = currentItem.rating
        holder.sinopsis.text = currentItem.sinopsis

        Glide.with(holder.itemView.context)
            .load(currentItem.imageUrl)
            .into(holder.image)

        // Edit button logic
        holder.itemView.findViewById<ImageButton>(R.id.btn_edit).setOnClickListener {
            val intent = Intent(holder.itemView.context, AdminEditFilmActivity::class.java).apply {
                putExtra("_id", currentItem._id)
                putExtra("title", currentItem.title)
                putExtra("director", currentItem.director)
                putExtra("durasi", currentItem.durasi)
                putExtra("rating", currentItem.rating)
                putExtra("sinopsis", currentItem.sinopsis)
                putExtra("imgId", currentItem.imageUrl)
            }
            holder.itemView.context.startActivity(intent)
        }

        // Hapus button logic
        holder.itemView.findViewById<ImageButton>(R.id.btn_hapus).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apiService.deleteMovie(currentItem._id.toString()) // Panggil endpoint DELETE
                    if (response.isSuccessful) {
                        // Jika sukses, hapus dari list dan notifikasi perubahan UI
                        CoroutineScope(Dispatchers.Main).launch {
                            filmAdminList.removeAt(position)
                            notifyItemRemoved(position)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun getItemCount(): Int = filmAdminList.size
}
