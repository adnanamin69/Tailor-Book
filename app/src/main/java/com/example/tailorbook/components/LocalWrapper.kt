package com.example.tailorbook.components


import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import com.example.tailorbook.routes.NavHostManager.LocalNavController
import com.example.tailorbook.ui.theme.TailorBookTheme


@Composable
fun LocalProviderWrapper(content: @Composable () -> Unit) {
    TailorBookTheme {

        CompositionLocalProvider(
            LocalNavController provides rememberNavController(),
            content = content
        )
    }
}
