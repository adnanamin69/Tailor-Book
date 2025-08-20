package com.example.tailorbook.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tailorbook.models.Order
import com.example.tailorbook.models.OrderStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrdersViewModel : ViewModel() {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

    fun fetchOrders(customerId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val uid = FirebaseAuth.getInstance().uid ?: return@launch
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("users").document(uid)
                    .collection("customers").document(customerId)
                    .collection("orders")
                    .get().await()
                _orders.value = snapshot.documents.map { d ->
                    Order(
                        id = d.id,
                        customerId = customerId,
                        measurementId = d.getString("measurementId") ?: "",
                        dressType = d.getString("dressType") ?: "",
                        checkInDate = d.getLong("checkInDate") ?: 0L,
                        deliveryDate = d.getLong("deliveryDate") ?: 0L,
                        status = runCatching { OrderStatus.valueOf(d.getString("status") ?: "PENDING") }.getOrDefault(OrderStatus.PENDING),
                        totalDue = (d.getDouble("totalDue") ?: 0.0),
                        totalPaid = (d.getDouble("totalPaid") ?: 0.0)
                    )
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.localizedMessage
                _isLoading.value = false
            }
        }
    }

    fun addOrUpdateOrder(customerId: String, orderId: String?, order: Order) {
        viewModelScope.launch {
            try {
                _saveSuccess.value = false
                _error.value = null
                _isLoading.value = true
                val uid = FirebaseAuth.getInstance().uid ?: return@launch
                val col = FirebaseFirestore.getInstance()
                    .collection("users").document(uid)
                    .collection("customers").document(customerId)
                    .collection("orders")

                val data = hashMapOf(
                    "measurementId" to order.measurementId,
                    "dressType" to order.dressType,
                    "checkInDate" to order.checkInDate,
                    "deliveryDate" to order.deliveryDate,
                    "status" to order.status.name,
                    "totalDue" to order.totalDue,
                    "totalPaid" to order.totalPaid
                )
                if (orderId == null) col.add(data).await() else col.document(orderId).set(data).await()
                _saveSuccess.value = true
                fetchOrders(customerId)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.localizedMessage
                _isLoading.value = false
            }
        }
    }

    fun fetchOrder(customerId: String, orderId: String) {
        viewModelScope.launch {
            try {
                val uid = FirebaseAuth.getInstance().uid ?: return@launch
                val d = FirebaseFirestore.getInstance()
                    .collection("users").document(uid)
                    .collection("customers").document(customerId)
                    .collection("orders").document(orderId)
                    .get().await()
                _selectedOrder.value = if (d.exists()) {
                    Order(
                        id = d.id,
                        customerId = customerId,
                        measurementId = d.getString("measurementId") ?: "",
                        dressType = d.getString("dressType") ?: "",
                        checkInDate = d.getLong("checkInDate") ?: 0L,
                        deliveryDate = d.getLong("deliveryDate") ?: 0L,
                        status = runCatching { OrderStatus.valueOf(d.getString("status") ?: "PENDING") }.getOrDefault(OrderStatus.PENDING),
                        totalDue = (d.getDouble("totalDue") ?: 0.0),
                        totalPaid = (d.getDouble("totalPaid") ?: 0.0)
                    )
                } else null
            } catch (e: Exception) {
                _selectedOrder.value = null
            }
        }
    }
}


