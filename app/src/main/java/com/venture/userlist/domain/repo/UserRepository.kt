package com.venture.userlist.domain.repo

import com.venture.userlist.data.model.UserDTO
import com.venture.userlist.data.model.UserRequest
import com.venture.userlist.data.repo.UserResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface UserRepository {
    fun getUsers(page: Int): Flow<UserResponse>
    fun createUser(user: UserRequest): Flow<Response<UserDTO>>
    fun deleteUser(id: Int): Flow<Boolean>
}
