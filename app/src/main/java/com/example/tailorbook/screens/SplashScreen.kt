package com.example.tailorbook.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.tailorbook.components.LocalProviderWrapper
import com.example.tailorbook.routes.NavHostManager.LocalNavController


@Composable
fun SplashScreen() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            AnimatedLinearProgressIndicator()
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}


@Composable
fun AnimatedLinearProgressIndicator(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    var progress by remember { mutableFloatStateOf(0.1F) }
    val progressAnimDuration = 8_000
    val progressAnimation by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = progressAnimDuration, easing = FastOutSlowInEasing),
        label = "",
    )
    LinearProgressIndicator(
        progress = { progressAnimation },
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.White, RoundedCornerShape(8.dp)),
        strokeCap = StrokeCap.Round,
        /*     trackColor = Color.LightGray,
             color = Color.White*/


    )
    LaunchedEffect(lifecycleOwner) {
        progress = 1f
    }
}

@Preview
@Composable
fun Preview() {
    LocalProviderWrapper {
        SplashScreen()
    }
}