package com.example.tailorbook.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.google.android.gms.common.wrappers.Wrappers.packageManager
import java.net.URLEncoder


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
        // Clean the phone number thoroughly
        val phone = customerPhone
            .replace("[+\\s()-]", "") // Remove +, spaces, parentheses, and dashes
            .trim()

        Log.i("WhatsApp", "Original phone: '$customerPhone', Cleaned phone: '$phone'")

        val phoneWithCountryCode = if (!phone.startsWith("+")) "+92${phone.substring(1)}" else phone
        val url = "https://api.whatsapp.com/send?phone=$phoneWithCountryCode&text=${
            URLEncoder.encode(
                invoiceText,
                "UTF-8"
            )
        }"
        Log.i("WhatsApp", "Generated URL: $url")


        // Define package names for WhatsApp and WhatsApp Business
        val whatsappPackage = "com.whatsapp"
        val whatsappBusinessPackage = "com.whatsapp.w4b"

        // Create intent
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }

        val packageManager = context.packageManager
        val activities = packageManager.queryIntentActivities(intent, 0)

        // Filter for WhatsApp and WhatsApp Business
        val targetIntents = mutableListOf<Intent>()
        val whatsappInstalled = activities.any { it.activityInfo.packageName == whatsappPackage }
        val whatsappBusinessInstalled =
            activities.any { it.activityInfo.packageName == whatsappBusinessPackage }

        when {
            whatsappInstalled && whatsappBusinessInstalled -> {
                // Both apps are installed, create intents for both
                val whatsappIntent = Intent(intent).apply {
                    setPackage(whatsappPackage)
                }
                val whatsappBusinessIntent = Intent(intent).apply {
                    setPackage(whatsappBusinessPackage)
                }
                targetIntents.add(whatsappIntent)
                targetIntents.add(whatsappBusinessIntent)

                // Create a chooser with only WhatsApp and WhatsApp Business
                val chooserIntent =
                    Intent.createChooser(targetIntents.removeAt(0), "Share via").apply {
                        putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toTypedArray())
                    }
                try {
                    context.startActivity(chooserIntent)
                } catch (e: Exception) {
                    Log.e("WhatsApp", "Error starting chooser: ${e.message}")
                    shareInvoice(context, invoiceText, customerPhone)
                }
            }

            whatsappInstalled -> {
                // Only WhatsApp is installed
                intent.setPackage(whatsappPackage)
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e("WhatsApp", "Error starting WhatsApp: ${e.message}")
                    shareInvoice(context, invoiceText, customerPhone)
                }
            }

            whatsappBusinessInstalled -> {
                // Only WhatsApp Business is installed
                intent.setPackage(whatsappBusinessPackage)
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e("WhatsApp", "Error starting WhatsApp Business: ${e.message}")
                    shareInvoice(context, invoiceText, customerPhone)
                }
            }

            else -> {
                // Neither app is installed
                Log.e("WhatsApp", "Neither WhatsApp nor WhatsApp Business is installed")
                shareInvoice(context, invoiceText, customerPhone)
            }
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
