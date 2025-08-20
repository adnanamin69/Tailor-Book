package com.example.tailorbook.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tailorbook.viewmodels.OrdersViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(customerId: String, orderId: String) {
    val vm: OrdersViewModel = koinViewModel()
    val order by vm.selectedOrder.collectAsStateWithLifecycle()

    LaunchedEffect(orderId) { vm.fetchOrder(customerId, orderId) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Order Details") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (order == null) {
                Text("Loading...")
            } else {
                Text("Dress: ${order!!.dressType}")
                Text("Status: ${order!!.status}")
                Text("Due: ${order!!.totalDue} Paid: ${order!!.totalPaid} Balance: ${order!!.balance}")
                Text("Check-in: ${order!!.checkInDate} Delivery: ${order!!.deliveryDate}")
            }
        }
    }
}


