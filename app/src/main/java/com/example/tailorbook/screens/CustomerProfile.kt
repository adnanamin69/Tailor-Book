package com.example.tailorbook.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tailorbook.models.Measurement
import com.example.tailorbook.routes.NavHostManager
import com.example.tailorbook.routes.Navigation
import com.example.tailorbook.viewmodels.MeasurementsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerProfileScreen(customerId: String) {
    val viewModel: MeasurementsViewModel = koinViewModel()
    val list by viewModel.measurements.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(customerId) { viewModel.fetchMeasurements(customerId) }

    val nav = NavHostManager.LocalNavController.current
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Customer Profile") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
    }, floatingActionButton = {
        Button(onClick = {
            if (selectedTab == 0)
                nav.navigate(Navigation.AddMeaurement(customerId))
            else nav.navigate(Navigation.OrderForm(customerId))


        }) {
            if (selectedTab == 0)
                Text("Add Measurement")
            else Text("New Order")
        }
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()

                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val tabs = listOf("Measurements", "Orders")
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) })
                }
            }

            if (selectedTab == 0) {
                Text("Measurements")
                if (list.isEmpty() && !isLoading) {
                    Text("No measurements yet")
                } else {
                    LazyColumn {
                        items(list) { m ->

                            MeasurementDashboard(m)
                        }
                    }
                }

            } else {
                Text("Orders")
                OrdersScreen(customerId)

            }

            Spacer(modifier = Modifier.weight(1f))

            // Quick add form (inline)
        }
    }
}


@Composable
fun MeasurementDashboard(m: Measurement) {
    val items = listOf(
        "Shirt Length" to m.shirt_length.toString(),
        "Shirt Arm" to m.shirt_arm.toString(),
        "Shoulder" to m.shoulder.toString(),
        "Collar" to m.collar.toString(),
        "Chest" to m.chest.toString(),
        "Lap" to m.lap.toString(),
        "Pant" to m.pant.toString(),
        "Pancha" to m.panch.toString()
    )

    // Show in grid with 2 per row
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { (label, value) ->
                    MeasurementCard(label, value, modifier = Modifier.weight(1f))
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f)) // keep grid aligned
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun MeasurementCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Check, // 👉 replace with relevant icons
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

