package com.venture.userlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venture.userlist.data.model.UserDTO
import com.venture.userlist.data.model.UserRequest
import com.venture.userlist.domain.repo.UserRepository
import com.venture.userlist.domain.results.BaseError
import com.venture.userlist.domain.results.DataError
import com.venture.userlist.domain.results.ResultResponse
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
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _users = MutableStateFlow<ResultResponse<List<UserDTO>, BaseError>>(ResultResponse.Idle)
    val users: StateFlow<ResultResponse<List<UserDTO>, BaseError>> = _users.asStateFlow()

    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors.asStateFlow()

    private var currentPage = 1
    private var totalPages = 1
    private val userCache = mutableListOf<UserDTO>()

    init {
        loadLastPageUsers()
    }

    private fun loadUsers(page: Int) {
        viewModelScope.launch {
            repository.getUsers(page)
                .catch { e -> _users.value = ResultResponse.Error(DataError.Network.UNKNOWN) }
                .collect { userResponse ->
                    when (userResponse) {
                        is ResultResponse.Loading -> _users.value = ResultResponse.Loading
                        is ResultResponse.Success -> {
                            userCache.addAll(userResponse.data.users)
                            _users.value = ResultResponse.Success(userCache)
                            totalPages = userResponse.data.totalPages
                            currentPage = userResponse.data.currentPage
                        }
                        is ResultResponse.Error -> _users.value = userResponse
                        else -> {}
                    }
                }
        }
    }

    private fun loadLastPageUsers() {
        viewModelScope.launch {
            repository.getUsers(1)
                .collect { userResponse ->
                    if (userResponse is ResultResponse.Success) {
                        totalPages = userResponse.data.totalPages
                        loadUsers(totalPages)
                    } else {
                        _users.value = ResultResponse.Error(DataError.Network.UNKNOWN)
                    }
                }
        }
    }

    fun loadMoreUsers() {
        if (currentPage < totalPages && !_users.value.isLoading()) {
            loadUsers(currentPage + 1)
        }
    }

    fun loadPreviousUsers() {
        if (currentPage > 1 && !_users.value.isLoading()) {
            loadUsers(currentPage - 1)
        }
    }

    suspend fun createUser(name: String, email: String, gender: String, status: String): Boolean {
        var isSuccess = false
        try {
            repository.createUser(UserRequest(name, email, gender, status))
                .onStart {
                    _users.value = ResultResponse.Loading
                    _validationErrors.value = emptyMap()
                }
                .catch { e ->
                    val errorResponse = e.message?.let { parseErrorResponse(it) }
                    _validationErrors.value = errorResponse ?: emptyMap()
                    _users.value = ResultResponse.Error(DataError.Network.UNKNOWN)
                }
                .collect { response ->
                    when (response) {
                        is ResultResponse.Success -> {
                            userCache.add(response.data)
                            _users.value = ResultResponse.Success(userCache)
                            isSuccess = true
                        }
                        is ResultResponse.Error -> _users.value = response
                        else -> {}
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
                .onStart { _users.value = ResultResponse.Loading }
                .catch { e -> _users.value = ResultResponse.Error(DataError.Network.UNKNOWN) }
                .collect { response ->
                    when (response) {
                        is ResultResponse.Success -> {
                            if (response.data) {
                                userCache.remove(user)
                                _users.value = ResultResponse.Success(userCache)
                            }
                        }
                        is ResultResponse.Error -> _users.value = response
                        else -> {}
                    }
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
