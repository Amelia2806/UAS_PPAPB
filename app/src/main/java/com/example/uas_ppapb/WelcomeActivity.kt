package com.example.uas_ppapb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uas_ppapb.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    // Inisialisasi binding untuk mengikat elemen tata letak dengan view binding
    private lateinit var binding: ActivityWelcomeBinding

    // Metode yang dipanggil ketika aktivitas dibuat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengikat tampilan dengan tata letak menggunakan view binding
        binding = ActivityWelcomeBinding.inflate(layoutInflater)

        // Menetapkan tampilan UI untuk aktivitas ini sesuai dengan layout dari binding.root
        setContentView(binding.root)

        // Menambahkan fungsi click listener untuk tombol welcomeBtn
        binding.welcomeBtn.setOnClickListener {
            // Membuat intent untuk berpindah ke aktivitas LoginRegisterActivity
            val intent = Intent(this, LoginRegisterActivity::class.java)

            // Memulai aktivitas LoginRegisterActivity
            startActivity(intent)
        }
    }
}
