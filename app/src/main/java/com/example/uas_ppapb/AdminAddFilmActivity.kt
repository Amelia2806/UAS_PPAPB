package com.example.uas_ppapb

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_ppapb.databinding.ActivityAdminAddFilmBinding
import com.example.uas_ppapb.model.FilmAdminData
import com.example.uas_pppb.network.ApiClient
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

// `AdminAddFilmActivity` adalah kelas yang mengelola aktivitas penambahan data film oleh admin
class AdminAddFilmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminAddFilmBinding

    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var imageUri: Uri

    // menerima hasil untuk mendapatkan konten gambar dari penyimpanan perangkat
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                binding.imgViewAdd.setImageURI(uri)
                // Optionally, you can call uploadData(imageUri) here if needed
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAddFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // menangani penambahan data ketika tombol "Add" ditekan
        binding.btnAdd.setOnClickListener {
//            uploadData(imageUri)
            uploadData()
        }

        // menangani pemilihan gambar ketika tombol "Choose Image" ditekan
        binding.btnChooseImage.setOnClickListener {
            getContent.launch("image/*")
        }

        // menangani kembali ke HomeAdminActivity saat tombol kembali ditekan
        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, HomeAdminActivity::class.java)
            startActivity(intent)
            finish() //
        }
    }

    // mungsi untuk mengunggah data film baru ke Firebase Database dan Firebase Storage
    private fun uploadData(imageUri: Uri? = null) {
        // mendapatkan nilai dari formulir
        val title: String = binding.txtName.text.toString()
        val director: String = binding.txtDirector.text.toString()
        val durasi: String = binding.txtDurasi.text.toString()
        val rating: String = binding.txtRating.text.toString()
        val sinopsis: String = binding.txtSinopsis.text.toString()

        // membuat ID unik untuk gambar
        val imageId = UUID.randomUUID().toString()

//        if (title.isNotEmpty() && director.isNotEmpty() && durasi.isNotEmpty() && rating.isNotEmpty() && sinopsis.isNotEmpty() && imageUri != null) {
        if (title.isNotEmpty() && director.isNotEmpty() && durasi.isNotEmpty() && rating.isNotEmpty() && sinopsis.isNotEmpty()) {
            // jika semua kolom formulir diisi dan gambar dipilih, unggah gambar ke Firebase Storage

            val item = FilmAdminData(title, director, durasi, rating, sinopsis, "imageUrl.toString()")

            System.out.println("item ========")
            System.out.println(item)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = ApiClient.api.createMovie(item)

                    if (response.isSuccessful) {
                        runOnUiThread {
                            System.out.println(response.body())

                            // navigasi ke HomeAdminActivity
                            val intent = Intent(this@AdminAddFilmActivity, HomeAdminActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@AdminAddFilmActivity, "Failed to add movie", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@AdminAddFilmActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

//            // upload image to Firebase Storage with the generated ID
//            storageReference = FirebaseStorage.getInstance().reference.child("images/$imageId")
//            val uploadTask: UploadTask = storageReference.putFile(imageUri)
//
//            uploadTask.addOnSuccessListener {
//                // jika unggah gambar berhasil, dapatkan URL unduhan gambar
//                storageReference.downloadUrl.addOnSuccessListener { imageUrl ->
//                    // buat objek FilmAdminData dengan nilai dari formulir dan URL gambar baru
//                    val item = FilmAdminData(title, director, durasi, rating, sinopsis, imageUrl.toString())
//
//                    // mendapatkan referensi ke Firebase Database
//                    database = FirebaseDatabase.getInstance().getReference("Film")
//
//                    // menyimpan data baru ke firebase database
//                    database.child(imageId).setValue(item)
//                        .addOnCompleteListener {
//
//                            // engosongkan kolom formulir setelah berhasil unggah data
//                            binding.txtName.text!!.clear()
//                            binding.txtDirector.text!!.clear()
//                            binding.txtDurasi.text!!.clear()
//                            binding.txtRating.text!!.clear()
//                            binding.txtSinopsis.text!!.clear()
//
//                            // menampilkan pesan data berhasil di up
//                            Toast.makeText(this, "Data Uploaded Successfully", Toast.LENGTH_SHORT).show()
//
//                            Log.d("URIIII",imageUri.toString())
//                            Log.d("URIIII",imageUri.toString())
//                            Log.d("URIIII",imageUri.toString())
//                            Log.d("URIIII",imageUri.toString())
//                            Log.d("URIIII",imageUri.toString())
//
//                            // navigasi ke HomeAdminActivity
//                            val intent = Intent(this, HomeAdminActivity::class.java)
//                            startActivity(intent)
//                            finish()
//                        }
//                        .addOnFailureListener {
//                            // menampilkan pesan gagal jika ada kesalahan
//                            Toast.makeText(this, "Adding Data Failed!", Toast.LENGTH_SHORT).show()
//                        }
//                }
//            }.addOnFailureListener {
//                // menampilkan pesan gagal jika unggah gambar gagal
//                Toast.makeText(this, "Image Upload Failed!", Toast.LENGTH_SHORT).show()
//            }
        } else {

            // menampilkan pesan jika ada kolom formulir yang belum diisi atau gambar tidak dipilih
            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show()
        }

    }
}