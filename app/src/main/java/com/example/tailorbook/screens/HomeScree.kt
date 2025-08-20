package com.example.tailorbook.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                TopAppBar(title = { Text("User List") })
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


        Box(modifier = Modifier.padding(it)) {
            when (state) {
                is UserListState.Loading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                is UserListState.Success -> {
                    LazyColumn(
                        modifier = Modifier.padding(it),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {

                        items(state.users) { user ->
                            UserListItem(user)
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
fun UserListItem(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


        GlideImage(user.img, contentDescription = "", modifier = Modifier.size(50.dp))


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