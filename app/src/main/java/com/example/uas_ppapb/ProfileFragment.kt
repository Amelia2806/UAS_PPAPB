package com.example.uas_film

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.uas_ppapb.LoginRegisterActivity
import com.example.uas_ppapb.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    // inisiasi variabel yang digunakan
    private lateinit var binding: FragmentProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAuth: FirebaseAuth
    // variabel yan digunakan sbg hashmap untuk menyimpan data pengguna
    private lateinit var datas: HashMap<String, String>

    private val firebase = FirebaseFirestore.getInstance()
    // referensi ke koleksi accounts di firestore
    private val movieadminCollectionRef = firebase.collection("accounts")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        firebaseAuth = Firebase.auth
        sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)

        // menambahkan listener pada koleksi firestore dengan nama accounts
        // ketika terjadi perubahan pada data dalam koleksi tsb, metode addsnapshotlistener akan dipanggil
        movieadminCollectionRef.addSnapshotListener{snapshots,e ->
                    for(snapshot in snapshots!!.documents){
                        if (snapshot.getString("role") == "user") {
                            datas = hashMapOf<String,String>(
                                "Username" to snapshot.getString("username").toString(),
                                "Email" to snapshot.get("email").toString(),
                                "Telp" to snapshot.get("phone").toString()
                            )
                            break
                        }
                    }
                    datas?.let {
                        binding.usernameProfile.text = it["Username"]
                        binding.emailProfile.text = it["Email"]
                        binding.phoneProfile.text = it["Telp"]
                    }
                }

        // untuk tombol logout
        binding.btnLogout.setOnClickListener{
            startActivity(Intent(requireActivity(), LoginRegisterActivity::class.java))
            saveLoginStatus(false)
            activity?.finishAffinity()
            Firebase.auth.signOut()
        }
        // inflate layout utk fragment ini
        return binding.root
    }

    // menyimpan status login pengguna ke dalam SharedPreferences
    // setiap kali pengguna memanggil fungsi ini dengan status login yg berbeda. nilai "isLohedIn" dalam sharedpreferences akan diperbarui
    // diperbarui sesuai dgn parameter ayng diberikan
    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    companion object {

    }
}