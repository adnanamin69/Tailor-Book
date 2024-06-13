package com.example.tailorbook.auth

data class AuthUiState(
    val numberVerificationMode: Boolean = true,
    val codeVerificationMode: Boolean = false,
    val errorMsg: String? = null,
    val isLoading: Boolean = false,
    val isSignedInSuccess: Boolean = false
)