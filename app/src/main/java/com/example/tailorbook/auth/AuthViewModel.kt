package com.example.tailorbook.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthOptions
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AuthViewModel(
    private val phoneAuthHandler: PhoneAuthHandler
) : ViewModel() {
    val authUiState = phoneAuthHandler.verificationState.map {
        AuthUiState(
            numberVerificationMode = (it is PhoneAuthState.Idle || it is PhoneAuthState.InvalidNumber || it is PhoneAuthState.SendingCode),
            codeVerificationMode = (it is PhoneAuthState.VerificationCodeSent || it is PhoneAuthState.InvalidCode || it is PhoneAuthState.VerificationCodeSent),
            errorMsg = when (it) {
                is PhoneAuthState.InvalidCode -> it.exception.localizedMessage
                is PhoneAuthState.InvalidNumber -> it.exception.localizedMessage
                else -> null
            },
            isLoading = it is PhoneAuthState.SendingCode || it is PhoneAuthState.VerificationInProgress,
            isSignedInSuccess = it is PhoneAuthState.SignedInSuccess
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        AuthUiState()
    )
    val isSignedIn: Boolean
        get() = phoneAuthHandler.isUserSignedIn
    val basePhoneAuthOptionsBuilder: PhoneAuthOptions.Builder
        get() = phoneAuthHandler.basePhoneAuthOptionsBuilder

    fun sendVerificationCode(options: PhoneAuthOptions) =
        phoneAuthHandler.sendVerificationCode(options)

    fun verifyCode(code: String) = phoneAuthHandler.verifyCode(code)
}