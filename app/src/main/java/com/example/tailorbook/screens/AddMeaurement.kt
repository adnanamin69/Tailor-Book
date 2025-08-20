package com.example.tailorbook.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tailorbook.components.LocalProviderWrapper
import com.example.tailorbook.models.Measurement
import com.example.tailorbook.routes.NavHostManager.LocalNavController
import com.example.tailorbook.viewmodels.MeasurementsViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMeasurementScreen(customerId: String) {
    val viewModel: MeasurementsViewModel = koinViewModel()
    val list by viewModel.measurements.collectAsStateWithLifecycle()
    LaunchedEffect(customerId) { viewModel.fetchMeasurements(customerId) }

    val context = LocalContext.current
    val navController = LocalNavController.current


    var shirtLength by remember { mutableStateOf("") }
    var shirtArm by remember { mutableStateOf("") }
    var shoulder by remember { mutableStateOf("") }
    var collar by remember { mutableStateOf("") }
    var chest by remember { mutableStateOf("") }
    var lap by remember { mutableStateOf("") }
    var pant by remember { mutableStateOf("") }
    var panch by remember { mutableStateOf("") }

    LaunchedEffect(list) {
        list.firstOrNull()?.let { m ->
            shirtLength = m.shirt_length
            shirtArm = m.shirt_arm
            shoulder = m.shoulder
            collar = m.collar
            chest = m.chest
            lap = m.lap
            pant = m.pant
            panch = m.panch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = "")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            TextField(
                value = shirtLength,
                onValueChange = { shirtLength = it },
                label = { Text("Shirt length") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),

                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = shirtArm,
                onValueChange = { shirtArm = it },
                label = { Text("Shirt arm") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),

                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = shoulder,
                onValueChange = { shoulder = it },
                label = { Text("Shoulder") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),

                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = collar,
                onValueChange = { collar = it },
                label = { Text("Collar") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),

                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = chest,
                onValueChange = { chest = it },
                label = { Text("Chest") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),

                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = lap,
                onValueChange = { lap = it },
                label = { Text("Lap") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),

                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = pant,
                onValueChange = { pant = it },
                label = { Text("Pant") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),

                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = panch,
                onValueChange = { panch = it },
                label = { Text("Pancha") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),

                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    viewModel.addMeasurement(
                        customerId,
                        Measurement(
                            customerId = customerId,
                            shirt_length = shirtLength,
                            shirt_arm = shirtArm,
                            shoulder = shoulder,
                            collar = collar,
                            chest = chest,
                            lap = lap,
                            pant = pant,
                            panch = panch
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Measurement")
            }
        }
    }
}


@Preview
@Composable
fun AddMeasurementPreview() {
    LocalProviderWrapper {
        AddMeasurementScreen("1")
    }
}