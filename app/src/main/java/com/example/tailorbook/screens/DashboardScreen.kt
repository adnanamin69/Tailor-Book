package com.example.tailorbook.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tailorbook.viewmodels.DashboardViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.math.sin

// Enhanced color palette for dashboard
object DashboardColors {
    val BackgroundGradient = listOf(
        Color(0xFF667eea),
        Color(0xFF764ba2),
        Color(0xFF667eea)
    )

    val CardGradients = mapOf(
        "customers" to listOf(Color(0xFF4facfe), Color(0xFF00f2fe)),
        "pending" to listOf(Color(0xFFfa709a), Color(0xFFfee140)),
        "inprogress" to listOf(Color(0xFF43e97b), Color(0xFF38f9d7)),
        "completed" to listOf(Color(0xFF4facfe), Color(0xFF00f2fe)),
        "delivered" to listOf(Color(0xFF667eea), Color(0xFF764ba2)),
        "due" to listOf(Color(0xFFf093fb), Color(0xFFf5576c))
    )

    val SurfaceGradient = listOf(
        Color(0xFFfdfbfb),
        Color(0xFFebedee)
    )
}

data class DashboardCardData(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val gradientKey: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val vm: DashboardViewModel = koinViewModel()
    val state = vm.state.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) { vm.fetchDashboard() }

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
                    colors = DashboardColors.BackgroundGradient,
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
                CreativeDashboardTopBar()
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    state.isLoading -> {
                        EnhancedLoadingScreen()
                    }

                    state.error != null -> {
                        EnhancedErrorScreen(state.error)
                    }

                    else -> {
                        DashboardContent(state)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreativeDashboardTopBar() {
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
                        text = "Business Overview",
                        fontSize = 14.sp,
                        color = Color(0xFF667eea).copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "Dashboard",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                }

                // Animated dashboard icon

            }
        }
    }
}

@Composable
private fun EnhancedLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated loading circles
            val infiniteTransition = rememberInfiniteTransition(label = "loading")

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    val delay = index * 200
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 1.5f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                1000,
                                delayMillis = delay,
                                easing = FastOutSlowInEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale$index"
                    )

                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                            .background(
                                Color.White.copy(alpha = 0.8f),
                                CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Loading dashboard data...",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EnhancedErrorScreen(error: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFf093fb),
                                    Color(0xFFf5576c)
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Something went wrong",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = error,
                    fontSize = 14.sp,
                    color = Color(0xFF4A5568),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(state: com.example.tailorbook.viewmodels.DashboardState) {
    val cards = listOf(
        DashboardCardData(
            "Total Customers",
            state.totalCustomers.toString(),
            Icons.Default.Group,
            "customers",
            "Active customer base"
        ),
        DashboardCardData(
            "Pending Orders",
            state.pending.toString(),
            Icons.Default.HourglassTop,
            "pending",
            "Awaiting processing"
        ),
        DashboardCardData(
            "In Progress",
            state.inProgress.toString(),
            Icons.Default.Construction,
            "inprogress",
            "Currently working on"
        ),
        DashboardCardData(
            "Completed",
            state.completed.toString(),
            Icons.Default.DoneAll,
            "completed",
            "Finished orders"
        ),
        DashboardCardData(
            "Delivered",
            state.delivered.toString(),
            Icons.Default.CheckCircle,
            "delivered",
            "Successfully delivered"
        ),
        DashboardCardData(
            "Outstanding",
            "Rs. ${state.totalDueOutstanding.toInt()}",
            Icons.Default.Payments,
            "due",
            "Pending payments"
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp)
    ) {
        itemsIndexed(cards) { index, cardData ->
            AnimatedDashboardCard(
                cardData = cardData,
                index = index
            )
        }
    }
}

@Composable
private fun AnimatedDashboardCard(
    cardData: DashboardCardData,
    index: Int
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = cardData.title) {
        delay(index * 150L) // Stagger animations
        isVisible = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "card$index")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000 + index * 500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float$index"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(600)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        )
    ) {
        EnhancedDashboardCard(
            cardData = cardData,
            floatOffset = sin(floatOffset * 2 * Math.PI).toFloat() * 2f
        )
    }
}

@Composable
private fun EnhancedDashboardCard(
    cardData: DashboardCardData,
    floatOffset: Float
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pressScale"
    )

    val gradient = DashboardColors.CardGradients[cardData.gradientKey]
        ?: listOf(Color(0xFF4facfe), Color(0xFF00f2fe))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationY = floatOffset
            )
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .clickable {
                isPressed = !isPressed
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = gradient.map { it.copy(alpha = 0.9f) },
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(300f, 300f)
                    )
                )
        ) {
            // Background pattern
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = (-20).dp, y = (-20).dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 20.dp, y = (-10).dp)
                    .background(
                        Color.White.copy(alpha = 0.05f),
                        CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = cardData.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = cardData.value,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }


                Column {


                    Text(
                        text = cardData.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Text(
                        text = cardData.description,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}