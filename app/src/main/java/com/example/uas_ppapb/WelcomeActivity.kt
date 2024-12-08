package com.example.uas_ppapb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uas_ppapb.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    // inisiasi binding
    private lateinit var binding: ActivityWelcomeBinding

    // memanggil aktivitas yang dibuat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        // menetapkan tampiplan ui untuk activity sesuai binding.root
        setContentView(binding.root)

        //fungsi onClick untuk tombol welcomeBtn
        binding.welcomeBtn.setOnClickListener {
            //intent untuk memulai aktivitas tab_layout
            val intent = Intent(this, LoginRegisterActivity::class.java)
            startActivity(intent)
        }

    }
}