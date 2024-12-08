package com.example.uas_ppapb

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.uas_ppapb.databinding.ActivityAdminEditFilmBinding
import com.example.uas_ppapb.model.FilmUserData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

// `AdminEditFilmActivity` adalah kelas yang mengelola aktivitas pengeditan data film oleh admin
class AdminEditFilmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminEditFilmBinding
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri ?= null

    // menerima hasil untuk mendapatkan konten gambar dari penyimpanan perangkat
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                binding.imgViewEdit.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminEditFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // mengaktifkan pemilihan gambar ketika tombol "Choose Image" ditekan
        binding.btnChooseImage.setOnClickListener {
            getContent.launch("image/*")
        }

        // mengisi formulir dengan data film yang akan diedit
        val title = binding.txtTitleEdit
        val director = binding.txtDirectorEdit
        val durasi = binding.txtDurasiEdit
        val rating = binding.txtRatingEdit
        val sinopsis = binding.txtSinopsisEdit

        val originalImageUrl = intent.getStringExtra("imgId")
        Glide.with(this)
            .load(originalImageUrl)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(binding.imgViewEdit)

        title.setText(intent.getStringExtra("title"))
        director.setText(intent.getStringExtra("director"))
        durasi.setText(intent.getStringExtra("durasi"))
        rating.setText(intent.getStringExtra("rating"))
        sinopsis.setText(intent.getStringExtra("sinopsis"))

        // menangani pembaruan data ketika tombol "Update" ditekan
        binding.btnUpdate.setOnClickListener {
            uploadData(imageUri)
        }

        // menambahkan OnClickListener untuk tombol kembali
        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, HomeAdminActivity::class.java)
            startActivity(intent)
            finish()
        }
        }

    // fungsi untuk mengunggah data film ke Firebase Database dan Firebase Storage
    private fun uploadData(imageUri: Uri? = null) {
        // mendapatkan nilai yang diperbarui dari form
        val updatedTitle = binding.txtTitleEdit.text.toString()
        val updatedDirector = binding.txtDirectorEdit.text.toString()
        val updatedDurasi = binding.txtDurasiEdit.text.toString()
        val updatedRating = binding.txtRatingEdit.text.toString()
        val updatedSinopsis = binding.txtSinopsisEdit.text.toString()

        // mendapatkan referensi ke Firebase Database
        database = FirebaseDatabase.getInstance().getReference("Film")

        if (imageUri != null) {
            // jika gambar baru dipilih, unggah gambar ke Firebase Storage
            val imageId = Uri.parse(intent.getStringExtra("imgId")).lastPathSegment?.removePrefix("images/")

            // jika unggah gambar berhasil, dapatkan URL unduhan gambar
            storageReference = FirebaseStorage.getInstance().reference.child("images/$imageId")
            val uploadTask: UploadTask = storageReference.putFile(imageUri)


            uploadTask.addOnSuccessListener {
                // buat objek FilmAdminData dengan nilai yang diperbarui dan URL gambar baru
                storageReference.downloadUrl.addOnSuccessListener { imageUrl ->
                    val item = FilmUserData(
                        updatedTitle,
                        updatedDirector,
                        updatedDurasi,
                        updatedRating,
                        updatedSinopsis,
                        imageUrl.toString()
                    )

                    // simpan data baru ke Firebase Database
                    database.child(imageId!!).setValue(item)
                        .addOnCompleteListener {
                            // handle ketika berhasil
                            Toast.makeText(this, "Data Uploaded Successfully", Toast.LENGTH_SHORT).show()

                            // mulai HomeAdminActivity setelah pembaruan berhasil
                            val intent = Intent(this, HomeAdminActivity::class.java)
                            startActivity(intent)

                            // finish current activity
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Adding Data Failed!", Toast.LENGTH_SHORT).show()
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Image Upload Failed!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d("testUri", "${imageUri}")
            Log.d("testUri", "${imageUri}")
            Log.d("testUri", "${imageUri}")

            // jika gambar tidak diperbarui, update data tanpa mengunggah gambar baru
            val imageId = Uri.parse(intent.getStringExtra("imgId")).lastPathSegment?.removePrefix("images/")

            val updatedList = mapOf(
                "title" to updatedTitle,
                "director" to updatedDirector,
                "writter" to updatedDurasi,
                "rating" to updatedRating,
                "sinopsis" to updatedSinopsis
            )

            // update data dengan judul yang baru
            database.child(imageId!!).updateChildren(updatedList)
                .addOnCompleteListener {
                    // Handle completion, e.g., show a success message
                    Toast.makeText(this, "Data Updated Successfully", Toast.LENGTH_SHORT).show()

                    // mulai HomeAdminActivity setelah pembaruan berhasil
                    val intent = Intent(this, HomeAdminActivity::class.java)
                    startActivity(intent)

                    // Finish current activity
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Updating Data Failed!", Toast.LENGTH_SHORT).show()
                }
        }
    }
}