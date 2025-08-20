package com.example.tailorbook.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tailorbook.viewmodels.PaymentsViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(customerId: String, orderId: String) {
    val viewModel: PaymentsViewModel = koinViewModel()
    val items by viewModel.payments.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(orderId) {
        viewModel.fetchPayments(customerId, orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payments") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items) { p ->
                        PaymentItem(amount = p.amount, date = p.date)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                var amount by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.addPayment(
                            customerId,
                            orderId,
                            amount.toDoubleOrNull() ?: 0.0,
                            System.currentTimeMillis() // use current date-time
                        )
                        amount = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Add Payment")
                }
            }
        }
    }
}

@Composable
fun PaymentItem(amount: Double, date: Long) {
    val formattedDate = remember(date) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(date))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("💰 Amount: $amount", style = MaterialTheme.typography.titleMedium)
            Text(
                "📅 Date: $formattedDate",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}



