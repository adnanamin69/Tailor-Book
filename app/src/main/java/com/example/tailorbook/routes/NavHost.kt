package com.example.tailorbook.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.tailorbook.screens.AddMeasurementScreen
import com.example.tailorbook.screens.CustomerFormPage
import com.example.tailorbook.screens.CustomerProfileScreen
import com.example.tailorbook.screens.DashboardScreen
import com.example.tailorbook.screens.EditCustomerScreen
import com.example.tailorbook.screens.HomeScreen
import com.example.tailorbook.screens.OrderDetailsScreen
import com.example.tailorbook.screens.OrderFormScreen
import com.example.tailorbook.screens.OrderFormScreen2
import com.example.tailorbook.screens.OrdersScreen
import com.example.tailorbook.screens.PaymentsScreen
import com.example.tailorbook.screens.PhoneNumberScreen
import com.example.tailorbook.screens.SplashScreen
import com.example.tailorbook.screens.newscreens.AddMeasurementScreen1
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

    val LocalAddCustomerError =
        compositionLocalOf<StateFlow<String?>> { error("Add customer error flow not found") }

    val LocalAddCustomerSuccess =
        compositionLocalOf<StateFlow<Boolean>> { error("Add customer success flow not found") }


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

            // New feature screens
            composable<Navigation.Dashboard> { DashboardScreen() }
            composable<Navigation.CustomerProfile> { backStackEntry ->
                val args = backStackEntry.toRoute<Navigation.CustomerProfile>()
                CustomerProfileScreen(args.customerId)
            }
            /*composable<Navigation.Measurements> { backStackEntry ->
                val args = backStackEntry.toRoute<Navigation.Measurements>()
                MeasurementsScreen(args.customerId)
            }
            composable<Navigation.MeasurementForm> { backStackEntry ->
                val args = backStackEntry.toRoute<Navigation.MeasurementForm>()
                MeasurementFormScreen(args.customerId, args.measurementId)
            }*/
            composable<Navigation.Orders> { backStackEntry ->
                val args = backStackEntry.toRoute<Navigation.Orders>()
                OrdersScreen(args.customerId)
            }
            composable<Navigation.OrderForm> { backStackEntry ->
                val args = backStackEntry.toRoute<Navigation.OrderForm>()
                OrderFormScreen2(args.customerId, args.measurementId, args.orderId)
            }
            composable<Navigation.OrderDetails> { backStackEntry ->
                val args = backStackEntry.toRoute<Navigation.OrderDetails>()
                OrderDetailsScreen(args.customerId, args.orderId)
            }
            composable<Navigation.Payments> { backStackEntry ->
                val args = backStackEntry.toRoute<Navigation.Payments>()
                PaymentsScreen(args.customerId, args.orderId)
            }
            composable<Navigation.EditCustomer> { backStackEntry ->
                val args = backStackEntry.toRoute<Navigation.EditCustomer>()
                // For simplicity, pass empty initial values; real flow would fetch customer by id
                EditCustomerScreen(
                    args.customerId,
                    initialName = "",
                    initialPhone = args.customerId,
                    initialImage = null
                )
            }


            composable<Navigation.AddMeaurement> { backStackEntry ->
                val args = backStackEntry.toRoute<Navigation.EditCustomer>()
                // For simplicity, pass empty initial values; real flow would fetch customer by id
                AddMeasurementScreen1(args.customerId)
            }


        }

    }
}