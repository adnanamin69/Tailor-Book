package com.example.tailorbook.routes

import android.os.Parcelable
import kotlinx.serialization.Serializable

@Serializable
sealed class Navigation {
    @Serializable
    data object Splash : Navigation()

    @Serializable
    data object Home : Navigation()

    @Serializable
    data object Login : Navigation()

}