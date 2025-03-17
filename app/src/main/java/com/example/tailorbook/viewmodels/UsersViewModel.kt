package com.example.tailorbook.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tailorbook.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "UsersViewModel"

class UsersViewModel : ViewModel() {

    private val _state = MutableStateFlow<UserListState>(UserListState.Loading)
    val state: StateFlow<UserListState> = _state

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())

    init {
        handleIntent(UserListIntent.LoadUsers)
        fetchUsers()
    }

    fun handleIntent(intent: UserListIntent) {
        when (intent) {
            is UserListIntent.LoadUsers -> fetchUsers()
            is UserListIntent.SearchUsers -> filterUsers(intent.query)
        }
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            try {
                _state.value = UserListState.Loading
                val snapshot = FirebaseFirestore.getInstance().collection("users").document(
                    FirebaseAuth.getInstance().uid.toString()
                ).collection("customers").get().await()
                val userList = snapshot.documents.map { doc ->
                    User(
                        userid = doc.id,
                        name = doc.getString("name") ?: "",
                        phone = doc.getString("phone") ?: "",
                        img = doc.getString("image") ?: ""
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
}


sealed class UserListState {
    object Loading : UserListState()
    data class Success(val users: List<User>, val searchQuery: String) : UserListState()
    data class Error(val message: String) : UserListState()
}

sealed class UserListIntent {
    object LoadUsers : UserListIntent()
    data class SearchUsers(val query: String) : UserListIntent()
}
