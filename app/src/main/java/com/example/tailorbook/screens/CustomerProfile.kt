package com.example.tailorbook.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    LaunchedEffect(customerId) { viewModel.fetchMeasurements(customerId) }

    val nav = NavHostManager.LocalNavController.current
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Customer Profile") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
    }, floatingActionButton = {
        Button(onClick = { nav.navigate(Navigation.AddMeaurement(customerId)) }) { Text("Add Measurement") }
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()

                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var selectedTab by remember { mutableStateOf(0) }
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

                            Column {
                                Text("Shirt Length: ${m.shirt_length}")
                                Text("Shirt Arm: ${m.shirt_arm}")
                                Text("Shoulder: ${m.shoulder}")
                                Text("Collar: ${m.collar}")
                                Text("Chest: ${m.chest}")
                                Text("Lap: ${m.lap}")
                                Text("Pant: ${m.pant}")
                                Text("Pancha: ${m.panch}")
                            }
                        }
                    }
                }

            } else {
                Text("Orders")
                Button(onClick = { nav.navigate(Navigation.Orders(customerId)) }) { Text("Open Orders") }
                Button(onClick = { nav.navigate(Navigation.OrderForm(customerId)) }) { Text("New Order") }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Quick add form (inline)
        }
    }
}


