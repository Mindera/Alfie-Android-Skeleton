package com.mindera.alfie.networking

import com.mindera.alfie.networking.model.GitHubRepo
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiService {
    @GET("orgs/{org}/repos")
    suspend fun getOrgRepos(
        @Path("org") org: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int,
        @Query("sort") sort: String = "updated",
        @Query("direction") direction: String = "desc"
    ): List<GitHubRepo>
}
