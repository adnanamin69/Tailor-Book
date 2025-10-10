package com.example.tailorbook.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tailorbook.models.OrderStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class DashboardState(
    val totalCustomers: Int = 0,
    val pending: Int = 0,
    val inProgress: Int = 0,
    val completed: Int = 0,
    val delivered: Int = 0,
    val totalDueOutstanding: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class DashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(DashboardState(isLoading = true))
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun fetchDashboard() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)
                val uid = FirebaseAuth.getInstance().uid ?: run {
                    _state.value = _state.value.copy(isLoading = false, error = "Not signed in")
                    return@launch
                }
                val db = FirebaseFirestore.getInstance()

                val customersSnap = db.collection("users").document(uid).collection("customers").get().await()
                val customers = customersSnap.documents

                var pending = 0
                var inProgress = 0
                var completed = 0
                var delivered = 0
                var totalDueOutstanding = 0.0

                // Fetch orders for each customer in parallel
                val jobs = customers.map { c ->
                    async {
                        val orders = db.collection("users").document(uid)
                            .collection("customers").document(c.id)
                            .collection("orders").get().await()
                        orders.documents.forEach { d ->
                            val status = runCatching { OrderStatus.valueOf(d.getString("status") ?: "PENDING") }.getOrDefault(OrderStatus.PENDING)
                            when (status) {
                                OrderStatus.PENDING -> pending++
                                OrderStatus.IN_PROGRESS -> inProgress++
                                OrderStatus.COMPLETED -> completed++
                                OrderStatus.DELIVERED -> delivered++
                            }
                            val due = d.getDouble("totalDue") ?: 0.0
                            val paid = d.getDouble("totalPaid") ?: 0.0
                            totalDueOutstanding += (due - paid).coerceAtLeast(0.0)
                        }
                    }
                }
                jobs.awaitAll()

                _state.value = DashboardState(
                    totalCustomers = customers.size,
                    pending = pending,
                    inProgress = inProgress,
                    completed = completed,
                    delivered = delivered,
                    totalDueOutstanding = totalDueOutstanding,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.localizedMessage ?: "Dashboard error")
            }
        }
    }
}


