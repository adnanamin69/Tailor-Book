package com.example.tailorbook.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.tailorbook.components.LocalProviderWrapper
import com.example.tailorbook.routes.NavHostManager.LocalNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            title = {
                Text("")
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        Icons.Outlined.ArrowBackIosNew,
                        contentDescription = ""
                    )
                }
            },

            actions = {

            }

        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(6.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(text = "Home Screen")
        }
    }
}


@Preview
@Composable
fun HomePreview() {
    LocalProviderWrapper {
        HomeScreen()
    }
}