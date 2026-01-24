package com.mindera.alfie.networking.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubRepo(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "full_name")
    val fullName: String,
    @Json(name = "html_url")
    val htmlUrl: String,
    @Json(name = "stargazers_count")
    val stargazersCount: Int,
    @Json(name = "description")
    val description: String?
)
