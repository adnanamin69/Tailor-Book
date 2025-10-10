package com.example.tailorbook.utils

import com.example.tailorbook.models.Order
import com.example.tailorbook.models.User
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object InvoiceGenerator {

    fun generateInvoiceText(customer: User, order: Order): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

        return buildString {
            appendLine("🧵 KHAN G TAILORING SHOP. WAH Cant - INVOICE")
            appendLine("=" * 30)
            appendLine()

            // Customer Details
            appendLine("👤 CUSTOMER DETAILS:")
            appendLine("Name: ${customer.name}")
            appendLine("Phone: ${customer.phone}")
            appendLine()

            // Order Details
            appendLine("📋 ORDER DETAILS:")
            appendLine("Order ID: #${order.id.take(8).uppercase()}")
            appendLine("Dress Type: ${order.dressType.ifEmpty { "Custom Order" }}")
            appendLine("Status: ${order.status.name.replace("_", " ").uppercase()}")
            appendLine("Check-in Date: ${dateFormat.format(Date(order.checkInDate))}")
            appendLine("Delivery Date: ${dateFormat.format(Date(order.deliveryDate))}")
            appendLine()

            // Financial Details
            appendLine("💰 FINANCIAL SUMMARY:")
            appendLine("Total Amount: ${currencyFormat.format(order.totalDue)}")
            appendLine("Amount Paid: ${currencyFormat.format(order.totalPaid)}")
            appendLine("Balance Due: ${currencyFormat.format(order.balance)}")
            appendLine()

            // Footer
            appendLine("=" * 30)
            appendLine("Thank you for choosing KHAN G TAILORING SHOP!")
            appendLine("Generated on: ${dateFormat.format(Date())}")
        }
    }

    fun generateInvoiceForSharing(customer: User, order: Order): String {
        return generateInvoiceText(customer, order)
    }
}

// Extension function for string repetition
private operator fun String.times(n: Int): String = this.repeat(n)
