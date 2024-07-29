package com.venture.userlist.data.repo

import com.venture.userlist.domain.repo.UserRepository
import com.venture.userlist.data.model.UserDTO
import com.venture.userlist.data.model.UserRequest
import com.venture.userlist.data.service.ApiService
import com.venture.userlist.domain.results.BaseError
import com.venture.userlist.domain.results.DataError
import com.venture.userlist.domain.results.ResultResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
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
    override fun getUsers(page: Int): Flow<ResultResponse<UserResponse, BaseError>> = flow {
        emit(ResultResponse.Loading)
        try {
            val response = apiService.getUsers(page)
            if (response.isSuccessful) {
                val headers = response.headers()
                val totalPages = headers["X-Pagination-Pages"]?.toInt() ?: 1
                val currentPage = headers["X-Pagination-Page"]?.toInt() ?: 1
                response.body()?.let {
                    emit(ResultResponse.Success(UserResponse(it, totalPages, currentPage)))
                } ?: emit(ResultResponse.Error(DataError.Network.UNKNOWN))
            } else {
                emit(ResultResponse.Error(DataError.Network.UNKNOWN))
            }
        } catch (e: HttpException) {
            emit(handleHttpException(e))
        } catch (e: Exception) {
            emit(ResultResponse.Error(DataError.Network.UNKNOWN))
        }
    }

    override fun createUser(user: UserRequest): Flow<ResultResponse<UserDTO, BaseError>> = flow {
        emit(ResultResponse.Loading)
        try {
            val response = apiService.createUser(user)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(ResultResponse.Success(it))
                } ?: emit(ResultResponse.Error(DataError.Network.UNKNOWN))
            } else {
                emit(ResultResponse.Error(DataError.Network.UNKNOWN))
            }
        } catch (e: HttpException) {
            emit(handleHttpException(e))
        } catch (e: Exception) {
            emit(ResultResponse.Error(DataError.Network.UNKNOWN))
        }
    }

    override fun deleteUser(id: Int): Flow<ResultResponse<Boolean, BaseError>> = flow {
        emit(ResultResponse.Loading)
        try {
            val response = apiService.deleteUser(id)
            emit(ResultResponse.Success(response.isSuccessful))
        } catch (e: HttpException) {
            emit(handleHttpException(e))
        } catch (e: Exception) {
            emit(ResultResponse.Error(DataError.Network.UNKNOWN))
        }
    }

    private fun handleHttpException(e: HttpException): ResultResponse.Error<DataError.Network> {
        return when (e.code()) {
            400 -> ResultResponse.Error(DataError.Network.BAD_REQUEST)
            401 -> ResultResponse.Error(DataError.Network.UNAUTHORIZED)
            403 -> ResultResponse.Error(DataError.Network.FORBIDDEN)
            404 -> ResultResponse.Error(DataError.Network.NOT_FOUND)
            408 -> ResultResponse.Error(DataError.Network.REQUEST_TIMEOUT)
            413 -> ResultResponse.Error(DataError.Network.PAYLOAD_TOO_LARGE)
            429 -> ResultResponse.Error(DataError.Network.TOO_MANY_REQUESTS)
            500 -> ResultResponse.Error(DataError.Network.SERVER_ERROR)
            else -> ResultResponse.Error(DataError.Network.UNKNOWN)
        }
    }
}
