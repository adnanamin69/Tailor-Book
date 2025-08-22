package com.example.tailorbook.screens

import android.util.Log
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tailorbook.models.Measurement
import com.example.tailorbook.routes.NavHostManager.LocalNavController
import com.example.tailorbook.viewmodels.MeasurementsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.collections.chunked

private const val TAG = "AddMeaurement"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMeasurementScreen(customerId: String) {
    val viewModel: MeasurementsViewModel = koinViewModel()
    val list by viewModel.measurements.collectAsStateWithLifecycle()
    val saved by viewModel.addSuccess.collectAsStateWithLifecycle(false)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    LaunchedEffect(customerId) { viewModel.fetchMeasurements(customerId) }
    LaunchedEffect(saved) {

        Log.i("AddMeaurement", "AddMeasurementScreen: $saved")
        if (saved) {
            snackbarHostState.showSnackbar("✅ Measurements added successfully!")
            scope.launch(Dispatchers.Main) {
                navController.navigateUp()
            }
        }
    }

    val context = LocalContext.current

    var shirtLength by remember { mutableStateOf("") }
    var shirtArm by remember { mutableStateOf("") }
    var shoulder by remember { mutableStateOf("") }
    var collar by remember { mutableStateOf("") }
    var chest by remember { mutableStateOf("") }
    var lap by remember { mutableStateOf("") }
    var pant by remember { mutableStateOf("") }
    var shalwar by remember { mutableStateOf("") }

    var panch by remember { mutableStateOf("") }
    var extra by remember { mutableStateOf("") }

    LaunchedEffect(list) {
        list.firstOrNull()?.let { m ->
            shirtLength = m.shirt_length
            shirtArm = m.shirt_arm
            shoulder = m.shoulder
            collar = m.collar
            chest = m.chest
            lap = m.lap
            pant = m.pant
            panch = m.panch
            shalwar = m.shalwar
            extra = m.extra
        }
    }

    // Background animation
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val backgroundOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        ), label = "backgroundOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = ProfileColors.BackgroundGradient,
                    start = androidx.compose.ui.geometry.Offset(
                        backgroundOffset * 1000f, backgroundOffset * 1000f
                    ),
                    end = androidx.compose.ui.geometry.Offset(
                        (1 - backgroundOffset) * 1000f, (1 - backgroundOffset) * 1000f
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent, topBar = {
                BeautifulMeasurementTopBar(
                    onBackClick = { navController.navigateUp() })
            },

            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { data ->
                        CustomSnackbar(data)
                    }
                )
            }


        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Define a proper structure
                data class MeasurementField(
                    val label: String,
                    val value: String,
                    val icon: ImageVector,
                    val onValueChange: (String) -> Unit
                )

// Measurement Fields
                val measurementFields = listOf(
                    MeasurementField(
                        "لمبائی",
                        shirtLength,
                        Icons.Default.Height
                    ) { shirtLength = it },
                    MeasurementField("بازو", shirtArm, Icons.Default.PanTool) {
                        shirtArm = it
                    },
                    MeasurementField("تیرا", shoulder, Icons.Default.Accessibility) {
                        shoulder = it
                    },
                    MeasurementField("گلہ", collar, Icons.Default.RadioButtonChecked) {
                        collar = it
                    },
                    MeasurementField("چھاتی", chest, Icons.Default.Favorite) { chest = it },
                    MeasurementField("چوڑائی", lap, Icons.Default.LinearScale) { lap = it },
                    MeasurementField("دامن", pant, Icons.Default.Straighten) { pant = it },
                    MeasurementField("شلوار", shalwar, Icons.Default.Expand) { shalwar = it },
                    MeasurementField("پنچہ", panch, Icons.Default.CropFree) { panch = it }
                )


                measurementFields.chunked(2).forEachIndexed { rowIndex, rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEachIndexed { index, (label, value, icon, onValueChange) ->
                            val gradientIndex =
                                index + rowIndex % ProfileColors.MeasurementGradients.size
                            val gradient = ProfileColors.MeasurementGradients[gradientIndex]
                                ?: ProfileColors.MeasurementGradients[0]!!

                            BeautifulMeasurementTextField(
                                modifier = Modifier.weight(1f),
                                value = value,
                                onValueChange = onValueChange,
                                label = label,
                                icon = icon,
                                gradient = gradient,
                                animationDelay = index * 100L
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                BeautifulMeasurementTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = extra,
                    onValueChange = {
                        extra = it
                    },
                    label = "Extra",
                    icon = Icons.Default.Dehaze,
                    gradient = ProfileColors.MeasurementGradients[2]!!,
                    animationDelay = 8 * 100L,
                    keyboardOptions = KeyboardOptions.Default
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Save Button
                BeautifulSaveButton(
                    onClick = {
                        viewModel.addMeasurement(
                            customerId, Measurement(
                                customerId = customerId,
                                shirt_length = shirtLength,
                                shirt_arm = shirtArm,
                                shoulder = shoulder,
                                collar = collar,
                                chest = chest,
                                lap = lap,
                                pant = pant,
                                panch = panch,
                                shalwar = shalwar,
                                extra = extra
                            )
                        )
                    })

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BeautifulMeasurementTopBar(onBackClick: () -> Unit) {
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
                            Color.White.copy(alpha = 0.95f), Color.White.copy(alpha = 0.9f)
                        )
                    ), RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onBackClick, modifier = Modifier
                            .size(44.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF667eea), Color(0xFF764ba2)
                                    )
                                ), CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Outlined.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Add New",
                            fontSize = 14.sp,
                            color = Color(0xFF667eea).copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Measurements",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3748)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4facfe), Color(0xFF00f2fe)
                                )
                            ), CircleShape
                        ), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Straighten,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BeautifulMeasurementTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    gradient: List<Color>,
    animationDelay: Long,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(animationDelay)
        isVisible = true
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible, enter = slideInHorizontally(
            initialOffsetX = { it }, animationSpec = tween(500, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(500))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.95f), Color.White.copy(alpha = 0.85f)
                            )
                        ), RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon with gradient background
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                Brush.radialGradient(gradient), CircleShape
                            ), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Text Field
                    TextField(
                        value = value,
                        onValueChange = onValueChange,
                        label = {
                            Text(
                                text = label,
                                fontSize = 14.sp,
                                color = Color(0xFF667eea).copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            )
                        },
                        keyboardOptions = keyboardOptions,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color(0xFF2D3748),
                            unfocusedTextColor = Color(0xFF2D3748)
                        ),
                        singleLine = true
                    )
                }
            }
        }
    }
}

@Composable
private fun BeautifulSaveButton(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF667eea), Color(0xFF764ba2)
                        )
                    ), RoundedCornerShape(20.dp)
                )
                .padding(18.dp), contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Save Measurement",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}