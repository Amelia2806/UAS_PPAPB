package com.example.uas_ppapb

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas_ppapb.model.FilmUserData

// adapter `FilmUserAdapter` digunakan untuk menghubungkan data film pada bagian pengguna (user) dengan tampilan RecyclerView
// data film ditampilkan dalam item_film.xml, dan setiap item menampilkan judul dan gambar film
// adapter juga menangani klik pada setiap item, sehingga ketika pengguna mengklik suatu film
// akan diarahkan ke DetailFilmActivity dengan membawa informasi detail film

class FilmUserAdapter(private val filmUserList: ArrayList<FilmUserData>) : RecyclerView.Adapter<FilmUserAdapter.FilmUserViewHolder>() {

    // ViewHolder berfungsi untuk merepresentasikan setiap item dalam RecyclerView
    class FilmUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleUser: TextView = itemView.findViewById(R.id.judul_film_user)
        val imageUser: ImageView = itemView.findViewById(R.id.image_film_user)
    }

    // onCreateViewHolder digunakan untuk membuat instance dari ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmUserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film, parent, false)
        return FilmUserViewHolder(itemView)
    }

    // onBindViewHolder menghubungkan data film dengan ViewHolder untuk setiap item
    override fun onBindViewHolder(holder: FilmUserViewHolder, position: Int) {
        val currentItem = filmUserList[position]
        holder.titleUser.text = currentItem.title

        // menggunakan Glide untuk memuat gambar dari URL ke dalam ImageView
        Glide.with(holder.itemView.context)
            .load(currentItem.imageUrl)
            .into(holder.imageUser)

        // menangani klik pada setiap item, mengarahkan pengguna ke DetailFilmActivity dengan membawa informasi detail film
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context,DetailFilmActivity::class.java)
            intent.putExtra("judul",currentItem.title)
            intent.putExtra("director",currentItem.director)
            intent.putExtra("durasi",currentItem.durasi)
            intent.putExtra("rating",currentItem.rating)
            intent.putExtra("deskripsi",currentItem.sinopsis)
            intent.putExtra("imageUrl",currentItem.imageUrl)
            holder.itemView.context.startActivity(intent)
        }
    }

    // getItemCount mengembalikan jumlah total item dalam RecyclerView
    override fun getItemCount() = filmUserList.size
}