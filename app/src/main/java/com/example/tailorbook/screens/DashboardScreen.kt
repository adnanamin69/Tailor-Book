package com.example.tailorbook.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tailorbook.viewmodels.DashboardViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val vm: DashboardViewModel = koinViewModel()
    val state = vm.state.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) { vm.fetchDashboard() }

    Scaffold(topBar = { TopAppBar(title = { Text("Dashboard") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.isLoading) {
                Text("Loading...")
            } else if (state.error != null) {
                Text("Error: ${state.error}")
            } else {
                Text("Total Customers: ${state.totalCustomers}")
                Text("Pending Orders: ${state.pending}")
                Text("In Progress: ${state.inProgress}")
                Text("Completed: ${state.completed}")
                Text("Delivered: ${state.delivered}")
                Text("Total Due Outstanding: ${state.totalDueOutstanding}")
            }
        }
    }
}


