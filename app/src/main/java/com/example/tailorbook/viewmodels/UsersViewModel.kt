package com.example.tailorbook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tailorbook.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

private const val TAG = "UsersViewModel"

class UsersViewModel : ViewModel() {

    private val _state = MutableStateFlow<UserListState>(UserListState.Loading)
    val state: StateFlow<UserListState> = _state

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())

    // Add-customer operation status
    private val _addCustomerError = MutableStateFlow<String?>(null)
    val addCustomerError = _addCustomerError.asStateFlow()

    private val _addCustomerSuccess = MutableStateFlow(false)
    val addCustomerSuccess = _addCustomerSuccess.asStateFlow()

    private val _isAddingCustomer = MutableStateFlow(false)
    val isAddingCustomer = _isAddingCustomer.asStateFlow()

    // Selected customer for sharing
    private val _selectedCustomer = MutableStateFlow<User?>(null)
    val selectedCustomer: StateFlow<User?> = _selectedCustomer.asStateFlow()

    init {
        handleIntent(UserListIntent.LoadUsers)
        fetchUsers()
    }

    fun handleIntent(intent: UserListIntent) {
        when (intent) {
            is UserListIntent.LoadUsers -> fetchUsers()
            is UserListIntent.SearchUsers -> filterUsers(intent.query)
            is UserListIntent.UploadImage -> uploadImage(intent)
            UserListIntent.ClearAddCustomerStatus -> clearAddCustomerStatus()
        }
    }

    private fun uploadImage(intent: UserListIntent.UploadImage) {
        viewModelScope.launch {
            try {
                _isAddingCustomer.value = true
                _addCustomerError.value = null
                _addCustomerSuccess.value = false
                Log.i(TAG, "uploadImage: add customor")
                val name = intent.name.trim()
                val phone = intent.phone.trim()
                val address = intent.address.trim()
                val imageBase64 = intent.imageBase64?.trim().orEmpty()

                if (name.isEmpty() || phone.isEmpty()) {
                    _addCustomerError.value = "Name and phone are required"
                    return@launch
                }

                val userData = hashMapOf(
                    "name" to name,
                    "phone" to phone,
                    "address" to address,
                    "image" to imageBase64
                )

                val uid = FirebaseAuth.getInstance().uid ?: run {
                    _addCustomerError.value = "Not signed in"
                    _isAddingCustomer.value = false
                    return@launch
                }

                val customers = FirebaseFirestore.getInstance()
                    .collection("users").document(uid)
                    .collection("customers")

                // Use phone number as document ID to enforce uniqueness
                val docRef = customers.document(phone)
                val existing = docRef.get().await()
                if (existing.exists()) {
                    _addCustomerError.value = "Customer with this phone already exists"
                    _isAddingCustomer.value = false
                    return@launch
                }

                docRef.set(userData).await()
                _addCustomerSuccess.value = true
                fetchUsers()
                Log.i(TAG, "addddd: add ${_addCustomerSuccess.value}")

                _isAddingCustomer.value = false
            } catch (e: Exception) {
                _addCustomerError.value = e.localizedMessage ?: "Unexpected error"
                _isAddingCustomer.value = false
            }
        }
    }

    private fun clearAddCustomerStatus() {
        _addCustomerError.value = null
        _addCustomerSuccess.value = false
        Log.i(TAG, "clear: add ${_addCustomerSuccess.value}")

    }

    fun updateCustomer(customerId: String, name: String, phone: String, imageBase64: String?) {
        viewModelScope.launch {
            try {
                val uid = FirebaseAuth.getInstance().uid ?: return@launch
                val customers = FirebaseFirestore.getInstance()
                    .collection("users").document(uid)
                    .collection("customers")
                val docRef = customers.document(customerId)
                val data = hashMapOf(
                    "name" to name,
                    "phone" to phone,
                    "image" to (imageBase64 ?: "")
                )
                docRef.set(data).await()
                fetchUsers()
            } catch (_: Exception) {
            }
        }
    }


    private fun fetchUsers() {
        viewModelScope.launch {
            try {
                _state.value = UserListState.Loading

                val uid = FirebaseAuth.getInstance().uid ?: return@launch
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("users").document(uid)
                    .collection("customers")
                    .get()
                    .await()

                val userList = snapshot.documents.map { doc ->
                    val userid = doc.id
                    val name = doc.getString("name") ?: ""
                    val phone = doc.getString("phone") ?: ""
                    val img = doc.getString("image") ?: ""

                    // Fetch orders for this user
                    val ordersSnapshot = FirebaseFirestore.getInstance()
                        .collection("users").document(uid)
                        .collection("customers").document(userid)
                        .collection("orders")
                        .get()
                        .await()

                    var total = 0.0
                    var nearest: Long? = null

                    ordersSnapshot.documents.forEach { d ->
                        val due = d.getDouble("totalDue") ?: 0.0
                        val paid = d.getDouble("totalPaid") ?: 0.0
                        total += (due - paid).coerceAtLeast(0.0)

                        val status = d.getString("status") ?: "PENDING"
                        val delivery = d.getLong("deliveryDate") ?: 0L
                        val isActive = status == "PENDING" || status == "IN_PROGRESS"
                        if (isActive && delivery > 0L) {
                            val now = System.currentTimeMillis()
                            if (delivery >= now) {
                                nearest =
                                    if (nearest == null) delivery else minOf(nearest!!, delivery)
                            }
                        }
                    }

                    User(
                        userid = userid,
                        name = name,
                        phone = phone,
                        img = img,
                        remainingBalance = total,
                        nearestDelivery = nearest
                    )
                }

                _allUsers.value = userList
                _state.value = UserListState.Success(users = userList, searchQuery = "")

            } catch (e: Exception) {
                _state.value = UserListState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }


    private fun filterUsers(query: String) {
        val filteredList = if (query.isBlank()) _allUsers.value
        else _allUsers.value.filter {
            it.name.contains(query, true) || it.phone.contains(
                query,
                true
            )
        }

        _state.value = UserListState.Success(users = filteredList, searchQuery = query)
    }

    fun fetchCustomerById(customerId: String) {
        viewModelScope.launch {
            try {
                val uid = FirebaseAuth.getInstance().uid ?: return@launch
                val doc = FirebaseFirestore.getInstance()
                    .collection("users").document(uid)
                    .collection("customers").document(customerId)
                    .get().await()

                _selectedCustomer.value = if (doc.exists()) {
                    User(
                        userid = doc.id,
                        name = doc.getString("name") ?: "",
                        phone = doc.getString("phone") ?: "",
                        img = doc.getString("image") ?: ""
                    )
                } else null
            } catch (e: Exception) {
                _selectedCustomer.value = null
            }
        }
    }
}


sealed class UserListState {
    object Loading : UserListState()
    data class Success(val users: List<User>, val searchQuery: String) : UserListState()
    data class Error(val message: String) : UserListState()
}

sealed class UserListIntent {
    object LoadUsers : UserListIntent()
    object ClearAddCustomerStatus : UserListIntent()
    data class UploadImage(
        val name: String,
        val phone: String,
        val address: String,
        val imageBase64: String?
    ) :
        UserListIntent()

    data class SearchUsers(val query: String) : UserListIntent()
}
