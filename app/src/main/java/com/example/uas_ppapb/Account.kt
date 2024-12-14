package com.example.uas_ppapb

// Data class Account digunakan untuk merepresentasikan entitas akun di Firestore
data class Account(
    // Beberapa properti yang digunakan untuk menyimpan data dengan nilai default string kosong
    var email: String = "", // Properti untuk menyimpan alamat email akun
    var username: String = "", // Properti untuk menyimpan nama pengguna
    var password: String = "", // Properti untuk menyimpan kata sandi
    var phone: String = "", // Properti untuk menyimpan nomor telepon
    var role: String = "user" // Properti untuk menyimpan peran pengguna, dengan nilai default "user"
)
