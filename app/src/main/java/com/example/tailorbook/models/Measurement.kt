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
    val shalwar: String = "",
    val extra: String,



    val kalar: String = "کالر",
    val daman: String = "گول دامن",
    val bazo: String = "فٹ بازو",
    val sidePoket: String = "0",
    val button: String = "سادہ",
    val cup: String = "چورس",
    val chamakDaga: String = "سنگل",
    val frontPoket: Boolean = false,
    val shalwarPoket: Boolean = false

)
