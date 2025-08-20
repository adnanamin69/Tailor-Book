package com.example.tailorbook.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tailorbook.models.Order
import com.example.tailorbook.models.OrderStatus
import com.example.tailorbook.routes.NavHostManager
import com.example.tailorbook.routes.Navigation
import com.example.tailorbook.viewmodels.OrdersViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(customerId: String) {
    val viewModel: OrdersViewModel = koinViewModel()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(customerId) { viewModel.fetchOrders(customerId) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Orders") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
    }) { padding ->
        val nav = NavHostManager.LocalNavController.current
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { o ->
                Text(
                    "${o.dressType} - ${o.status} - Due: ${o.totalDue} Paid: ${o.totalPaid}",
                )
                Button(onClick = {
                    nav.navigate(
                        Navigation.OrderDetails(
                            customerId,
                            o.id
                        )
                    )
                }) { Text("Details") }
                Button(onClick = {
                    nav.navigate(
                        Navigation.Payments(
                            customerId,
                            o.id
                        )
                    )
                }) { Text("Payments") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFormScreen(
    customerId: String,
    measurementId: String?,
    orderId: String?,
    onSaved: () -> Unit = {}
) {
    val viewModel: OrdersViewModel = koinViewModel()

    var dress by remember { mutableStateOf("") }
    var due by remember { mutableStateOf("") }
    var paid by remember { mutableStateOf("") }

    // Delivery timestamp
    var deliveryMillis by remember { mutableStateOf<Long?>(null) }
    var deliveryText by remember { mutableStateOf("Pick Delivery date") }

    var statusExpanded by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf(OrderStatus.PENDING) }

    val context = LocalContext.current

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Order Form") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                value = dress,
                onValueChange = { dress = it },
                label = { Text("Dress Type") },
                modifier = Modifier.fillMaxWidth()
            )

            // Delivery Date & Time Picker
            Text(
                text = deliveryText,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Open Date Picker first
                        val now = Calendar.getInstance()
                        android.app.DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                // After picking date → open Time Picker
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val cal = Calendar.getInstance().apply {
                                            set(year, month, dayOfMonth, hour, minute, 0)
                                        }
                                        deliveryMillis = cal.timeInMillis
                                        val sdf =
                                            SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
                                        deliveryText = sdf.format(cal.time)
                                    },
                                    now.get(Calendar.HOUR_OF_DAY),
                                    now.get(Calendar.MINUTE),
                                    true
                                ).show()
                            },
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
            )

            TextField(
                value = due,
                onValueChange = { due = it },
                label = { Text("Total Due") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = paid,
                onValueChange = { paid = it },
                label = { Text("Total Paid") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = { statusExpanded = true }) { Text("Status: ${status.name}") }
            DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                OrderStatus.entries.forEach { s ->
                    DropdownMenuItem(
                        text = { Text(s.name) },
                        onClick = { status = s; statusExpanded = false })
                }
            }

            Button(
                onClick = {
                    val checkInMillis = System.currentTimeMillis() // Auto set current date/time
                    val order = Order(
                        customerId = customerId,
                        measurementId = measurementId ?: "",
                        dressType = dress,
                        checkInDate = checkInMillis,
                        deliveryDate = deliveryMillis ?: 0L,
                        status = status,
                        totalDue = due.toDoubleOrNull() ?: 0.0,
                        totalPaid = paid.toDoubleOrNull() ?: 0.0
                    )
                    viewModel.addOrUpdateOrder(customerId, orderId, order)
                    onSaved()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
        }
    }
}


