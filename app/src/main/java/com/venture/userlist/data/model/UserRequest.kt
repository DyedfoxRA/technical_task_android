package com.venture.userlist.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val name: String,
    val email: String,
    val gender: String,
    val status: String
)
