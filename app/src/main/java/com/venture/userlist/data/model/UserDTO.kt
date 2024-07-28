package com.venture.userlist.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Int,
    val name: String,
    val email: String,
    val gender: String,
    val status: String,
)