package com.example.tailorbook.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.tailorbook.components.LocalProviderWrapper
import com.example.tailorbook.models.User
import com.example.tailorbook.routes.NavHostManager
import com.example.tailorbook.routes.NavHostManager.LocalNavController
import com.example.tailorbook.routes.Navigation
import com.example.tailorbook.viewmodels.UserListIntent
import com.example.tailorbook.viewmodels.UserListState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val navController = LocalNavController.current


    val state = NavHostManager.LocalMainViewModelState.current.collectAsState().value
    val handleIntent = NavHostManager.LocalUserSearch.current
    Scaffold(

        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                    navController.navigate(Navigation.AddUSer)
                }
            ) {
                Icon(Icons.Filled.Add, "")
            }
        },
        topBar = {

            Column {
                val titleText = if (state is UserListState.Success) {
                    "Customers (" + state.users.size + ")"
                } else "Customers"
                TopAppBar(
                    title = { Text(titleText) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                if (state is UserListState.Success) {
                    SearchBar(
                        query = state.searchQuery,
                        onQueryChanged = { handleIntent(UserListIntent.SearchUsers(it)) }
                    )
                }
            }

            /*  TopAppBar(
                  colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                  title = {
                      Text("")
                  },


                  actions = {

                  }

              )*/
        }) {


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (state) {
                is UserListState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                is UserListState.Success -> {
                    if (state.users.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No customers yet. Tap + to add.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(state.users) { user ->
                                UserListItem(user) {
                                    navController.navigate(
                                        Navigation.CustomerProfile(user.userid)
                                    )
                                }
                            }
                        }
                    }
                }

                is UserListState.Error -> Text(
                    text = state.message,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }


    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UserListItem(user: User, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick.invoke()
            }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val isLikelyBase64 = user.img.isNotBlank() && !user.img.startsWith("http", true)
        if (isLikelyBase64) {
            val bytes = runCatching { Base64.decode(user.img, Base64.DEFAULT) }.getOrNull()
            val bmp = bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            if (bmp != null) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            } else {
                GlideImage(
                    user.img,
                    contentDescription = "",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            }
        } else {
            GlideImage(
                user.img,
                contentDescription = "",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        }


        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(text = user.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = user.phone, style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        label = { Text("Search by Name or Phone") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}


@Preview
@Composable
fun HomePreview() {
    LocalProviderWrapper {
        HomeScreen()
    }
}