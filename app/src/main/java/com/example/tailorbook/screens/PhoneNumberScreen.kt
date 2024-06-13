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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
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
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberScreen() {
    val context = LocalContext.current
    val navController = NavHostManager.LocalNavController.current
    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            title = {
                Text("")
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
    val viewModel: AuthViewModel = hiltViewModel()

    val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()


    Log.i("TAG", "SelectCountryWithCountryCode: $authUiState")
    var code by remember {
        mutableStateOf("")
    }


    if (authUiState.codeVerificationMode) {
        TextField(value = code, onValueChange = {
            code = it
        })
        Button(onClick = { viewModel.verifyCode(code) }) {
            Text(text = "Virify")
        }
    }
    val navController = NavHostManager.LocalNavController.current
    if (viewModel.isSignedIn)
        navController.navigate(Navigation.Home)


    Log.i("TAG", "SelectCountryWithCountryCode: ${viewModel.isSignedIn}")

    val context = LocalContext.current
    var phoneCode by remember { mutableStateOf(getDefaultPhoneCode(context)) }
    val phoneNumber = rememberSaveable { mutableStateOf("") }
    var defaultLang by rememberSaveable { mutableStateOf(getDefaultLangCode(context)) }
    var isValidPhone by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier.padding(16.dp)
    ) {

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
            isEnabled = true,
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
        val checkPhoneNumber = checkPhoneNumber(
            phone = phoneNumber.value,
            fullPhoneNumber = fullPhoneNumber,
            countryCode = defaultLang
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = {
                isValidPhone = checkPhoneNumber


                Toast.makeText(context, fullPhoneNumber, Toast.LENGTH_SHORT).show()
                if (isValidPhone)
                    viewModel.sendVerificationCode(
                        viewModel.basePhoneAuthOptionsBuilder
                            .setActivity(context as Activity)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setPhoneNumber(fullPhoneNumber)
                            .build()
                    )


            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(
                    50.dp
                )
        ) {
            Text(text = "Phone Verify")
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