package com.venture.userlist.domain.repo

import com.venture.userlist.data.model.UserDTO
import com.venture.userlist.data.model.UserRequest
import com.venture.userlist.data.repo.UserResponse
import com.venture.userlist.domain.results.BaseError
import com.venture.userlist.domain.results.ResultResponse
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(page: Int): Flow<ResultResponse<UserResponse, BaseError>>
    fun createUser(user: UserRequest): Flow<ResultResponse<UserDTO, BaseError>>
    fun deleteUser(id: Int): Flow<ResultResponse<Boolean, BaseError>>
}