package com.example.tailorbook.screens.newscreens


import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tailorbook.models.Measurement
import com.example.tailorbook.routes.NavHostManager.LocalNavController
import com.example.tailorbook.screens.CustomSnackbar
import com.example.tailorbook.screens.ProfileColors
import com.example.tailorbook.viewmodels.MeasurementsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.collections.chunked

private const val TAG = "AddMeaurement"

data class StyleField(
    val label: String,
    val value: String,
    val options: List<String>,
    val onSelected: (String) -> Unit
)

data class BasicField(
    val label: String,
    val value: String,
    val onValueChange: (String) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMeasurementScreen1(customerId: String) {
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

    // Measurement state variables
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

    // Radio/Checkbox states
    var kalar by remember { mutableStateOf("") }
    var daman by remember { mutableStateOf("") }
    var bazo by remember { mutableStateOf("") }
    var sidePoket by remember { mutableStateOf("") }
    var button by remember { mutableStateOf("") }
    var cup by remember { mutableStateOf("") }
    var chamakDaga by remember { mutableStateOf("") }
    var frontPoket by remember { mutableStateOf(false) }
    var shalwarPoket by remember { mutableStateOf(false) }

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
            kalar = m.kalar
            daman = m.daman
            bazo = m.bazo
            sidePoket = m.sidePoket
            button = m.button
            cup = m.cup
            chamakDaga = m.chamakDaga
            frontPoket = m.frontPoket
            shalwarPoket = m.shalwarPoket
        }
    }

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
                        backgroundOffset * 1000f, backgroundOffset * 1000f
                    ),
                    end = androidx.compose.ui.geometry.Offset(
                        (1 - backgroundOffset) * 1000f, (1 - backgroundOffset) * 1000f
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                BeautifulMeasurementTopBar(onBackClick = { navController.navigateUp() })
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { data -> CustomSnackbar(data) }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Basic measurements in 3-column grid
                val basicFields = listOf(
                    BasicField("لمبائی", shirtLength) { shirtLength = it },
                    BasicField("بازو", shirtArm) { shirtArm = it },
                    BasicField("تیرا", shoulder) { shoulder = it },
                    BasicField("گلہ", collar) { collar = it },
                    BasicField("چھاتی", chest) { chest = it },
                    BasicField("چوڑائی", lap) { lap = it },
                    BasicField("دامن", pant) { pant = it },
                    BasicField("شلوار", shalwar) { shalwar = it },
                    BasicField("پنچہ", panch) { panch = it }
                )

                basicFields.chunked(3).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowItems.forEach { (label, value, onValueChange) ->
                            SimpleTextField(
                                label = label,
                                value = value,
                                onValueChange = onValueChange,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Style options in 3-column grid
                val styleFields = listOf(
                    StyleField("کالر", kalar, listOf("کالر", "ہاف بین")) { kalar = it },
                    StyleField("چمک ڈاگہ", chamakDaga, listOf("سنگل", "ڈبل", "ٹرپل")) {
                        chamakDaga = it
                    },
                    StyleField("دامن", daman, listOf("گول دامن", "چورس دامن")) { daman = it },
                    StyleField("بازو", bazo, listOf("فٹ بازو", "عام بازو", "گول بازو")) {
                        bazo = it
                    },
                    StyleField("بٹن", button, listOf("سادہ", "ڈیزائن")) { button = it },
                    StyleField("کف", cup, listOf("چورس", "گول")) { cup = it },
                )

                styleFields.chunked(3).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowItems.forEach { (label, value, options, onSelected) ->
                            SimpleRadioGroup(
                                title = label,
                                options = options,
                                selected = value,
                                onSelected = onSelected,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Checkboxes in 2-column grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            IntrinsicSize.Min
                        ),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StyleField("سائیڈ پاکٹ", sidePoket, listOf("0", "1", "2")) { sidePoket = it }

                    SimpleRadioGroup(
                        title = "سائیڈ پاکٹ",
                        options = listOf("0", "1", "2"),
                        selected = sidePoket,
                        onSelected = { sidePoket = it },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )


                    SimpleCheckbox(
                        title = "فرنٹ پاکٹ",
                        checked = frontPoket,
                        onCheckedChange = { frontPoket = it },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    SimpleCheckbox(
                        title = "شلوار پاکٹ",
                        checked = shalwarPoket,
                        onCheckedChange = { shalwarPoket = it },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }

                // Extra field
                SimpleTextField(
                    label = "Extra",
                    value = extra,
                    onValueChange = { extra = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Save Button
                SimpleSaveButton(
                    onClick = {
                        viewModel.addMeasurement(
                            customerId, Measurement(
                                id = "", // Will be generated by Firestore
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
                                extra = extra,
                                kalar = kalar,
                                daman = daman,
                                bazo = bazo,
                                sidePoket = sidePoket,
                                button = button,
                                cup = cup,
                                chamakDaga = chamakDaga,
                                frontPoket = frontPoket,
                                shalwarPoket = shalwarPoket
                            )
                        )
                    }
                )
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
                            Color.White.copy(alpha = 0.95f),
                            Color.White.copy(alpha = 0.9f)
                        )
                    ),
                    RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
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
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF667eea),
                                        Color(0xFF764ba2)
                                    )
                                ),
                                CircleShape
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
                                    Color(0xFF4facfe),
                                    Color(0xFF00f2fe)
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
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Card(
        modifier = modifier
            .height(70.dp)
            .shadow(2.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF667eea),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFF667eea),
                    unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.5f),
                    focusedTextColor = Color(0xFF2D3748),
                    unfocusedTextColor = Color(0xFF2D3748)
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SimpleRadioGroup(
    modifier: Modifier = Modifier,
    title: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Card(
        modifier = modifier
            .height(70.dp)
            .shadow(2.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF667eea),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { option ->
                    val isSelected = selected == option
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) Color(0xFF667eea) else Color.Gray.copy(alpha = 0.3f),
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { onSelected(option) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = option,
                            fontSize = 8.sp,
                            color = if (isSelected) Color.White else Color(0xFF2D3748),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleCheckbox(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxHeight()
            .clickable {
                onCheckedChange(!checked)
            }
            .shadow(2.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        if (checked) Color(0xFF667eea) else Color.Gray.copy(alpha = 0.3f),
                        RoundedCornerShape(3.dp)
                    )
                    .clickable { onCheckedChange(!checked) },
                contentAlignment = Alignment.Center
            ) {
                if (checked) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D3748)
            )
        }
    }
}

@Composable
private fun SimpleSaveButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF667eea)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Save Measurements",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


