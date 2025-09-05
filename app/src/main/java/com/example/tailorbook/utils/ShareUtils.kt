package com.example.tailorbook.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity

object ShareUtils {
    
    fun shareInvoice(context: Context, invoiceText: String, customerPhone: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, invoiceText)
            putExtra(Intent.EXTRA_SUBJECT, "Invoice from Tailor Book")
        }
        
        // Try to share via WhatsApp if available
        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.whatsapp")
            putExtra(Intent.EXTRA_TEXT, invoiceText)
            putExtra(Intent.EXTRA_SUBJECT, "Invoice from Tailor Book")
        }
        
        // Try to share via SMS
        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("sms:$customerPhone")
            putExtra("sms_body", invoiceText)
        }
        
        // Create chooser with multiple options
        val chooserIntent = Intent.createChooser(shareIntent, "Share Invoice").apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(whatsappIntent, smsIntent))
        }
        
        try {
            startActivity(context, chooserIntent, null)
        } catch (e: Exception) {
            // Fallback to regular share intent
            try {
                startActivity(context, shareIntent, null)
            } catch (e2: Exception) {
                // Handle error - could show a toast or snackbar
            }
        }
    }
    
    fun shareViaWhatsApp(context: Context, invoiceText: String, customerPhone: String) {
        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.whatsapp")
            putExtra(Intent.EXTRA_TEXT, invoiceText)
        }
        
        try {
            startActivity(context, whatsappIntent, null)
        } catch (e: Exception) {
            // WhatsApp not installed, fallback to regular share
            shareInvoice(context, invoiceText, customerPhone)
        }
    }
    
    fun shareViaSMS(context: Context, invoiceText: String, customerPhone: String) {
        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("sms:$customerPhone")
            putExtra("sms_body", invoiceText)
        }
        
        try {
            startActivity(context, smsIntent, null)
        } catch (e: Exception) {
            // Handle error
        }
    }
}
