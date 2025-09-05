package com.example.tailorbook.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tailorbook.MainActivity
import com.example.tailorbook.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

object NotificationService {
    private const val CHANNEL_ID = "tailor_book_reminders"
    private const val CHANNEL_NAME = "Tailor Book Reminders"
    private const val CHANNEL_DESCRIPTION = "Daily reminders for pending orders"
    private const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    suspend fun sendDailyReminder(context: Context) {
        try {
            val uid = FirebaseAuth.getInstance().uid ?: return
            val db = FirebaseFirestore.getInstance()
            
            // Get all customers and their pending orders
            val customersSnap = db.collection("users").document(uid).collection("customers").get().await()
            var pendingOrdersCount = 0
            var todayDeliveryCount = 0
            
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            val tomorrow = today + (24 * 60 * 60 * 1000)

            for (customer in customersSnap.documents) {
                val ordersSnap = db.collection("users").document(uid)
                    .collection("customers").document(customer.id)
                    .collection("orders").get().await()
                
                ordersSnap.documents.forEach { orderDoc ->
                    val status = orderDoc.getString("status") ?: "PENDING"
                    val deliveryDate = orderDoc.getLong("deliveryDate") ?: 0L
                    
                    if (status == "PENDING" || status == "IN_PROGRESS") {
                        pendingOrdersCount++
                        
                        // Check if delivery is today
                        if (deliveryDate >= today && deliveryDate < tomorrow) {
                            todayDeliveryCount++
                        }
                    }
                }
            }

            if (pendingOrdersCount > 0) {
                val title = if (todayDeliveryCount > 0) {
                    "🚨 $todayDeliveryCount orders due today!"
                } else {
                    "📋 $pendingOrdersCount pending orders"
                }
                
                val message = if (todayDeliveryCount > 0) {
                    "You have $todayDeliveryCount orders due for delivery today. Check your pending orders!"
                } else {
                    "You have $pendingOrdersCount pending orders. Have a look at them to complete for today!"
                }

                showNotification(context, title, message)
            }
        } catch (e: Exception) {
            // Handle error silently for now
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // You might want to create a custom icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, notification)
        }
    }
}
