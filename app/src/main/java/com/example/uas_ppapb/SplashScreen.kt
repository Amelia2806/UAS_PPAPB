package com.example.uas_ppapb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uas_ppapb.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    // Mengikat tata letak aktivitas menggunakan view binding untuk mengakses elemen UI
    private lateinit var binding: ActivitySplashScreenBinding

    // Menentukan durasi untuk menampilkan splashscreen dalam milidetik (3000 milidetik = 3 detik)
    private val SPLASH_DISPLAY_LENGTH = 3000

    // Metode yang dipanggil ketika aktivitas pertama kali dibuat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengikat tampilan dengan tata letak menggunakan view binding
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur tingkat transparansi awal elemen dengan ID ivLogo menjadi 0 (transparan) sebelum animasi
        binding.ivLogo.alpha = 0f

        // Menerapkan animasi logo memudar (fade in) dari transparan (0) menjadi tidak transparan (1) selama 3 detik
        binding.ivLogo.animate().setDuration(3000).alpha(1f).withEndAction() {
            // Membuat intent untuk berpindah ke activity WelcomeActivity
            val mainIntent = Intent(this@SplashScreen, WelcomeActivity::class.java)

            // Memulai aktivitas WelcomeActivity
            startActivity(mainIntent)

            // Menutup aktivitas SplashScreen setelah berpindah, agar pengguna tidak bisa kembali ke halaman ini dengan tombol kembali
            finish()
        }
    }
}
