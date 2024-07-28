package com.venture.userlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venture.userlist.data.model.UserDTO
import com.venture.userlist.data.model.UserRequest
import com.venture.userlist.domain.repo.UserRepository
import com.venture.userlist.data.repo.UserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<UserDTO>>(emptyList())
    val users: StateFlow<List<UserDTO>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors.asStateFlow()

    private var currentPage = 1
    private var lastPageReached = false
    private var totalPages = 1

    init {
        getLastPageUsers()
    }

    private fun getUsers(page: Int) {
        viewModelScope.launch {
            repository.getUsers(page)
                .onStart { _isLoading.value = true }
                .catch { e -> _error.value = e.message }
                .collect { userResponse ->
                    _users.value = userResponse.users
                    totalPages = userResponse.totalPages
                    currentPage = userResponse.currentPage
                    lastPageReached = currentPage >= totalPages
                    _isLoading.value = false
                }
        }
    }

    private fun getLastPageUsers() {
        viewModelScope.launch {
            repository.getUsers(1)
                .collect { userResponse ->
                    totalPages = userResponse.totalPages
                    getUsers(totalPages)
                }
        }
    }

    fun loadMoreUsers() {
        if (lastPageReached || _isLoading.value) return

        getUsers(++currentPage)
    }

    suspend fun createUser(name: String, email: String, gender: String, status: String): Boolean {
        var isSuccess = false
        try {
            repository.createUser(UserRequest(name, email, gender, status))
                .onStart {
                    _isLoading.value = true
                    _validationErrors.value = emptyMap()
                }
                .catch { e ->
                    val errorResponse = e.message?.let { parseErrorResponse(it) }
                    _validationErrors.value = errorResponse ?: emptyMap()
                    _error.value = e.message
                }
                .collect { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { newUser ->
                            _users.value = _users.value + newUser
                        }
                        _isLoading.value = false
                        isSuccess = true
                    } else {
                        val errorResponse =
                            response.errorBody()?.string()?.let { parseErrorResponse(it) }
                        _validationErrors.value = errorResponse ?: emptyMap()
                        _isLoading.value = false
                        isSuccess = false
                    }
                }
        } catch (e: Exception) {
            isSuccess = false
        }
        return isSuccess
    }

    fun deleteUser(user: UserDTO) {
        viewModelScope.launch {
            repository.deleteUser(user.id)
                .onStart { _isLoading.value = true }
                .catch { e -> _error.value = e.message }
                .collect { success ->
                    if (success) {
                        _users.value -= user
                    }
                    _isLoading.value = false
                }
        }
    }

    private fun parseErrorResponse(response: String): Map<String, String> {
        return try {
            val jsonElement = Json.parseToJsonElement(response)
            val errors = mutableMapOf<String, String>()
            if (jsonElement is JsonArray) {
                jsonElement.forEach { element ->
                    if (element is JsonObject) {
                        val field = element["field"]?.jsonPrimitive?.content
                        val message = element["message"]?.jsonPrimitive?.content
                        if (field != null && message != null) {
                            errors[field] = message
                        }
                    }
                }
            }
            errors
        } catch (e: Exception) {
            mapOf("general" to "Unexpected error occurred")
        }
    }
}
