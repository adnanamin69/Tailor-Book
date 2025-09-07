package com.example.tailorbook.screens


import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tailorbook.models.Order
import com.example.tailorbook.models.OrderStatus
import com.example.tailorbook.routes.NavHostManager
import com.example.tailorbook.routes.Navigation
import com.example.tailorbook.utils.CompletionMessageGenerator
import com.example.tailorbook.utils.InvoiceGenerator
import com.example.tailorbook.utils.ShareUtils
import com.example.tailorbook.viewmodels.OrdersViewModel
import com.example.tailorbook.viewmodels.PaymentsViewModel
import com.example.tailorbook.viewmodels.UsersViewModel
import org.koin.androidx.compose.koinViewModel
import com.example.tailorbook.routes.NavHostManager.LocalUsersViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(customerId: String) {
    val viewModel: OrdersViewModel = koinViewModel()
    val usersViewModel: UsersViewModel = LocalUsersViewModel.current
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val customer by usersViewModel.selectedCustomer.collectAsStateWithLifecycle()

    LaunchedEffect(customerId) {
        viewModel.fetchOrders(customerId)
        usersViewModel.fetchCustomerById(customerId)
    }

    val nav = NavHostManager.LocalNavController.current
    val context = LocalContext.current


    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders, key = {
                it.id
            }) { o ->
                OrderCard(
                    order = o,
                    cId = customerId,
                    customer = customer,
                    onDetailsClick = {
                        nav.navigate(Navigation.OrderDetails(customerId, o.id))
                    },
                    onPaymentsClick = {
                        nav.navigate(Navigation.Payments(customerId, o.id))
                    },
                    onShareClick = { order ->
                        customer?.let { customerData ->
                            val invoiceText =
                                InvoiceGenerator.generateInvoiceForSharing(customerData, order)
                            ShareUtils.shareViaWhatsApp(context, invoiceText, customerData.phone)
                        }
                    },
                    onStatusChange = { status ->
                        val updatedOrder = o.copy(status = status)
                        viewModel.addOrUpdateOrder(customerId, o.id, updatedOrder)
                    },
                    onStatusChangeWithMessage = { status, order ->
                        val updatedOrder = order.copy(status = status)
                        viewModel.addOrUpdateOrder(customerId, order.id, updatedOrder)
                        
                        // Send completion message if status changed to COMPLETED
                        if (status == OrderStatus.COMPLETED) {
                            customer?.let { customerData ->
                                val completionMessage = CompletionMessageGenerator.generateCompletionMessageForSharing(customerData, order)
                                ShareUtils.shareViaWhatsApp(context, completionMessage, customerData.phone)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    cId: String,
    customer: com.example.tailorbook.models.User?,
    onDetailsClick: () -> Unit,
    onPaymentsClick: () -> Unit,
    onShareClick: (Order) -> Unit,
    onStatusChange: (OrderStatus) -> Unit,
    onStatusChangeWithMessage: (OrderStatus, Order) -> Unit
) {
    /* val viewModel: PaymentsViewModel = koinViewModel()

     LaunchedEffect(order.id) {
         viewModel.fetchPayments(cId, order.id)
     }
 */


    var showStatusDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header Row with Share Button and Status Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Share Button (Upper Right)
                IconButton(
                    onClick = { onShareClick(order) },
                    enabled = customer != null
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share Invoice",
                        tint = if (customer != null) Color(0xFF667eea) else Color.Gray
                    )
                }

                Spacer(Modifier.weight(1f))

                // Status Button (Clickable)
                val (icon, color) = when (order.status) {
                    OrderStatus.PENDING -> Icons.Outlined.Schedule to Color.Gray
                    OrderStatus.IN_PROGRESS -> Icons.Outlined.Build to Color(0xFFFB8C00)
                    OrderStatus.COMPLETED -> Icons.Outlined.CheckCircle to Color(0xFF4CAF50)
                    OrderStatus.DELIVERED -> Icons.Outlined.DoneAll to Color(0xFFE53935)
                }

                OutlinedButton(
                    onClick = { showStatusDialog = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = color
                    )
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = order.status.name,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = order.status.name.replace("_", " "),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // Status Change Dialog
            if (showStatusDialog) {
                AlertDialog(
                    onDismissRequest = { showStatusDialog = false },
                    title = { Text("Change Order Status") },
                    text = {
                        Column {
                            Text("Select new status for this order:")
                            Spacer(modifier = Modifier.height(16.dp))
                            OrderStatus.entries.forEach { status ->
                                val (sIcon, sColor) = when (status) {
                                    OrderStatus.PENDING -> Icons.Outlined.Schedule to Color.Gray
                                    OrderStatus.IN_PROGRESS -> Icons.Outlined.Build to Color(
                                        0xFFFB8C00
                                    )

                                    OrderStatus.COMPLETED -> Icons.Outlined.CheckCircle to Color(
                                        0xFF4CAF50
                                    )

                                    OrderStatus.DELIVERED -> Icons.Outlined.DoneAll to Color(
                                        0xFFE53935
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        if (status != order.status) {
                                            onStatusChangeWithMessage(status, order)
                                        }
                                        showStatusDialog = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = sColor
                                    )
                                ) {
                                    Icon(
                                        imageVector = sIcon,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(status.name.replace("_", " "))
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showStatusDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            Spacer(Modifier.height(8.dp))

            // Dates
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Check-in", style = MaterialTheme.typography.labelSmall)
                    Text(formatDate(order.checkInDate), style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("Delivery", style = MaterialTheme.typography.labelSmall)
                    Text(
                        formatDate(order.deliveryDate),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Payment Info
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Total Due", style = MaterialTheme.typography.labelSmall)
                    Text(
                        "RS ${order.totalDue - order.totalPaid}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text("Paid", style = MaterialTheme.typography.labelSmall)
                    Text(
                        "RS ${order.totalPaid}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Actions
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(onClick = onDetailsClick) { Text("Details") }

                // Payments Button
                if (order.totalDue > order.totalPaid)
                    Button(onClick = onPaymentsClick) { Text("Payments") }
            }
        }
    }
}


@Composable
fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "-"
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OrderFormScreen(
    customerId: String,
    measurementId: String?,
    orderId: String?,
    onSaved: () -> Unit = {}
) {
    val viewModel: OrdersViewModel = koinViewModel()
    val pViewModel: PaymentsViewModel = koinViewModel()

    var dress by remember { mutableStateOf("") }
    var due by remember { mutableStateOf("") }
    var paid by remember { mutableStateOf("") }

    // Delivery timestamp
    var deliveryMillis by remember { mutableStateOf<Long?>(null) }
    var deliveryText by remember { mutableStateOf("Pick Delivery Date & Time") }

    var status by remember { mutableStateOf(OrderStatus.PENDING) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Form") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            /* OutlinedTextField(
                 value = dress,
                 onValueChange = { dress = it },
                 label = { Text("Dress Type") },
                 modifier = Modifier.fillMaxWidth(),
                 singleLine = true
             )     */

            // Delivery Date & Time Picker
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val now = Calendar.getInstance()
                        android.app.DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
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
                                    false
                                ).show()
                            },
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("📅 $deliveryText", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Amount fields
            OutlinedTextField(
                value = due,
                onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) due = it },
                label = { Text("Total Due") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = paid,
                onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) paid = it },
                label = { Text("Total Paid") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            // Status selection as Chips/Segmented Buttons
            Text("Order Status", style = MaterialTheme.typography.titleMedium)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OrderStatus.entries.forEach { s ->
                    val (icon, color) = when (s) {
                        OrderStatus.PENDING -> Icons.Outlined.Schedule to Color.Gray
                        OrderStatus.IN_PROGRESS -> Icons.Outlined.Build to Color(0xFFFB8C00)
                        OrderStatus.COMPLETED -> Icons.Outlined.CheckCircle to Color(0xFF4CAF50)
                        OrderStatus.DELIVERED -> Icons.Outlined.DoneAll to Color(0xFFE53935)
                    }


                    FilterChip(
                        selected = status == s,
                        onClick = { status = s },
                        label = { Text(s.name) },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = s.name,
                                tint = color
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val checkInMillis = System.currentTimeMillis()
                    val order = Order(
                        customerId = customerId,
                        measurementId = measurementId ?: "",
                        dressType = dress,
                        checkInDate = checkInMillis,
                        deliveryDate = deliveryMillis ?: 0L,
                        status = status,
                        totalDue = due.toDoubleOrNull() ?: 0.0,
                        totalPaid = 0.0
                    )

                    val currentTime = System.currentTimeMillis()

                    viewModel.addOrUpdateOrder(customerId, orderId ?: currentTime.toString(), order)
                    if (paid.isNotEmpty())
                        pViewModel.addPayment(
                            customerId,
                            orderId ?: currentTime.toString(),
                            paid.toDoubleOrNull() ?: 0.0,
                            System.currentTimeMillis() // use current date-time
                        )

                    onSaved()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("💾 Save Order", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}



