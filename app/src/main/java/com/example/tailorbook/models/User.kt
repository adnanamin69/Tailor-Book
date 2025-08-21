package com.example.tailorbook.models

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap

data class User(
    val userid: String = "",
    val name: String = "",
    val phone: String = "",
    val img: String = ""
) {

    val imageBitmap by lazy {
        val decodedBytes = Base64.decode(img, Base64.DEFAULT)

        if (decodedBytes != null) {
            val decodedByte1 = BitmapFactory.decodeByteArray(
                decodedBytes,
                0,
                decodedBytes.size
            )

            decodedByte1.asImageBitmap()
        } else null
    }

}