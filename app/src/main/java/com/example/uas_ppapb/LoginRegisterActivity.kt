package com.example.uas_ppapb

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_ppapb.databinding.ActivityLoginRegisterBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginRegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginRegisterBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // menggunakan view binding untuk meng-inflate layout
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // inisialisasi SharedPreferences untuk menyimpan data secara lokal
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // memeriksa apakah pengguna sudah masuk
        val currentUser = Firebase.auth.currentUser

        // menyiapkan viewpager2 dan tablayout
        val adapter = LoginRegisAdapter(this@LoginRegisterActivity)
        binding.viewPager2.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->

            // menetapkan teks tab berdasarkan posisi
            tab.text = when (position) {
                0 -> "Login"
                1 -> "Register"
                else -> ""
            }
        }.attach()
    }
}