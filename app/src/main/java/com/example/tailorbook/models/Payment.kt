package com.example.tailorbook.models

data class Payment(
    val id: String = "",
    val orderId: String = "",
    val amount: Double = 0.0,
    val date: Long = 0L
)
