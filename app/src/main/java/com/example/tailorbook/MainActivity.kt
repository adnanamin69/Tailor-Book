package com.example.tailorbook

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.tailorbook.routes.NavHostManager
import com.example.tailorbook.routes.NavHostManager.LocalUserSearch
import com.example.tailorbook.routes.NavHostManager.SetupNavHost
import com.example.tailorbook.routes.NavHostManager.LocalAddCustomerError
import com.example.tailorbook.routes.NavHostManager.LocalAddCustomerSuccess
import com.example.tailorbook.routes.NavHostManager.LocalUsersViewModel
import com.example.tailorbook.ui.theme.TailorBookTheme
import com.example.tailorbook.viewmodels.UsersViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TailorBookTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    val mainViewModel: UsersViewModel = koinViewModel()


                    CompositionLocalProvider(
                        NavHostManager.LocalNavController provides rememberNavController(),
                        NavHostManager.LocalMainViewModelState provides mainViewModel.state,
                        LocalUserSearch provides mainViewModel::handleIntent,
                        LocalAddCustomerError provides mainViewModel.addCustomerError,
                        LocalAddCustomerSuccess provides mainViewModel.addCustomerSuccess,
                        LocalUsersViewModel provides mainViewModel
                    ) {
                        SetupNavHost()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TailorBookTheme {
        Greeting("Android")
    }
}