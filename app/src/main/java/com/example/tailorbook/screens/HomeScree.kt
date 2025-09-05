package com.example.tailorbook.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.tailorbook.components.LocalProviderWrapper
import com.example.tailorbook.models.User
import com.example.tailorbook.routes.NavHostManager
import com.example.tailorbook.routes.NavHostManager.LocalNavController
import com.example.tailorbook.routes.Navigation
import com.example.tailorbook.services.DailyReminderScheduler
import com.example.tailorbook.services.NotificationService
import com.example.tailorbook.viewmodels.UserListIntent
import com.example.tailorbook.viewmodels.UserListState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// Custom color scheme
object TailorColors {
    val PrimaryGradient = listOf(
        Color(0xFF667eea),
        Color(0xFF764ba2)
    )
    val SecondaryGradient = listOf(
        Color(0xFFf093fb),
        Color(0xFFf5576c)
    )
    val CardGradient = listOf(
        Color(0xFFffecd2),
        Color(0xFFfcb69f)
    )
    val BackgroundGradient = listOf(
        Color(0xFFfdfbfb),
        Color(0xFFebedee)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val navController = LocalNavController.current

    val state = NavHostManager.LocalMainViewModelState.current.collectAsState().value
    val handleIntent = NavHostManager.LocalUserSearch.current
    
    // Request notification permission for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Schedule daily reminder after permission is granted
            DailyReminderScheduler.scheduleDailyReminder(context)
        }
    }
    
    // Request notification permission and schedule daily reminder
    LaunchedEffect(Unit) {
        // Create notification channel
        NotificationService.createNotificationChannel(context)
        
        // Check if we need to request notification permission (Android 13+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context, 
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            
            if (hasPermission) {
                // Schedule daily reminder if permission already granted
                DailyReminderScheduler.scheduleDailyReminder(context)
            } else {
                // Request permission
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // For Android 12 and below, no permission needed
            DailyReminderScheduler.scheduleDailyReminder(context)
        }
    }

    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = TailorColors.BackgroundGradient,
                    start = androidx.compose.ui.geometry.Offset(
                        animatedOffset * 1000f,
                        animatedOffset * 1000f
                    ),
                    end = androidx.compose.ui.geometry.Offset(
                        (1 - animatedOffset) * 1000f,
                        (1 - animatedOffset) * 1000f
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                AnimatedFloatingActionButton {
                    navController.navigate(Navigation.AddUSer)
                }
            },
            topBar = {
                CreativeTopBar(state, handleIntent, navController)
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (state) {
                    is UserListState.Loading -> LoadingScreen()
                    is UserListState.Success -> SuccessContent(state, navController)
                    is UserListState.Error -> ErrorScreen(state.message)
                }
            }
        }
    }
}

@Composable
private fun AnimatedFloatingActionButton(onClick: () -> Unit) {
    val scale by rememberInfiniteTransition(label = "fab").animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(16.dp, CircleShape),
        containerColor = Color(0xFF667eea),
        contentColor = Color.White
    ) {
        Icon(
            Icons.Filled.Add,
            contentDescription = "Add Customer",
            modifier = Modifier.size(28.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreativeTopBar(
    state: UserListState,
    handleIntent: (UserListIntent) -> Unit,
    navController: androidx.navigation.NavController
) {
    Column {
        // Main top bar with gradient background
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(TailorColors.PrimaryGradient),
                        RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Welcome Back!",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light
                        )
                        val titleText = if (state is UserListState.Success) {
                            "${state.users.size} Customers"
                        } else "Your Customers"

                        Text(
                            text = titleText,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }


                    val scale by rememberInfiniteTransition(label = "icon").animateFloat(
                        initialValue = 1f,
                        targetValue = 1.1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "iconScale"
                    )

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                            .clip(CircleShape)
                            .clickable {
                                navController.navigate(Navigation.Dashboard)
                            }
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xffdf3939),
                                        Color(0xffb87070)
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Dashboard,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    /*
                       Button(
                           onClick = { navController.navigate(Navigation.Dashboard) },
                           colors = ButtonDefaults.buttonColors(
                               containerColor = Color.White.copy(alpha = 0.2f),
                               contentColor = Color.White
                           ),
                           shape = RoundedCornerShape(20.dp),
                           modifier = Modifier.shadow(4.dp, RoundedCornerShape(20.dp))
                       ) {
                           Icon(
                               Icons.Default.Dashboard,
                               contentDescription = null,
                               modifier = Modifier.size(18.dp)
                           )
                           Spacer(modifier = Modifier.width(8.dp))
                           Text("Dashboard", fontWeight = FontWeight.SemiBold)
                       }*/
                }
            }
        }

        // Search bar with animation
        if (state is UserListState.Success) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(600))
            ) {
                CreativeSearchBar(
                    query = state.searchQuery,
                    onQueryChanged = { handleIntent(UserListIntent.SearchUsers(it)) }
                )
            }
        }
    }
}

@Composable
private fun CreativeSearchBar(query: String, onQueryChanged: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .shadow(6.dp, RoundedCornerShape(25.dp)),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF667eea),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = onQueryChanged,
                placeholder = {
                    Text(
                        "Search customers...",
                        color = Color.Gray.copy(alpha = 0.7f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                singleLine = true
            )
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "loading")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing)
                ),
                label = "rotation"
            )

            CircularProgressIndicator(
                modifier = Modifier
                    .size(60.dp)
                    .graphicsLayer(rotationZ = rotation),
                color = Color(0xFF667eea),
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Loading customers...",
                color = Color(0xFF667eea),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SuccessContent(
    state: UserListState.Success,
    navController: androidx.navigation.NavController
) {
    if (state.users.isEmpty()) {
        EmptyState()
    } else {
        val summariesCache = remember { mutableStateMapOf<String, Pair<Double, Long?>>() }
        val sortedUsers = remember(state.users, summariesCache.toMap()) {
            state.users.sortedWith(
                compareBy(
                    { user -> (summariesCache[user.userid]?.second == null) },
                    { user -> summariesCache[user.userid]?.second ?: Long.MAX_VALUE }
                )
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
        ) {
            items(sortedUsers.size, key = {
                sortedUsers[it].userid
            }) { index ->
                val user = sortedUsers[index]


                CreativeUserListItem(user, summariesCache) {
                    navController.navigate(Navigation.CustomerProfile(user.userid))

                }
                /*
                                AnimatedListItem(
                                    user = user,
                                    index = index,
                                    onClick = {
                                        navController.navigate(Navigation.CustomerProfile(user.userid))
                                    }
                                )*/
            }

            // Add some bottom spacing for FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Animated empty state icon
            val scale by rememberInfiniteTransition(label = "empty").animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .background(
                        Brush.radialGradient(TailorColors.SecondaryGradient),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "No Customers Yet",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Start building your customer base by adding your first customer!",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFffebee))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Oops! Something went wrong",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFc62828)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AnimatedListItem(
    user: User,
    index: Int,
    onClick: () -> Unit
) {

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun CreativeUserListItem(
    user: User,
    summariesCache: MutableMap<String, Pair<Double, Long?>>, // userid -> (remainingBalance, nearestDelivery)
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    var remainingBalance by remember { mutableStateOf<Double?>(summariesCache[user.userid]?.first) }
    var nextDeliveryDate by remember { mutableStateOf<Long?>(summariesCache[user.userid]?.second) }

    LaunchedEffect(user.userid) {
        if (summariesCache.containsKey(user.userid)) return@LaunchedEffect
        try {
            val uid = FirebaseAuth.getInstance().uid ?: return@LaunchedEffect
            val snapshot = FirebaseFirestore.getInstance()
                .collection("users").document(uid)
                .collection("customers").document(user.userid)
                .collection("orders")
                .get().await()
            var total = 0.0
            var nearest: Long? = null
            snapshot.documents.forEach { d ->
                val due = d.getDouble("totalDue") ?: 0.0
                val paid = d.getDouble("totalPaid") ?: 0.0
                total += (due - paid).coerceAtLeast(0.0)

                val status = d.getString("status") ?: "PENDING"
                val delivery = d.getLong("deliveryDate") ?: 0L
                val isActive = status == "PENDING" || status == "IN_PROGRESS"
                if (isActive && delivery > 0L) {
                    val now = System.currentTimeMillis()
                    if (delivery >= now) {
                        nearest = if (nearest == null) delivery else minOf(nearest!!, delivery)
                    }
                }
            }
            summariesCache[user.userid] = total to nearest
            remainingBalance = total
            nextDeliveryDate = nearest
        } catch (_: Exception) {
            summariesCache.remove(user.userid)
            remainingBalance = null
            nextDeliveryDate = null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clickable {
                onClick()
            }
            .zIndex(if (isPressed) 1f else 0f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = TailorColors.CardGradient,
                        startX = 0f,
                        endX = 500f
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar with gradient border
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(
                            Brush.linearGradient(TailorColors.PrimaryGradient),
                            CircleShape
                        )
                        .padding(3.dp)
                ) {
                    val isLikelyBase64 = user.img.isNotBlank() && !user.img.startsWith("http", true)

                    if (isLikelyBase64) {

                        if (user.imageBitmap != null) {
                            Image(
                                bitmap = user.imageBitmap!!,
                                contentDescription = "",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            DefaultAvatar(user.name)
                        }
                    } else if (user.img.isNotBlank()) {
                        GlideImage(
                            model = user.img,
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        DefaultAvatar(user.name)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // User Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = user.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = user.phone,
                        fontSize = 14.sp,
                        color = Color(0xFF4A5568),
                        fontWeight = FontWeight.Medium
                    )

                    // Nearest delivery date for active orders
                    if (nextDeliveryDate != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        Text(
                            text = "Next delivery: ${sdf.format(Date(nextDeliveryDate!!))}",
                            fontSize = 12.sp,
                            color = Color(0xFF667eea),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }


            }


            // Remaining Balance indicator
            val balance = remainingBalance ?: 0.0
            val balanceText = if (balance > 0.0) "RS ${"%.0f".format(balance)}" else null
            val balanceColor = when {
                remainingBalance == null -> Color(0xFF6B7280)
                balance > 0.0 -> Color(0xFFEF4444)
                else -> Color(0xFF10B981)
            }
            if (balanceText != null) {
                Text(
                    text = balanceText,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    color = balanceColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DefaultAvatar(name: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(TailorColors.SecondaryGradient),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(2).uppercase(),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun HomePreview() {
    LocalProviderWrapper {
        HomeScreen()
    }
}