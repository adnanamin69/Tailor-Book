package com.example.tailorbook.models

enum class OrderStatus { PENDING, IN_PROGRESS, COMPLETED, DELIVERED }

data class Order(
    val id: String = "",
    val customerId: String = "",
    val measurementId: String = "",
    val dressType: String = "",
    val checkInDate: Long = 0L,
    val deliveryDate: Long = 0L,
    val status: OrderStatus = OrderStatus.PENDING,
    val totalDue: Double = 0.0,
    val totalPaid: Double = 0.0
) {
    val balance: Double get() = (totalDue - totalPaid).coerceAtLeast(0.0)
}
