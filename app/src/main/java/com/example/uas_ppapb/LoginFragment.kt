package com.example.uas_ppapb

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.uas_ppapb.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notificationManager: NotificationManagerCompat
    private val channelId = "TEST_NOTIF"
    private val notifId = 90

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // inflate layout untuk fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        notificationManager = NotificationManagerCompat.from(requireContext())

        // menentukan flag pending intent berdasarkan versi android
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }

        // buat intent untuk menjalankan mainactivity
        val intent = Intent(requireContext(), MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, flag)

        with(binding) {
            // memeriksa apakah pengguna sudah login
            val currentUser = firebaseAuth.currentUser
            if (getLoginStatus() && currentUser != null) {
                // jika sudah login, maka dia akan di navigasikan ke layar yang sesuai dan telah menyelesaikan activity ini
                navigateToUserOrAdmin(firebaseAuth.currentUser!!.email!!)
                activity?.finishAffinity()
            }

            // mengatur OnClickListener untuk tombol login
            loginBtn.setOnClickListener {
                val email = email.text.toString()
                val password = pass.text.toString()

                // masuk perkondisian
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // menyimpan status login ke SharedPreferences
                                saveLoginStatus(true)

                                // membuat dan menampilkan notifikasi
                                notification()

                                // menavigasi ke HomeActivity atau AdminActivity berdasarkan userType
                                navigateToUserOrAdmin(email)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Login failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "This is channel"

            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            Log.d("NotificationChannel", "Channel created successfully")
        }
    }

    private fun notification() {
        Log.d("Notification", "Notification function called")

        createNotificationChannel()

        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle("Sign In")
            .setContentText("Success")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            // memeriksa izin notifikasi
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.VIBRATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("Notification", "Missing notification permission")
                return
            }
            notify(1, builder.build())
            Log.d("Notification", "Notification displayed")
        }
    }

    // function
    // menyimpan status login
    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    // mengambil status login
    private fun getLoginStatus(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    // menavigasi ke user or admin
    private fun navigateToUserOrAdmin(email: String) {
        val userType = getUserTypeFromEmail(email)
        val username = getUsernameFromEmail(email)

        Log.d("NEW TYPEEEEEEEEEEEEEE", userType)

        val intent = if (userType == "admin") {
            Intent(requireContext(), HomeAdminActivity::class.java)
        } else {
            Intent(requireActivity(), BottomNavigationActivity::class.java)
        }

        saveUsername(username)

        startActivity(intent)
        activity?.finishAffinity()
    }

    // menyimpan username
    private fun saveUsername(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString("USERNAME_KEY", username)
        editor.apply()
    }

    // mengambil username dari email
    private fun getUsernameFromEmail(email: String): String {
        return email.substringBefore('@')
    }

    // mengambil user type dari email
    private fun getUserTypeFromEmail(email: String): String {
        return if (email.contains("admin")) {
            "admin"
        } else {
            "user"
        }
    }
}
