package com.example.tailorbook.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tailorbook.routes.NavHostManager
import com.example.tailorbook.routes.Navigation
import com.example.tailorbook.viewmodels.OrdersViewModel
import com.example.tailorbook.viewmodels.PaymentsViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(customerId: String, orderId: String) {
    val orderViewModel: OrdersViewModel = koinViewModel()
    val orders by orderViewModel.orders.collectAsStateWithLifecycle()

    val viewModel: PaymentsViewModel = koinViewModel()
    val items by viewModel.payments.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val navController = NavHostManager.LocalNavController.current
    // Calculate total payments

    var totalRemaining by remember { mutableStateOf(0.0) }
    LaunchedEffect(customerId) {
        orderViewModel.fetchOrders(customerId)
    }


    LaunchedEffect(orders) {
        totalRemaining = orders.sumOf { it.totalDue - it.totalPaid }
    }

    val totalPaid = remember(items) {
        items.sumOf { it.amount }
    }

    LaunchedEffect(orderId) {
        viewModel.fetchPayments(customerId, orderId)
    }

    // Modern color scheme
    val primaryGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF667eea),
            Color(0xFF764ba2)
        )
    )

    val cardGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFf093fb),
            Color(0xFFf5576c)
        )
    )

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Payment History",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                modifier = Modifier
                    .background(primaryGradient)
                    .statusBarsPadding(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {

                // Total Payment Summary Card
                Spacer(Modifier.size(10.dp))
                // Financial Summary
                FinancialSummaryCard(
                    totalDue = totalRemaining + totalPaid,
                    totalPaid = totalPaid,
                    balance = totalRemaining,
                )
                Spacer(Modifier.size(10.dp))


                /*  // Total Payment Summary Card
                  Card(
                      modifier = Modifier
                          .fillMaxWidth()
                          .padding(20.dp),
                      shape = RoundedCornerShape(20.dp),
                      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                      colors = CardDefaults.cardColors(containerColor = Color.White)
                  ) {
                      Box(
                          modifier = Modifier
                              .fillMaxWidth()
                              .background(cardGradient)
                              .padding(24.dp)
                      ) {
                          Column(
                              horizontalAlignment = Alignment.CenterHorizontally
                          ) {
                              Icon(
                                  Icons.Default.Payment,
                                  contentDescription = null,
                                  tint = Color.White,
                                  modifier = Modifier.size(32.dp)
                              )
                              Spacer(modifier = Modifier.height(8.dp))
                              Text(
                                  "Total Paid",
                                  fontSize = 16.sp,
                                  color = Color.White.copy(alpha = 0.9f),
                                  fontWeight = FontWeight.Medium
                              )
                              Text(
                                  NumberFormat.getCurrencyInstance(Locale.getDefault())
                                      .format(totalPaid),
                                  fontSize = 32.sp,
                                  fontWeight = FontWeight.Bold,
                                  color = Color.White
                              )
                              Text(
                                  "${items.size} payment${if (items.size != 1) "s" else ""} made",
                                  fontSize = 14.sp,
                                  color = Color.White.copy(alpha = 0.8f)
                              )
                          }
                      }
                  }*/

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF667eea),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Loading payments...",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                } else {
                    if (items.isEmpty()) {
                        // Empty state
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Payment,
                                    contentDescription = null,
                                    tint = Color.Gray.copy(alpha = 0.5f),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No payments yet",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Text(
                                    "Add your first payment below",
                                    fontSize = 14.sp,
                                    color = Color.Gray.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        // Payments List
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    "Payment History",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2D3748),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            items(items.sortedByDescending { it.date }) { payment ->
                                ModernPaymentItem(
                                    amount = payment.amount,
                                    date = payment.date
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }

                // Add Payment Section
                AddPaymentSection(
                    totalRemaining,
                    onAddPayment = { amount ->


                        viewModel.addPayment(
                            customerId,
                            orderId,
                            amount,
                            System.currentTimeMillis()
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ModernPaymentItem(amount: Double, date: Long) {
    val formattedDate = remember(date) {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(date))
    }

    val formattedTime = remember(date) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(date))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /*   // Payment Icon
               Box(
                   modifier = Modifier
                       .size(50.dp)
                       .clip(RoundedCornerShape(12.dp))
                       .background(
                           Brush.linearGradient(
                               colors = listOf(
                                   Color(0xFF4facfe),
                                   Color(0xFF00f2fe)
                               )
                           )
                       ),
                   contentAlignment = Alignment.Center
               ) {
                   Icon(
                       Icons.Default.Payment,
                       contentDescription = null,
                       tint = Color.White,
                       modifier = Modifier.size(24.dp)
                   )
               }
   */
            Spacer(modifier = Modifier.width(16.dp))

            // Payment Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    NumberFormat.getCurrencyInstance(Locale.getDefault()).format(amount),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        formattedDate,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        " • $formattedTime",
                        fontSize = 14.sp,
                        color = Color.Gray.copy(alpha = 0.7f)
                    )
                }
            }

            // Status Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF10B981).copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    "Paid",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF10B981)
                )
            }
        }
    }
}

@Composable
fun AddPaymentSection(due: Double, onAddPayment: (Double) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        "Add New Payment",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                    Text(
                        "Record a new payment transaction",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                IconButton(
                    onClick = { isExpanded = !isExpanded }
                ) {
                    Icon(
                        if (isExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Add Payment",
                        tint = Color(0xFF667eea)
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        if (it.all { ch -> ch.isDigit() || ch == '.' }) amount = it
                    },
                    label = { Text("Payment Amount") },
                    placeholder = { Text("0.00") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF667eea),
                        focusedLabelColor = Color(0xFF667eea)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Button(
                        onClick = {
                            val amountValue = amount.toDoubleOrNull()



                            if (amountValue != null && amountValue > 0) {
                                onAddPayment(amountValue)
                                amount = ""
                                isExpanded = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF667eea)
                        ),
                        enabled = amount.toDoubleOrNull()?.let { it > 0 && it <= due } == true
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Payment")
                    }
                }
            }
        }
    }
}