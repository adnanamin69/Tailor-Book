package com.example.tailorbook.screens


import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tailorbook.models.Order
import com.example.tailorbook.models.OrderStatus
import com.example.tailorbook.routes.NavHostManager
import com.example.tailorbook.viewmodels.OrdersViewModel
import com.example.tailorbook.viewmodels.PaymentsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OrderFormScreen2(
    customerId: String, measurementId: String?, orderId: String?, onSaved: () -> Unit = {}
) {
    val viewModel: OrdersViewModel = koinViewModel()
    val pViewModel: PaymentsViewModel = koinViewModel()

    val navController = NavHostManager.LocalNavController.current
    var dress by remember { mutableStateOf("shalwar qamees") }
    var due by remember { mutableStateOf("") }
    var paid by remember { mutableStateOf("") }

    // Delivery timestamp
    var deliveryMillis by remember { mutableStateOf<Long?>(null) }
    var deliveryText by remember { mutableStateOf("Pick Delivery Date") }

    var status by remember { mutableStateOf(OrderStatus.PENDING) }
    val context = LocalContext.current

    // Check if we're editing an existing order
    val isEditing = orderId != null
    val selectedOrder by viewModel.selectedOrder.collectAsStateWithLifecycle()

    // Fetch existing order data when editing
    LaunchedEffect(orderId) {
        if (isEditing && orderId != null) {
            viewModel.fetchOrder(customerId, orderId)
        }
    }

    // Pre-fill form fields when order data is loaded
    LaunchedEffect(selectedOrder) {
        selectedOrder?.let { order ->
            dress = order.dressType
            due = order.totalDue.toString()
            paid = order.totalPaid.toString()
            status = order.status
            deliveryMillis = order.deliveryDate
            if (order.deliveryDate > 0) {
                val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                deliveryText = sdf.format(Date(order.deliveryDate))
            }
        }
    }

    val primaryGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF667eea), Color(0xFF764ba2)
        )
    )

    val success by viewModel.saveSuccess.collectAsStateWithLifecycle(false)

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }



    LaunchedEffect(success) {
        if (success) {
            snackbarHostState.showSnackbar("✅ Order saved successfully!")
            due = ""
            paid = ""
            scope.launch(Dispatchers.Main) {
                navController.navigateUp()
            }
        }
    }



    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    if (isEditing) "Edit Order" else "New Order",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White
                    )
                }
            },
            modifier = Modifier
                .background(primaryGradient)
                .statusBarsPadding(),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
    }, snackbarHost = {
        SnackbarHost(
            hostState = snackbarHostState, snackbar = { data ->
                CustomSnackbar(data)
            })
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Delivery Date & Time
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val now = Calendar.getInstance()
                            android.app.DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val cal = Calendar.getInstance().apply {
                                        set(year, month, dayOfMonth)
                                    }
                                    deliveryMillis = cal.timeInMillis
                                    val sdf = SimpleDateFormat(
                                        "dd/MM/yy", Locale.getDefault()
                                    )
                                    deliveryText = sdf.format(cal.time)
                                },
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                            ).apply {
                                datePicker.minDate = now.timeInMillis // prevent past dates

                            }.show()
                        }
                        .padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF667eea)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        deliveryText,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = if (deliveryMillis == null) Color.Gray else Color.Black
                    )
                }
            }

            // Financial inputs
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = due,
                        onValueChange = {
                            if (it.all { ch -> ch.isDigit() || ch == '.' }) due = it
                        },
                        label = { Text("Total Due") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = paid,
                        onValueChange = {
                            if (it.all { ch -> ch.isDigit() || ch == '.' }) paid = it
                        },
                        label = { Text("Total Paid") },
                        placeholder = { Text(if (isEditing) "Current paid amount" else "Enter paid amount") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        enabled = !isEditing // Disable editing paid amount when editing existing order
                    )
                }
            }

            // Status Chips
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Order Status",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF2D3748)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OrderStatus.entries.forEach { s ->
                            val (icon, color) = when (s) {
                                OrderStatus.PENDING -> Icons.Outlined.Schedule to Color.Gray
                                OrderStatus.IN_PROGRESS -> Icons.Outlined.Build to Color(0xFFFB8C00)
                                OrderStatus.COMPLETED -> Icons.Outlined.CheckCircle to Color(
                                    0xFF4CAF50
                                )

                                OrderStatus.DELIVERED -> Icons.Outlined.DoneAll to Color(0xFFE53935)
                            }
                            FilterChip(selected = status == s, onClick = { status = s }, label = {
                                Text(
                                    s.name.replace("_", " "), fontWeight = FontWeight.Medium
                                )
                            }, leadingIcon = {
                                Icon(icon, contentDescription = s.name, tint = color)
                            })
                        }
                    }
                }
            }


            val isSaveEnabled =
                deliveryMillis != null && due.isNotEmpty() && due.toDoubleOrNull() != null

            // Save Button
            Button(
                enabled = isSaveEnabled,
                onClick = {
                    // When editing, preserve original check-in date and total paid
                    val checkInMillis = if (isEditing && selectedOrder != null) {
                        selectedOrder!!.checkInDate
                    } else {
                        System.currentTimeMillis()
                    }

                    val totalPaidAmount = if (isEditing && selectedOrder != null) {
                        selectedOrder!!.totalPaid // Keep existing paid amount when editing
                    } else {
                        paid.toDoubleOrNull() ?: 0.0 // Use entered amount for new orders
                    }

                    val order = Order(
                        customerId = customerId,
                        measurementId = measurementId ?: "",
                        dressType = dress,
                        checkInDate = checkInMillis,
                        deliveryDate = deliveryMillis ?: 0L,
                        status = status,
                        totalDue = due.toDoubleOrNull() ?: 0.0,
                        // totalPaid = totalPaidAmount
                    )

                    val currentTime = System.currentTimeMillis()

                    viewModel.addOrUpdateOrder(customerId, orderId ?: currentTime.toString(), order)

                    // Only add payment if it's a new order or if paid amount is different
                    if (!isEditing && paid.isNotEmpty()) {
                        pViewModel.addPayment(
                            customerId,
                            orderId ?: currentTime.toString(),
                            paid.toDoubleOrNull() ?: 0.0,
                            System.currentTimeMillis()
                        )
                    }

                    if (!isEditing) {
                        due = ""
                        paid = ""
                    }

                    onSaved()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667eea))
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Order", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
