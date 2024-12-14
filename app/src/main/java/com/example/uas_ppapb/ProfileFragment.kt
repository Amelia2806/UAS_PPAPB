package com.example.uas_ppapb

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.uas_ppapb.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    // Deklarasi variabel untuk mengikat tampilan, SharedPreferences, Firestore, dan autentikasi Firebase
    private lateinit var binding: FragmentProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Mengikat tampilan menggunakan View Binding
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi SharedPreferences, Firestore, dan autentikasi Firebase
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        // Mengambil email yang disimpan dari SharedPreferences
        val email = sharedPreferences.getString("EMAIL_KEY", null)

        // Jika email ditemukan, ambil data profil dari Firestore
        if (email != null) {
            firestore.collection("accounts")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val document = documents.documents[0]
                        // Menampilkan data profil pengguna di UI
                        binding.usernameProfile.text = document.getString("username")
                        binding.emailProfile.text = document.getString("email")
                        binding.phoneProfile.text = document.getString("phone")
                    }
                }
                .addOnFailureListener {
                    // Jika gagal mengambil data, tampilkan toast
                    Toast.makeText(requireContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show()
                }
        }

        // Mengatur aksi tombol Logout
        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut() // Melakukan sign out dari Firebase Authentication
            clearSharedPreferences() // Menghapus data login dari SharedPreferences
            Toast.makeText(requireContext(), "Logout successful!", Toast.LENGTH_SHORT).show()
            navigateToLogin() // Arahkan pengguna ke halaman login
        }
    }

    private fun clearSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.clear() // Menghapus semua data yang tersimpan di SharedPreferences
        editor.apply()
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
        startActivity(intent) // Memulai aktivitas LoginRegisterActivity
        requireActivity().finishAffinity() // Menutup semua aktivitas sebelumnya
    }
}
