package com.example.tailorbook.models

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlin.collections.set

data class User(
    val userid: String = "",
    val name: String = "",
    val phone: String = "",
    val img: String = "",
    val remainingBalance: Double = 0.0,
    val nearestDelivery: Long? = null
) {
    val imageBitmap by lazy {
        val decodedBytes = Base64.decode(img, Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        decodedBitmap?.asImageBitmap()
    }
}