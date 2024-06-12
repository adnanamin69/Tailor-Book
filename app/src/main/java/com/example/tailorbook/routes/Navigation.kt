package com.example.tailorbook.routes

sealed class Navigation {
    data object Splash : Navigation()
    data object Home : Navigation()

}