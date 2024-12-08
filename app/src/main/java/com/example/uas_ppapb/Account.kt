package com.example.uas_ppapb

// data class account digunakan untuk merepresentasikan entitas akun di firestore
data class Account(
    // beberapa properti yang digunakan untuk menyimpan data dengan default string kosong
    var email: String ="",
    var username: String ="",
    var password: String ="",
    var phone: String ="",
    var role: String ="user"
)