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

    // deklarasi properti yang digunakan dalam class registerfragment
    // insiasi view binding pada layout fragment
    private lateinit var binding: FragmentRegisterBinding
    // objek dari kelas firebaseauth yan digunakan untuk interaksi dengan layanan otentikasi firebase
    private lateinit var firebaseAuth: FirebaseAuth
    // objek dari kelas sharedpreference yang digunakan untuk menyimpan data sederhana dalam bentuk key-value
    private lateinit var sharedPreferences: SharedPreferences
    // interaksi dengan layanan firestore dari firebase yang merupakan database cloud nosql
    private lateinit var firestore: FirebaseFirestore

    // membuat dan menginisialisasi tampilan fragment seperti inflater, container, savedinstancestate
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // inisiasi instance firebase
        // digunakan untuk otentikasi pengguna dengan Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()
        // mendapatkan instance dari SharedPreferences dengan nama "MyPrefs
        // digunakan untuk menyimpan dan mengambil data sederhana dalam bentuk pasangan key-value
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        // digunakan untuk berinteraksi dengan Firebase Cloud Firestore
        firestore = FirebaseFirestore.getInstance()

        // mengatur click listener untuk tombol registrasi
        with(binding) {
            regisBtn.setOnClickListener {
                // mengekstrak inputan dari pengguna/user
                val email = email.text.toString().trim()
                val username = username.text.toString().trim()
                val phone = phone.text.toString()
                val password = pass.text.toString()

                // memeriksa apakah semua kolom registrasi terisi
                if (email.isNotEmpty() && username.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                    // mencoba membuat pengguna baru dengan otentikasi firebase
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // jika registrasi berhasil, maka dia akan menyimpan data pengguna ke firestore
                                val newAccount = Account(email, username, password, phone)
                                saveUserDataToFirestore(newAccount)

                                // menyimpan status login ke sharedpreferences
                                saveLoginStatus(true)

                                // pindah ke halaman login
                                val viewPager = (requireActivity() as LoginRegisterActivity).binding.viewPager2
                                // mengatur tampilan indeks dari ke 0
                                viewPager.setCurrentItem(0, true)

                                // navigasi ke home admin atau user admin berdasarkan usertype
                                // navigasi ke HomeOrAdmin("user")
                            } else {
                                // menampilkan pesan toast jika registrasi gagal
                                Toast.makeText(
                                    requireContext(),
                                    "Registration failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // log kesalahan untuk debug
                                Log.e("RegisterrFragment", "Error creating user", task.exception)
                            }
                        }
                } else {
                    // menampilkan pesan toast jika ada kolom yang kosong
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveUserDataToFirestore(account: Account) {
        // menyimpan data pengguna (accounts) ke firestore
        firestore.collection("accounts")
            .add(account)
            .addOnSuccessListener { documentReference ->
                Log.d("RegisterrFragment", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("RegisterrFragment", "Error adding document to Firestore", e)
                Toast.makeText(
                    requireContext(),
                    "Error adding document to Firestore: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        // menyimpan status login ke sharedpreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    private fun navigateToHomeOrAdmin(userType: String) {
        // navigasi ke homeactivity atau adminactivity berdasarkan userType
        // jika di emailnya terdapat kata admin maka rolenya masuk ke admin
        val intent = if (userType == "admin") {
            Intent(requireContext(), HomeAdminActivity::class.java)
        } else {
            Intent(requireContext(), BottomNavigationActivity::class.java)
        }

        startActivity(intent)
        requireActivity().finish()
    }
}