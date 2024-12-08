package com.example.uas_ppapb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uas_ppapb.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    // mengikat tata letak aktivitas menggunakan view binding
    private lateinit var binding: ActivitySplashScreenBinding
    // menentukan lamanya splashscreen yaitu 3000milidetik atau 3 detik
    private val SPLASH_DISPLAY_LENGTH = 3000
    // metode yang dipanggil ketika activity dibuat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // mengatur tingkat transparansi awal elemen dengan ID ivLogo menjadi 0 (transparan)
        binding.ivLogo.alpha = 0f
        // menerapkan animasi secara singat, logo akan memudar dari transparan (0) menjadi tidak transparan(1) selama 3 detik
        binding.ivLogo.animate().setDuration(3000).alpha(1f).withEndAction() {
            // intent untuk berpindah ke activity welcomeactivity
            val mainIntent = Intent(this@SplashScreen, WelcomeActivity::class.java)
            // memulai activity welcomeactivity
            startActivity(mainIntent)
            // menutup activity spashscreen, shg pengguna tidak dapat kembali ke halaman ini dgn menekan tombol kembali
            finish()
        }
    }
}