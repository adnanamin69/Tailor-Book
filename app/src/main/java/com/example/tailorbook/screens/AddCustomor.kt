package com.example.tailorbook.screens

import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.tailorbook.routes.NavHostManager
import com.example.tailorbook.viewmodels.UserListIntent
import com.example.tailorbook.viewmodels.UsersViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import android.content.ContentResolver
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.Color
import com.example.tailorbook.routes.NavHostManager.LocalNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomerFormPage() {
    val navController = LocalNavController.current
    val viewModel: UsersViewModel = koinViewModel()
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var pickedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBase64 by remember { mutableStateOf<String?>(null) }
    val addError by viewModel.addCustomerError.collectAsStateWithLifecycle(null)
    val addSuccess by viewModel.addCustomerSuccess.collectAsStateWithLifecycle(false)
    val isAdding by viewModel.isAddingCustomer.collectAsStateWithLifecycle(false)
    val handleIntent = NavHostManager.LocalUserSearch.current

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(addSuccess) {
        if (addSuccess) {
            snackbarHostState.showSnackbar("Customer added")
            viewModel.clearAddCustomerStatus()
            navController.navigateUp()
        }
    }

    LaunchedEffect(addError) {
        addError?.let { snackbarHostState.showSnackbar(it) }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        pickedImageUri = uri
        imageBase64 = uri?.let { encodeImageToBase64(context.contentResolver, it) }
    }

    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            title = { Text("Add Customer") }
        )
    }, snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlideImage(
                    model = pickedImageUri,
                    contentDescription = "",
                    modifier = Modifier.size(72.dp).clip(CircleShape)
                )
                OutlinedButton(onClick = { pickImageLauncher.launch("image/*") }) {
                    Text("Pick Image")
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = { if (nameError != null) Text(nameError!!, color = MaterialTheme.colorScheme.error) }
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { input ->
                    // allow digits, + and spaces
                    phone = input.filter { it.isDigit() || it == '+' || it == ' ' }
                },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(),
                isError = phoneError != null,
                supportingText = { if (phoneError != null) Text(phoneError!!, color = MaterialTheme.colorScheme.error) }
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )

            if (addError != null) {
                Text(text = addError ?: "", color = MaterialTheme.colorScheme.error)
            }

            Button(onClick = {
                nameError = if (name.isBlank()) "Name is required" else null
                phoneError = if (phone.isBlank()) "Phone is required" else null
                if (nameError == null && phoneError == null && !isAdding) {
                    handleIntent.invoke(UserListIntent.UploadImage(name, phone, address, imageBase64))
                }
            }, enabled = !isAdding, modifier = Modifier.fillMaxWidth()) {
                if (isAdding) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Save")
                }
            }
        }
    }
}




private fun encodeImageToBase64(contentResolver: ContentResolver, uri: Uri): String? {
    return try {
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val output = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
        Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
    } catch (e: Exception) {
        null
    }
}
