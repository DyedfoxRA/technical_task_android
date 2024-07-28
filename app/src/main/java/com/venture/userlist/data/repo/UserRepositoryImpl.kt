package com.venture.userlist.data.repo

import com.venture.userlist.domain.repo.UserRepository
import com.venture.userlist.data.model.UserDTO
import com.venture.userlist.data.model.UserRequest
import com.venture.userlist.data.service.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

data class UserResponse(
    val users: List<UserDTO>,
    val totalPages: Int,
    val currentPage: Int
)

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {
    override fun getUsers(page: Int): Flow<UserResponse> = flow {
        val response = apiService.getUsers(page)
        if (response.isSuccessful) {
            val headers = response.headers()
            val totalPages = headers["X-Pagination-Pages"]?.toInt() ?: 1
            val currentPage = headers["X-Pagination-Page"]?.toInt() ?: 1
            response.body()?.let {
                emit(UserResponse(it, totalPages, currentPage))
            }
        } else {
            throw Exception("Failed to load users")
        }
    }

    override fun createUser(user: UserRequest): Flow<Response<UserDTO>> = flow {
        emit(apiService.createUser(user))
    }

    override fun deleteUser(id: Int): Flow<Boolean> = flow {
        val response = apiService.deleteUser(id)
        emit(response.isSuccessful)
    }
}
