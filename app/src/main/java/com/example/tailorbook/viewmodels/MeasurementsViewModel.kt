package com.example.tailorbook.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tailorbook.models.Measurement
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MeasurementsViewModel : ViewModel() {
    private val _measurements = MutableStateFlow<List<Measurement>>(emptyList())
    val measurements: StateFlow<List<Measurement>> = _measurements.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _addSuccess = MutableStateFlow(false)
    val addSuccess: StateFlow<Boolean> = _addSuccess.asStateFlow()

    fun fetchMeasurements(customerId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val uid = FirebaseAuth.getInstance().uid ?: run {
                    _error.value = "Not signed in"
                    _isLoading.value = false
                    return@launch
                }
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("users").document(uid)
                    .collection("customers").document(customerId)
                    .collection("measurements")
                    .get().await()
                _measurements.value = snapshot.documents.map { d ->
                    Measurement(
                        id = d.id,
                        customerId = customerId,
                        shirt_length = d.getString("shirt_length") ?: "",
                        shirt_arm = d.getString("shirt_arm") ?: "",
                        shoulder = d.getString("shoulder") ?: "",
                        collar = d.getString("collar") ?: "",
                        chest = d.getString("chest") ?: "",
                        lap = d.getString("lap") ?: "",
                        pant = d.getString("pant") ?: "",
                        panch = d.getString("panch") ?: "",
                        extra = d.getString("extra") ?: ""
                    )
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to load measurements"
                _isLoading.value = false
            }
        }
    }

    fun addMeasurement(customerId: String, m: Measurement) {
        viewModelScope.launch {
            try {
                _addSuccess.value = false
                _error.value = null
                _isLoading.value = true
                val uid = FirebaseAuth.getInstance().uid ?: run {
                    _error.value = "Not signed in"
                    _isLoading.value = false
                    return@launch
                }
                val data = hashMapOf(
                    "shirt_length" to m.shirt_length,
                    "shirt_arm" to m.shirt_arm,
                    "shoulder" to m.shoulder,
                    "collar" to m.collar,
                    "chest" to m.chest,
                    "lap" to m.lap,
                    "pant" to m.pant,
                    "panch" to m.panch,
                    "extra" to m.extra
                )
                FirebaseFirestore.getInstance()
                    .collection("users").document(uid)
                    .collection("customers").document(customerId)
                    .collection("measurements")
                    .document("m1")
                    .set(data).await()
                _addSuccess.value = true
                fetchMeasurements(customerId)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to add measurement"
                _isLoading.value = false
            }
        }
    }
}


