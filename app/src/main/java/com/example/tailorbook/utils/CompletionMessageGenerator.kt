package com.example.tailorbook.utils

import com.example.tailorbook.models.Order
import com.example.tailorbook.models.User
import java.text.NumberFormat
import java.util.*

object CompletionMessageGenerator {

    fun generateCompletionMessage(customer: User, order: Order): String {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        val balance = order.balance

        return buildString {
            appendLine("🎉 Great News!")
            appendLine()
            appendLine("Dear ${customer.name},")
            appendLine()
            appendLine("Your order has been completed and is ready for pickup!")
            appendLine()
            appendLine("📋 Order Details:")
            appendLine("• Order ID: #${order.id.take(8).uppercase()}")
            if (order.dressType.isNotEmpty()) {
                appendLine("• Item: ${order.dressType}")
            }
            appendLine("• Status: ✅ COMPLETED")
            appendLine()

            if (balance > 0) {
                appendLine("💰 Payment Information:")
                appendLine("• Total Amount: ${currencyFormat.format(order.totalDue)}")
                appendLine("• Amount Paid: ${currencyFormat.format(order.totalPaid)}")
                appendLine("• Outstanding Balance: ${currencyFormat.format(balance)}")
                appendLine()
                appendLine("Please bring the outstanding amount when you visit to collect your order.")
                appendLine()
            } else {
                appendLine("✅ Payment Status: Fully Paid")
                appendLine()
            }

            appendLine("🏪 Please visit our shop to collect your order.")
            appendLine()
            appendLine("Thank you for choosing our services!")
            appendLine("We look forward to serving you again.")
            appendLine()
            appendLine("Best regards,")
            appendLine("KHAN G TAILORING SHOP. WAH Cant New City Phase 1")
        }
    }

    fun generateCompletionMessageForSharing(customer: User, order: Order): String {
        return generateCompletionMessage(customer, order)
    }
}
