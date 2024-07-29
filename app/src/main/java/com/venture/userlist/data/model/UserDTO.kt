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

// here I leave this data class and I am using this on every layer , but we need mapper here to use
// data class from domain layer