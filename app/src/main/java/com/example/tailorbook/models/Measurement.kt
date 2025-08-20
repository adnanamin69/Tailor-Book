package com.example.tailorbook.models

data class Measurement(
    val id: String = "",
    val customerId: String = "",
    val shirt_length: String = "",
    val shirt_arm: String = "",
    val shoulder: String = "",
    val collar: String = "",
    val chest: String = "",
    val lap: String = "",
    val pant: String = "",
    val panch: String = "",
    val extra: String
)
