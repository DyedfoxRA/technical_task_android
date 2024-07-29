package com.venture.userlist.data.service

import com.venture.userlist.data.model.UserDTO
import com.venture.userlist.data.model.UserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("public/v2/users")
    suspend fun getUsers(@Query("page") page: Int): Response<List<UserDTO>>

    @POST("public/v2/users")
    suspend fun createUser(@Body createUserRequest: UserRequest): Response<UserDTO>

    @DELETE("public/v2/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>
}