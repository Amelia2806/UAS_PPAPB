package com.example.uas_ppapb

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.uas_ppapb.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding // Mengikat tampilan dengan View Binding
    private lateinit var firebaseAuth: FirebaseAuth // Menggunakan Firebase Authentication
    private lateinit var sharedPreferences: SharedPreferences // Untuk menyimpan data ke SharedPreferences
    private val channelId = "LOGIN_NOTIFICATION" // ID untuk Notification Channel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Mengikat tampilan dengan fragment login melalui View Binding
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance() // Inisialisasi FirebaseAuth
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE) // Inisialisasi SharedPreferences

        // Mengatur aksi ketika tombol login ditekan
        binding.loginBtn.setOnClickListener {
            val email = binding.email.text.toString() // Mengambil input email
            val password = binding.pass.text.toString() // Mengambil input password

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Memvalidasi login dengan Firebase Authentication
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Jika login berhasil, simpan email ke SharedPreferences, tunjukkan notifikasi, dan navigasikan ke halaman yang sesuai
                            saveEmailToSharedPreferences(email)
                            showLoginNotification()
                            Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                            navigateToUserOrAdmin(email)
                        } else {
                            // Jika login gagal, tampilkan pesan error
                            Toast.makeText(
                                requireContext(),
                                "Login failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                // Jika email atau password kosong, berikan pesan kepada pengguna
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveEmailToSharedPreferences(email: String) {
        // Menyimpan email pengguna ke SharedPreferences untuk digunakan nanti
        val editor = sharedPreferences.edit()
        editor.putString("EMAIL_KEY", email)
        editor.apply()
    }

    private fun showLoginNotification() {
        // Membuat notifikasi jika login berhasil
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Membuat saluran notifikasi untuk versi Android Oreo dan lebih tinggi
            val channel = NotificationChannel(
                channelId, "Login Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Notifications for login success"
            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Membuat notifikasi yang akan ditampilkan kepada pengguna
        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.baseline_notifications_active_24) // Ikon notifikasi
            .setContentTitle("Login Success") // Judul notifikasi
            .setContentText("You have successfully logged in!") // Pesan notifikasi
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Prioritas notifikasi
            .setAutoCancel(true) // Notifikasi akan dihapus ketika ditekan

        // Menampilkan notifikasi menggunakan NotificationManagerCompat
        NotificationManagerCompat.from(requireContext()).notify(1, builder.build())
    }

    private fun navigateToUserOrAdmin(email: String) {
        // Navigasi pengguna berdasarkan email ke aktivitas yang sesuai
        val intent = if (email.contains("admin")) {
            Intent(requireContext(), HomeAdminActivity::class.java) // Jika email mengandung "admin", navigasi ke halaman admin
        } else {
            Intent(requireContext(), BottomNavigationActivity::class.java) // Jika bukan admin, navigasi ke halaman biasa
        }
        startActivity(intent) // Memulai aktivitas baru
        requireActivity().finishAffinity() // Mengakhiri aktivitas sebelumnya agar tidak bisa kembali dengan tombol back
    }
}
