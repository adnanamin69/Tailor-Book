package com.example.tailorbook.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tailorbook.viewmodels.PaymentsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(customerId: String, orderId: String) {
    val viewModel: PaymentsViewModel = koinViewModel()
    val items by viewModel.payments.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(orderId) { viewModel.fetchPayments(customerId, orderId) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Payments") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
    }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { p ->
                    Text("Amount: ${p.amount} at ${p.date}")
                }
            }

            var amount by remember { mutableStateOf("") }
            var date by remember { mutableStateOf("") }
            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (epoch)") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = {
                viewModel.addPayment(
                    customerId,
                    orderId,
                    amount.toDoubleOrNull() ?: 0.0,
                    date.toLongOrNull() ?: 0L
                )
            }, modifier = Modifier.fillMaxWidth()) { Text("Add Payment") }
        }
    }
}


