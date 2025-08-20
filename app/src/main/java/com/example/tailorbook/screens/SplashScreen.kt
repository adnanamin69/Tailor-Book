package com.example.tailorbook.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.tailorbook.auth.AuthViewModel
import com.example.tailorbook.components.LocalProviderWrapper
import com.example.tailorbook.routes.NavHostManager.LocalNavController
import com.example.tailorbook.routes.Navigation
import org.koin.androidx.compose.koinViewModel


@Composable
fun SplashScreen() {
    val navController = LocalNavController.current
    val authViewModel: AuthViewModel = koinViewModel()
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.95f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                val target = 1f
                val animDuration = 1200
                val animatedAlpha by animateFloatAsState(
                    targetValue = target,
                    animationSpec = tween(durationMillis = animDuration, easing = FastOutSlowInEasing),
                    label = ""
                )

                Text(
                    text = "TailorBook",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White.copy(alpha = animatedAlpha)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Manage customers with ease",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = animatedAlpha)
                )

                Spacer(modifier = Modifier.height(24.dp))
                AnimatedLinearProgressIndicator()

                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(animDuration.toLong())
                    val destination = if (authViewModel.isSignedIn) Navigation.Home else Navigation.Login
                    navController.navigate(destination) {
                        popUpTo(Navigation.Splash) { inclusive = true }
                    }
                }
            }
        }
    }
}


@Composable
fun AnimatedLinearProgressIndicator(
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableFloatStateOf(0.1F) }
    val progressAnimDuration = 1200
    val progressAnimation by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = progressAnimDuration, easing = FastOutSlowInEasing),
        label = "",
    )
    androidx.compose.material3.LinearProgressIndicator(
        progress = { progressAnimation },
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(8.dp)),
        strokeCap = StrokeCap.Round
    )

    LaunchedEffect(Unit) {
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