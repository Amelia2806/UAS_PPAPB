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
import com.example.uas_ppapb.model.FilmAdminData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

// kelas `FilmAdminAdapter` adalah adaptor RecyclerView untuk tampilan daftar film pada bagian admin
// adaptor ini mengelola item-item dalam daftar dan mengatur tampilan masing-masing item
class FilmAdminAdapter(private val filmAdminList: ArrayList<FilmAdminData>) : RecyclerView.Adapter<FilmAdminAdapter.FilmAdminViewHolder>() {

    // kelas `FilmAdminViewHolder` merepresentasikan tampilan item pada daftar film admin
    class FilmAdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title_film_admin)
        val director: TextView = itemView.findViewById(R.id.director_film_admin)
        val durasi: TextView = itemView.findViewById(R.id.durasi_film_admin)
        val rating: TextView = itemView.findViewById(R.id.rating_film_admin)
        val sinopsis: TextView = itemView.findViewById(R.id.sinopsis_film_admin)
        val image: ImageView = itemView.findViewById(R.id.image_film_admin)
    }

    // metode yang dipanggil ketika RecyclerView memerlukan tampilan baru untuk mewakili item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmAdminViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.admin_film, parent, false)
        return FilmAdminViewHolder(itemView)
    }

    // metode yang dipanggil untuk menetapkan data film pada tampilan item yang ditangani oleh `FilmAdminViewHolder`
    override fun onBindViewHolder(holder: FilmAdminViewHolder, position: Int) {
        val currentItem = filmAdminList[position]

        // menetapkan nilai dari objek `FilmAdminData` ke tampilan masing-masing item
        holder.title.setText(currentItem.title)
        holder.director.setText(currentItem.director)
        holder.durasi.setText(currentItem.durasi)
        holder.rating.setText(currentItem.rating)
        holder.sinopsis.setText(currentItem.sinopsis)

        holder.title.text = currentItem.title

        // menggunakan Glide untuk memuat gambar dari URL ke dalam ImageView
        Glide.with(holder.itemView.context)
            .load(currentItem.imageUrl)
            .into(holder.image)

        // menangani aksi ketika tombol edit ditekan
        holder.itemView.findViewById<ImageButton>(R.id.btn_edit).setOnClickListener{
            val intent = Intent(holder.itemView.context, AdminEditFilmActivity::class.java)
            val currentItem = filmAdminList[position]
            intent.putExtra("title", currentItem.title)
            intent.putExtra("director", currentItem.director)
            intent.putExtra("durasi", currentItem.durasi)
            intent.putExtra("rating", currentItem.rating)
            intent.putExtra("sinopsis", currentItem.sinopsis)
            intent.putExtra("imgId", currentItem.imageUrl)
            holder.itemView.context.startActivity(intent)
        }

        // menangani aksi ketika tombol hapus ditekan
        holder.itemView.findViewById<ImageButton>(R.id.btn_hapus).setOnClickListener{
            val itemToDelete = Uri.parse(filmAdminList[position].imageUrl.toString()).lastPathSegment?.removePrefix("images/")

            // menghapus item dari daftar
            filmAdminList.removeAt(position)

            // memberi tahu adaptor tentang perubahan data
            notifyDataSetChanged()

            // menghapus data yang sesuai dari Realtime Database
            deleteItemFromDatabase(itemToDelete.toString())
        }

    }

    // metode untuk menghapus item dari Firebase Storage dan Realtime Database berdasarkan ID gambar
    private fun deleteItemFromDatabase(imgId: String) {
        // referensi ke Firebase Storage
        val storageReference = FirebaseStorage.getInstance().getReference("images").child(imgId)

        // menghapus gambar dari Firebase Storage
        storageReference.delete().addOnSuccessListener {
            // jika gambar dihapus dengan sukses, hapus juga data yang sesuai dari Realtime Database
            val database = FirebaseDatabase.getInstance().getReference("Film")
            database.child(imgId).removeValue()
                .addOnCompleteListener {
                }
                .addOnFailureListener {
                }
        }.addOnFailureListener {
        }
    }



    // metode yang memberikan jumlah item dalam daftar
    override fun getItemCount() = filmAdminList.size
}