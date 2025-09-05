package com.example.tailorbook.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tailorbook.models.Measurement
import com.example.tailorbook.models.Order
import com.example.tailorbook.models.OrderStatus
import com.example.tailorbook.routes.NavHostManager
import com.example.tailorbook.routes.NavHostManager.LocalNavController
import com.example.tailorbook.routes.Navigation
import com.example.tailorbook.utils.CompletionMessageGenerator
import com.example.tailorbook.utils.InvoiceGenerator
import com.example.tailorbook.utils.ShareUtils
import com.example.tailorbook.viewmodels.MeasurementsViewModel
import com.example.tailorbook.viewmodels.OrdersViewModel
import com.example.tailorbook.viewmodels.UsersViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

// Enhanced color palette
object ProfileColors {
    val BackgroundGradient = listOf(
        Color(0xFF667eea),
        Color(0xFF764ba2),
        Color(0xFF667eea)
    )

    val MeasurementGradients = mapOf(
        0 to listOf(Color(0xFF4facfe), Color(0xFF00f2fe)),
        1 to listOf(Color(0xFFfa709a), Color(0xFFfee140)),
        2 to listOf(Color(0xFF43e97b), Color(0xFF38f9d7)),
        3 to listOf(Color(0xFF667eea), Color(0xFF764ba2)),
        4 to listOf(Color(0xFFf093fb), Color(0xFFf5576c)),
        5 to listOf(Color(0xFF4facfe), Color(0xFF00f2fe)),
        6 to listOf(Color(0xFFffecd2), Color(0xFFfcb69f)),
        7 to listOf(Color(0xFFa8edea), Color(0xFFfed6e3))
    )

    val OrderStatusGradients = mapOf(
        OrderStatus.PENDING to listOf(Color(0xFFbdc3c7), Color(0xFF95a5a6)),
        OrderStatus.IN_PROGRESS to listOf(Color(0xFFf39c12), Color(0xFFe67e22)),
        OrderStatus.COMPLETED to listOf(Color(0xFF27ae60), Color(0xFF2ecc71)),
        OrderStatus.DELIVERED to listOf(Color(0xFF8e44ad), Color(0xFF9b59b6))
    )

    val CardGradient = listOf(
        Color(0xFFfdfbfb),
        Color(0xFFebedee)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerProfileScreen(customerId: String) {
    val measurementViewModel: MeasurementsViewModel = koinViewModel()
    val measurements by measurementViewModel.measurements.collectAsStateWithLifecycle()
    val isLoading by measurementViewModel.isLoading.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { androidx.compose.runtime.mutableIntStateOf(0) }
    var totalAmountDue by remember { mutableStateOf("") }
    LaunchedEffect(customerId) {
        measurementViewModel.fetchMeasurements(customerId)
    }

    val nav = NavHostManager.LocalNavController.current

    // Background animation
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val backgroundOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "backgroundOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = ProfileColors.BackgroundGradient,
                    start = androidx.compose.ui.geometry.Offset(
                        backgroundOffset * 1000f,
                        backgroundOffset * 1000f
                    ),
                    end = androidx.compose.ui.geometry.Offset(
                        (1 - backgroundOffset) * 1000f,
                        (1 - backgroundOffset) * 1000f
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                BeautifulProfileTopBar(totalAmountDue)
            },
            floatingActionButton = {
                AnimatedFAB(selectedTab) {
                    if (selectedTab == 0)
                        nav.navigate(Navigation.AddMeaurement(customerId))
                    else nav.navigate(Navigation.OrderForm(customerId))
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                // Beautiful Tab Row
                Spacer(modifier = Modifier.height(10.dp))
                BeautifulTabRow(selectedTab) { selectedTab = it }

                Spacer(modifier = Modifier.height(20.dp))

                // Content based on selected tab
                when (selectedTab) {
                    0 -> MeasurementsContent(measurements, isLoading)
                    1 -> OrdersContent(customerId) {
                        totalAmountDue = it
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BeautifulProfileTopBar(totalAmountDue: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.95f),
                            Color.White.copy(alpha = 0.9f)
                        )
                    ),
                    RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Customer",
                        fontSize = 14.sp,
                        color = Color(0xFF667eea).copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "Profile" + if (!totalAmountDue.isEmpty()) "($totalAmountDue)" else "",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF667eea),
                                    Color(0xFF764ba2)
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BeautifulTabRow(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf(
        "Measurements" to Icons.Default.Straighten,
        "Orders" to Icons.Default.Assignment
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(25.dp)),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            tabs.forEachIndexed { index, (title, icon) ->
                val isSelected = selectedTab == index
                val animatedWeight by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "tabWeight"
                )

                Box(
                    modifier = Modifier
                        .weight(animatedWeight)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isSelected) {
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF667eea), Color(0xFF764ba2))
                                )
                            } else {
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, Color.Transparent)
                                )
                            }
                        )
                        .clickable { onTabSelected(index) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else Color(0xFF667eea),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else Color(0xFF667eea),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedFAB(selectedTab: Int, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "fab")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fabScale"
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(16.dp, CircleShape),
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    Brush.radialGradient(
                        colors = if (selectedTab == 0) {
                            listOf(Color(0xFF4facfe), Color(0xFF00f2fe))
                        } else {
                            listOf(Color(0xFFfa709a), Color(0xFFfee140))
                        }
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (selectedTab == 0) Icons.Default.Add else Icons.Default.ShoppingCart,
                contentDescription = if (selectedTab == 0) "Add Measurement" else "New Order",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun MeasurementsContent(measurements: List<Measurement>, isLoading: Boolean) {
    when {
        isLoading -> {
            LoadingMeasurements()
        }

        measurements.isEmpty() -> {
            EmptyMeasurements()
        }

        else -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                itemsIndexed(measurements) { index, measurement ->
                    AnimatedMeasurementCard(measurement, index)
                }
            }
        }
    }
}

@Composable
private fun LoadingMeasurements() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 4.dp,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Loading measurements...",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EmptyMeasurements() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            val scale by rememberInfiniteTransition(label = "empty").animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "emptyScale"
            )

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Straighten,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "No Measurements Yet",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap the + button to add the first measurement for this customer",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun AnimatedMeasurementCard(measurement: Measurement, index: Int) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = measurement) {
        delay(index * 100L)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(500))
    ) {
        BeautifulMeasurementDashboard(measurement)
    }
}

@Composable
private fun BeautifulMeasurementDashboard(measurement: Measurement) {


    val measurementItems = listOf(
        "لمبائی" to measurement.shirt_length.toString() to Icons.Default.Height,
        "بازو" to measurement.shirt_arm to Icons.Default.PanTool,
        "تیرا" to measurement.shoulder.toString() to Icons.Default.Accessibility,
        "گلہ" to measurement.collar to Icons.Default.RadioButtonChecked,
        "چھاتی" to measurement.chest.toString() to Icons.Default.Favorite,
        "چوڑائی" to measurement.lap.toString() to Icons.Default.LinearScale,
        "دامن" to measurement.pant to Icons.Default.Straighten,
        "شلوار" to measurement.shalwar.toString() to Icons.Default.Expand,
        "پنچہ" to measurement.panch.toString() to Icons.Default.CropFree,


        "کالر" to measurement.kalar.toString() to Icons.Default.Checkroom,
        "دامن" to measurement.daman.toString() to Icons.Default.Style,
        "بازو" to measurement.bazo to Icons.Default.FitnessCenter,
        "سائیڈ جیب" to measurement.sidePoket to Icons.Default.Folder,
        "بٹن" to measurement.button to Icons.Default.RadioButtonChecked,
        "کف" to measurement.cup to Icons.Default.Watch,
        "چمک دھاگہ" to measurement.chamakDaga to Icons.Default.Star,
        "سامنے کی جیب" to measurement.getYesNo(measurement.frontPoket) to Icons.Default.FolderShared,
        "شلوار کی جیب" to measurement.getYesNo(measurement.shalwarPoket) to Icons.Default.Work,


        )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(ProfileColors.CardGradient),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Measurements",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                measurementItems.chunked(2).forEachIndexed { rowIndex, rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEachIndexed { itemIndex, (labelValue, icon) ->
                            val (label, value) = labelValue
                            val gradientIndex =
                                (rowIndex * 2 + itemIndex) % ProfileColors.MeasurementGradients.size

                            BeautifulMeasurementCard(
                                label = label,
                                value = value,
                                icon = icon,
                                gradient = ProfileColors.MeasurementGradients[gradientIndex]
                                    ?: ProfileColors.MeasurementGradients[0]!!,
                                modifier = Modifier

                                    .weight(1f)
                                    .height(110.dp)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                BeautifulMeasurementCard(
                    label = "Extra",
                    value = measurement.extra,
                    icon = Icons.Default.Menu,
                    gradient = ProfileColors.MeasurementGradients[1]
                        ?: ProfileColors.MeasurementGradients[1]!!,
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }
    }
}

@Composable
private fun BeautifulMeasurementCard(
    label: String,
    value: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(gradient),
                    RoundedCornerShape(16.dp)
                )
        ) {
            // Background pattern
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(x = (-10).dp, y = (-10).dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun OrdersContent(customerId: String, callBack: (String) -> Unit) {
    val viewModel: OrdersViewModel = koinViewModel()
    val usersViewModel: UsersViewModel = koinViewModel()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val customer by usersViewModel.selectedCustomer.collectAsStateWithLifecycle()
    var totalRemaining by remember { mutableStateOf(0.0) }

    LaunchedEffect(customerId) {
        viewModel.fetchOrders(customerId)
        usersViewModel.fetchCustomerById(customerId)
    }

    val nav = LocalNavController.current
    val context = LocalContext.current

    LaunchedEffect(orders) {
        totalRemaining = orders.sumOf { it.totalDue - it.totalPaid }
        callBack(totalRemaining.toString())
    }


    when {
        isLoading -> {
            LoadingOrders()
        }

        orders.isEmpty() -> {
            EmptyOrders()
        }

        else -> {
            LazyColumn(
                state = rememberLazyListState(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                itemsIndexed(orders.reversed(), key = { _, order -> order.id }) { index, order ->
                    //  AnimatedOrderCard(order, customerId, index)
                    BeautifulOrderCard(
                        order = order,
                        customer = customer,
                        onDetailsClick = {
                            nav.navigate(Navigation.OrderDetails(customerId, order.id))
                        },
                        onPaymentsClick = {
                            nav.navigate(Navigation.Payments(customerId, order.id))
                        },
                        onShareClick = { orderToShare, isWhatsapp ->
                            customer?.let { customerData ->
                                val invoiceText = InvoiceGenerator.generateInvoiceForSharing(
                                    customerData,
                                    orderToShare
                                )
                                if (isWhatsapp)
                                    ShareUtils.shareViaWhatsApp(
                                        context,
                                        invoiceText,
                                        customerData.phone
                                    )
                                else ShareUtils.shareViaSMS(
                                    context,
                                    invoiceText,
                                    customerData.phone
                                )
                            }
                        },
                        onStatusChange = { newStatus ->
                            val updatedOrder = order.copy(status = newStatus)
                            viewModel.addOrUpdateOrder(customerId, order.id, updatedOrder)
                        },
                        onStatusChangeWithMessage = { newStatus, orderToUpdate ->
                            val updatedOrder = orderToUpdate.copy(status = newStatus)
                            viewModel.addOrUpdateOrder(customerId, orderToUpdate.id, updatedOrder)

                            // Send completion message if status changed to COMPLETED
                            if (newStatus == OrderStatus.COMPLETED) {
                                customer?.let { customerData ->
                                    val completionMessage =
                                        CompletionMessageGenerator.generateCompletionMessageForSharing(
                                            customerData,
                                            orderToUpdate
                                        )
                                    ShareUtils.shareViaWhatsApp(
                                        context,
                                        completionMessage,
                                        customerData.phone
                                    )
                                }
                            }
                        }
                    )


                }
            }
        }
    }
}

@Composable
private fun LoadingOrders() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 4.dp,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Loading orders...",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EmptyOrders() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            val scale by rememberInfiniteTransition(label = "emptyOrders").animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "emptyOrdersScale"
            )

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Assignment,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "No Orders Yet",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create the first order for this customer using the cart button",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun AnimatedOrderCard(order: Order, customerId: String, index: Int) {
    var isVisible by remember { mutableStateOf(false) }

    /*  LaunchedEffect(key1 = order.id) {
          delay(index * 150L)
          isVisible = true
      }*/

    val nav = NavHostManager.LocalNavController.current
    val viewModel: OrdersViewModel = koinViewModel()

    /*  AnimatedVisibility(
          visible = isVisible,
          enter = slideInHorizontally(
              initialOffsetX = { -it },
              animationSpec = tween(600, easing = FastOutSlowInEasing)
          ) + fadeIn(animationSpec = tween(600))
      ) {*/

}


@Composable
private fun BeautifulOrderCard(
    order: Order,
    customer: com.example.tailorbook.models.User?,
    onDetailsClick: () -> Unit,
    onPaymentsClick: () -> Unit,
    onShareClick: (Order, Boolean) -> Unit,
    onStatusChange: (OrderStatus) -> Unit,
    onStatusChangeWithMessage: (OrderStatus, Order) -> Unit
) {
    var showStatusDialog by remember { mutableStateOf(false) }
    val statusGradient = ProfileColors.OrderStatusGradients[order.status]
        ?: ProfileColors.OrderStatusGradients[OrderStatus.PENDING]!!

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(ProfileColors.CardGradient),
                    RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Row with Share Button and Status Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Share Button (Upper Left)
                    IconButton(
                        onClick = { onShareClick(order, true) },
                        enabled = customer != null,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Color(0xFF43e97b).copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Whatsapp,
                            contentDescription = "Share Invoice",
                            tint = if (customer != null) Color(0xFF43e97b) else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }


                    // Share Button (Upper Left)
                    IconButton(
                        onClick = { onShareClick(order, false) },
                        enabled = customer != null,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Color(0xFF43e97b).copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share Invoice",
                            tint = if (customer != null) Color(0xff2791f4) else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Status Button (Clickable)
                    OutlinedButton(
                        onClick = { showStatusDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = statusGradient.first(),
                            containerColor = Color.Transparent
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(statusGradient)
                        )
                    ) {
                        val icon = when (order.status) {
                            OrderStatus.PENDING -> Icons.Outlined.Schedule
                            OrderStatus.IN_PROGRESS -> Icons.Outlined.Build
                            OrderStatus.COMPLETED -> Icons.Outlined.CheckCircle
                            OrderStatus.DELIVERED -> Icons.Outlined.DoneAll
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp, 0.dp))
                        Text(
                            text = order.status.name.replace("_", " "),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
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
                                        Spacer(modifier = Modifier.size(8.dp, 0.dp))
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

                // Dates Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Check-in Date
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Check-in",
                                    fontSize = 12.sp,
                                    color = Color(0xFF666666),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = formatDate(order.checkInDate),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3748)
                            )
                        }

                        // Delivery Date
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Assignment,
                                    contentDescription = null,
                                    tint = Color(0xFF2196F3),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Delivery",
                                    fontSize = 12.sp,
                                    color = Color(0xFF666666),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = formatDate(order.deliveryDate),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3748)
                            )
                        }
                    }
                }

                // Payment Information Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total Due and Paid Amount Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Total Due Amount
                            Column(
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CropFree,
                                        contentDescription = null,
                                        tint = if (order.totalDue - order.totalPaid > 0) Color(
                                            0xFFFF5722
                                        ) else Color(0xFF4CAF50),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Total Due",
                                        fontSize = 12.sp,
                                        color = Color(0xFF666666),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "RS ${order.totalDue - order.totalPaid}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (order.totalDue - order.totalPaid > 0) Color(
                                        0xFFFF5722
                                    ) else Color(0xFF4CAF50)
                                )
                            }

                            // Paid Amount
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Paid",
                                        fontSize = 12.sp,
                                        color = Color(0xFF666666),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "RS ${order.totalPaid}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                }

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Details Button
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onDetailsClick() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF4facfe), Color(0xFF00f2fe))
                                    ),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Assignment,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Details",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Payments Button (only show if there's outstanding balance)
                    if (order.totalDue > order.totalPaid) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onPaymentsClick() },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFFfa709a), Color(0xFFfee140))
                                        ),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Payments",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}