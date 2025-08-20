package com.example.tailorbook.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tailorbook.models.Payment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PaymentsViewModel : ViewModel() {
    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    fun fetchPayments(customerId: String, orderId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val uid = FirebaseAuth.getInstance().uid ?: return@launch
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("users").document(uid)
                    .collection("customers").document(customerId)
                    .collection("orders").document(orderId)
                    .collection("payments").get().await()
                _payments.value = snapshot.documents.map { d ->
                    Payment(
                        id = d.id,
                        orderId = orderId,
                        amount = d.getDouble("amount") ?: 0.0,
                        date = d.getLong("date") ?: 0L
                    )
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.localizedMessage
                _isLoading.value = false
            }
        }
    }

    fun addPayment(customerId: String, orderId: String, amount: Double, date: Long) {
        viewModelScope.launch {
            try {
                _saveSuccess.value = false
                _error.value = null
                _isLoading.value = true
                val uid = FirebaseAuth.getInstance().uid ?: return@launch
                val col = FirebaseFirestore.getInstance()
                    .collection("users").document(uid)
                    .collection("customers").document(customerId)
                    .collection("orders").document(orderId)
                    .collection("payments")
                val data = hashMapOf(
                    "amount" to amount,
                    "date" to date
                )
                col.add(data).await()
                _saveSuccess.value = true
                fetchPayments(customerId, orderId)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.localizedMessage
                _isLoading.value = false
            }
        }
    }
}


