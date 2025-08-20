package com.example.tailorbook.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tailorbook.auth.AuthViewModel
import com.example.tailorbook.components.LocalProviderWrapper
import com.example.tailorbook.routes.NavHostManager
import com.example.tailorbook.routes.Navigation
import com.simon.xmaterialccp.component.MaterialCountryCodePicker
import com.simon.xmaterialccp.data.ccpDefaultColors
import com.simon.xmaterialccp.data.utils.checkPhoneNumber
import com.simon.xmaterialccp.data.utils.getDefaultLangCode
import com.simon.xmaterialccp.data.utils.getDefaultPhoneCode
import com.simon.xmaterialccp.data.utils.getLibCountries
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberScreen() {
    val context = LocalContext.current
    val navController = NavHostManager.LocalNavController.current
    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            title = {
                Text("Sign in")
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        Icons.Outlined.ArrowBackIosNew,
                        contentDescription = ""
                    )
                }
            },

            actions = {

            }

        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(6.dp),
            verticalArrangement = Arrangement.Center
        ) {

            SelectCountryWithCountryCode()
        }
    }
}


@Composable
fun SelectCountryWithCountryCode() {
    val viewModel: AuthViewModel = koinViewModel()

    val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()

    Log.i("TAG", "SelectCountryWithCountryCode: $authUiState")
    var code by rememberSaveable { mutableStateOf("") }
    val navController = NavHostManager.LocalNavController.current
    LaunchedEffect(authUiState.isSignedInSuccess) {
        if (authUiState.isSignedInSuccess) {
            navController.navigate(Navigation.Home) {
                popUpTo(Navigation.Login) { inclusive = true }
            }
        }
    }

    val context = LocalContext.current
    var phoneCode by remember { mutableStateOf(getDefaultPhoneCode(context)) }
    val phoneNumber = rememberSaveable { mutableStateOf("") }
    var defaultLang by rememberSaveable { mutableStateOf(getDefaultLangCode(context)) }
    var isValidPhone by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        if (authUiState.numberVerificationMode) {
            Text(
                text = "Verify your phone",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "We’ll send a code to confirm it’s you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            MaterialCountryCodePicker(
                pickedCountry = {
                    phoneCode = it.countryPhoneCode
                    defaultLang = it.countryCode
                },
                defaultCountry = getLibCountries().single { it.countryCode == defaultLang },
                error = !isValidPhone,
                text = phoneNumber.value,
                onValueChange = { phoneNumber.value = it },
                searchFieldPlaceHolderTextStyle = MaterialTheme.typography.bodyMedium,
                searchFieldTextStyle = MaterialTheme.typography.bodyMedium,
                phonenumbertextstyle = MaterialTheme.typography.bodyMedium,
                countrytextstyle = MaterialTheme.typography.bodyMedium,
                countrycodetextstyle = MaterialTheme.typography.bodyMedium,
                showErrorText = true,
                showCountryCodeInDIalog = true,
                showDropDownAfterFlag = true,
                textFieldShapeCornerRadiusInPercentage = 40,
                searchFieldShapeCornerRadiusInPercentage = 40,
                appbartitleStyle = MaterialTheme.typography.titleLarge,
                countryItemBgShape = RoundedCornerShape(5.dp),
                showCountryFlag = true,
                showCountryCode = true,
                isEnabled = !authUiState.isLoading,
                colors = ccpDefaultColors(
                    primaryColor = MaterialTheme.colorScheme.primary,
                    errorColor = MaterialTheme.colorScheme.error,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    surfaceColor = MaterialTheme.colorScheme.surface,
                    outlineColor = MaterialTheme.colorScheme.outline,
                    disabledOutlineColor = MaterialTheme.colorScheme.outline.copy(0.1f),
                    unfocusedOutlineColor = MaterialTheme.colorScheme.onBackground.copy(0.3f),
                    textColor = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                    cursorColor = MaterialTheme.colorScheme.primary,
                    topAppBarColor = MaterialTheme.colorScheme.surface,
                    countryItemBgColor = MaterialTheme.colorScheme.surface,
                    searchFieldBgColor = MaterialTheme.colorScheme.surface,
                    dialogNavIconColor = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                    dropDownIconTint = MaterialTheme.colorScheme.onBackground.copy(0.7f)

                )
            )

            val fullPhoneNumber = "$phoneCode${phoneNumber.value}"
            val canSubmit = checkPhoneNumber(
                phone = phoneNumber.value,
                fullPhoneNumber = fullPhoneNumber,
                countryCode = defaultLang
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    isValidPhone = canSubmit
                    if (isValidPhone) {
                        viewModel.sendVerificationCode(
                            viewModel.basePhoneAuthOptionsBuilder
                                .setActivity(context as Activity)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setPhoneNumber(fullPhoneNumber)
                                .build()
                        )
                    }
                },
                enabled = canSubmit && !authUiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp)
            ) {
                if (authUiState.isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(text = "Send code")
                }
            }
        } else if (authUiState.codeVerificationMode) {
            Text(
                text = "Enter verification code",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "We sent a 6-digit code to your number.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = code,
                onValueChange = { input ->
                    if (input.length <= 6) code = input.filter { it.isDigit() }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                label = { Text("6-digit code") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.verifyCode(code) },
                enabled = code.length == 6 && !authUiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp)
            ) {
                if (authUiState.isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(text = "Verify")
                }
            }
        }

        if (authUiState.errorMsg != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = authUiState.errorMsg ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}


@Preview
@Composable
fun PhoneNumberPreview() {
    LocalProviderWrapper {
        PhoneNumberScreen()
    }
}