package com.example.tailorbook.screens

import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.tailorbook.routes.NavHostManager
import com.example.tailorbook.viewmodels.UsersViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun EditCustomerScreen(customerId: String, initialName: String, initialPhone: String, initialImage: String?) {
    val vm: UsersViewModel = koinViewModel()
    val nav = NavHostManager.LocalNavController.current

    val name = remember { mutableStateOf(initialName) }
    val phone = remember { mutableStateOf(initialPhone) }
    val imageBase64 = remember { mutableStateOf(initialImage ?: "") }
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val pick = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri.value = uri
        // leave base64 conversion to future improvement; keep previous base64 if not picking
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Edit Customer") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            GlideImage(model = imageUri.value, contentDescription = null, modifier = Modifier.size(72.dp).clip(CircleShape))
            Button(onClick = { pick.launch("image/*") }) { Text("Change Image") }
            OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phone.value, onValueChange = { phone.value = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                vm.updateCustomer(customerId, name.value.trim(), phone.value.trim(), imageBase64.value)
                nav.navigateUp()
            }, modifier = Modifier.fillMaxWidth()) { Text("Save") }
        }
    }
}


