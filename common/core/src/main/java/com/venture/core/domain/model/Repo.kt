package com.venture.core.domain.model

data class Repo(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val htmlUrl: String,
    val stargazersCount: Int,
    val language: String?,
    val owner: Owner,
    val forksCount: Int
)