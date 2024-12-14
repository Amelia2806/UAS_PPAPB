package com.example.uas_ppapb

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.uas_ppapb.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    // Inisialisasi variabel untuk mengikat tampilan XML
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth // Firebase Auth untuk autentikasi pengguna
    private lateinit var sharedPreferences: SharedPreferences // SharedPreferences untuk menyimpan data lokal
    private lateinit var firestore: FirebaseFirestore // Firestore untuk database cloud

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Mengikat tampilan dengan View Binding dari layout XML
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi Firebase Auth, SharedPreferences, dan Firestore instance
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        firestore = FirebaseFirestore.getInstance()

        // Mengatur listener untuk tombol daftar
        binding.regisBtn.setOnClickListener {
            // Mengambil data dari inputan pengguna
            val email = binding.email.text.toString().trim()
            val username = binding.username.text.toString().trim()
            val phone = binding.phone.text.toString()
            val password = binding.pass.text.toString()

            // Validasi inputan pengguna sebelum mendaftar
            if (email.isNotEmpty() && username.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                // Menggunakan Firebase Auth untuk membuat akun dengan email dan password
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Jika berhasil, buat data akun dan simpan ke Firestore
                            val newAccount = hashMapOf(
                                "email" to email,
                                "username" to username,
                                "phone" to phone,
                                "role" to "user"
                            )
                            saveEmailToSharedPreferences(email) // Simpan email ke SharedPreferences
                            firestore.collection("accounts").add(newAccount)
                                .addOnSuccessListener {
                                    // Jika berhasil menyimpan data ke Firestore
                                    Toast.makeText(
                                        requireContext(),
                                        "Registration successful!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Berpindah ke halaman login utama
                                    (requireActivity() as LoginRegisterActivity).binding.viewPager2.setCurrentItem(0, true)
                                }
                                .addOnFailureListener {
                                    // Jika gagal menyimpan data ke Firestore
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed to save user data",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            // Jika gagal membuat akun dengan Firebase Auth
                            Toast.makeText(
                                requireContext(),
                                "Registration failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                // Jika inputan belum lengkap
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk menyimpan email ke SharedPreferences
    private fun saveEmailToSharedPreferences(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("EMAIL_KEY", email) // Menyimpan email dengan key "EMAIL_KEY"
        editor.apply()
    }
}
