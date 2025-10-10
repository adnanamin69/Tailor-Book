package com.example.tailorbook.routes

import android.os.Parcelable
import kotlinx.serialization.Serializable

@Serializable
sealed class Navigation {
    @Serializable
    data object Splash : Navigation()

    @Serializable
    data object Home : Navigation()

    @Serializable
    data object Login : Navigation()

    @Serializable
    data object AddUSer : Navigation()

    // New screens
    @Serializable
    data object Dashboard : Navigation()

    @Serializable
    data class CustomerProfile(val customerId: String) : Navigation()

    @Serializable
    data class Measurements(val customerId: String) : Navigation()

    @Serializable
    data class MeasurementForm(val customerId: String, val measurementId: String? = null) : Navigation()

    @Serializable
    data class Orders(val customerId: String) : Navigation()

    @Serializable
    data class OrderForm(val customerId: String, val measurementId: String? = null, val orderId: String? = null) : Navigation()

    @Serializable
    data class OrderDetails(val customerId: String, val orderId: String) : Navigation()

    @Serializable
    data class Payments(val customerId: String, val orderId: String) : Navigation()

    @Serializable
    data class EditCustomer(val customerId: String) : Navigation()




    @Serializable
    data class AddMeaurement(val customerId: String) : Navigation()

}