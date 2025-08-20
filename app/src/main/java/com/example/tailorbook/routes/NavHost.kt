package com.example.tailorbook.routes


import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tailorbook.models.User
import com.example.tailorbook.screens.CustomerFormPage
import com.example.tailorbook.screens.HomeScreen
import com.example.tailorbook.screens.PhoneNumberScreen
import com.example.tailorbook.screens.SplashScreen
import com.example.tailorbook.viewmodels.UserListIntent
import com.example.tailorbook.viewmodels.UserListState
import kotlinx.coroutines.flow.StateFlow

object NavHostManager {
    val LocalNavController =
        compositionLocalOf<NavHostController> { error("No NavController found!") }

    val LocalUserSearch =
        compositionLocalOf<(UserListIntent) -> Unit> { error("ViewModel intent not found") }


    val LocalMainViewModelState =
        compositionLocalOf<StateFlow<UserListState>> { error("ViewModel state not found") }


    @Composable
    fun SetupNavHost(
    ) {
        NavHost(
            navController = LocalNavController.current,
            startDestination = Navigation.Splash
        ) {
            composable<Navigation.Splash> {
                SplashScreen()
            }


            composable<Navigation.Login> {
                PhoneNumberScreen()
            }
            composable<Navigation.Home> {
                HomeScreen()
            }

            composable<Navigation.AddUSer> {
                CustomerFormPage()
            }


        }


    }
}