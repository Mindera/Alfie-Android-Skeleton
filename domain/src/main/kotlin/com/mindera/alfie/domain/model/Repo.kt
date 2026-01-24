package com.mindera.alfie.domain.model

/**
 * Domain model representing a repository.
 * Independent of data layer implementation details.
 */
data class Repo(
    val id: Int,
    val name: String,
    val fullName: String,
    val url: String,
    val stars: Int,
    val description: String?
)
